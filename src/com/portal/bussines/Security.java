package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.portal.dto.AplicacionOpcionDTO;
import com.portal.security.Node;
import com.portal.security.SecEmpresa;
import com.portal.security.SecPerfil;
import com.portal.security.SecPerfilOpcion;
import com.portal.security.SecUsuario;
import com.portal.utils.SecurityPortal;

public class Security extends PDAbstract {

	public Security() {
		log = Logger.getLogger(getClass());
	}

	private static final String SQL_GET_USUARIO_EMPRESA = "	SELECT emp.* "
			+ "			 	FROM sec_usuario_empresa usuario "
			+ "			 	INNER JOIN pfd_empresa emp "
			+ "	 	ON usuario.cod_empresa = emp.cod_empresa "
			+ "	 	WHERE usuario.user_name  = ? ";

	private static final String SQL_GET_USUARIO = " select user_name, user_nombres, user_apellidos, "
			+ "  case cod_estado when 1 then 'ACTIVO' else 'INACTIVO' end as user_estado, "
			+ " user_email "
			+ "  from sec_usuario  "
			+ " where user_name = ?  " + "  and user_password = ?  ";

	private static final String SQL_GET_PERFIL_USUARIO = " select per.cod_perfil, per.per_nombre "
			+ "  from sec_usuario_perfil usep "
			+ "   inner join sec_perfil per "
			+ "  on usep.cod_perfil = per.cod_perfil "
			+ "   where user_name = ? ";

	private static final String SQL_GET_PERFIL_OPCIONES = "WITH recursive vista(cod_aplicacion_opcion,cod_aplicacion_opcion_padre,opc_nombre,cod_perfil,opc_url,opc_image) AS "
			+ "  (SELECT cod_aplicacion_opcion, "
			+ "    cod_aplicacion_opcion_padre, "
			+ "    opc_nombre, "
			+ "    cod_perfil, "
			+ "    opc_url,opc_image "
			+ "  FROM view_permisos "
			+ "  WHERE cod_aplicacion_opcion_padre = 0 "
			+ "  UNION "
			+ "  SELECT permisos.cod_aplicacion_opcion, "
			+ "    permisos.cod_aplicacion_opcion_padre, "
			+ "    permisos.opc_nombre, "
			+ "    permisos.cod_perfil , "
			+ "    permisos.opc_url,permisos.opc_image "
			+ "  FROM view_permisos AS permisos, "
			+ "    vista            AS padres "
			+ "  WHERE permisos.cod_aplicacion_opcion_padre = padres.cod_aplicacion_opcion "
			+ "  ) "
			+ "SELECT * FROM vista WHERE cod_perfil = CAST(? AS NUMERIC) order by cod_aplicacion_opcion";

	private static final String SQL_GET_ALL_OPCIONES = "WITH recursive vista(cod_aplicacion_opcion,cod_aplicacion_opcion_padre, opc_url,opc_tipo,opc_nombre,opc_nombre_desplegar,opc_image) AS "
			+ "  (SELECT cod_aplicacion_opcion, "
			+ "    cod_aplicacion_opcion_padre, "
			+ "    opc_url, "
			+ "    opc_tipo, "
			+ "    opc_nombre, "
			+ "    opc_nombre_desplegar, "
			+ "    opc_image "
			+ "  FROM sec_aplicacion_opcion "
			+ "  WHERE opc_tipo                  = 'MENU' "
			+ "  AND cod_aplicacion_opcion_padre = 0 "
			+ "  UNION "
			+ "  SELECT hijos.cod_aplicacion_opcion, "
			+ "    hijos.cod_aplicacion_opcion_padre, "
			+ "    hijos.opc_url, "
			+ "    hijos.opc_tipo, "
			+ "    hijos.opc_nombre, "
			+ "    hijos.opc_nombre_desplegar, "
			+ "    hijos.opc_image "
			+ "  FROM sec_aplicacion_opcion AS hijos, "
			+ "    vista                    AS padres "
			+ "  WHERE hijos.cod_aplicacion_opcion_padre = padres.cod_aplicacion_opcion "
			+ "  ) " + "SELECT * FROM vista ";

	private static final String SQL_GET_OPCIONES_PERFIL_EDITAR = "SELECT * "
			+ "FROM "
			+ "  (SELECT opciones.cod_aplicacion, "
			+ "    opciones.cod_aplicacion_opcion, "
			+ "    opciones.cod_aplicacion_opcion_padre, "
			+ "    opciones.opc_url, "
			+ "    opciones.opc_tipo, "
			+ "    opciones.opc_nombre, "
			+ "    opciones.opc_nombre_desplegar, "
			+ "    opciones.opc_image, "
			+ "    CASE "
			+ "      WHEN asignadas.cod_aplicacion_opcion IS NULL "
			+ "      THEN 'false' "
			+ "      ELSE 'true' "
			+ "    END opc_selected "
			+ "  FROM sec_aplicacion_opcion opciones "
			+ "  LEFT OUTER JOIN sec_perfil_opcion asignadas "
			+ "  ON opciones.cod_aplicacion_opcion          = asignadas.cod_aplicacion_opcion "
			+ "  AND asignadas.cod_perfil                   = ? "
			+ "  WHERE opciones.cod_aplicacion_opcion_padre = 0 "
			+ "  UNION "
			+ "  SELECT * "
			+ "  FROM "
			+ "    (SELECT opciones.cod_aplicacion, "
			+ "      opciones.cod_aplicacion_opcion, "
			+ "      opciones.cod_aplicacion_opcion_padre, "
			+ "      opciones.opc_url, "
			+ "      opciones.opc_tipo, "
			+ "      opciones.opc_nombre, "
			+ "      opciones.opc_nombre_desplegar, "
			+ "      opciones.opc_image, "
			+ "      CASE "
			+ "        WHEN asignadas.cod_aplicacion_opcion IS NULL "
			+ "        THEN 'false' "
			+ "        ELSE 'true' "
			+ "      END opc_selected "
			+ "    FROM sec_aplicacion_opcion opciones "
			+ "    LEFT OUTER JOIN sec_perfil_opcion asignadas "
			+ "    ON opciones.cod_aplicacion_opcion = asignadas.cod_aplicacion_opcion "
			+ "    AND asignadas.cod_perfil          = ? "
			+ "    ) AS hijos "
			+ "  ) AS consulta "
			+ "ORDER BY consulta.cod_aplicacion_opcion_padre, consulta.cod_aplicacion_opcion ";

	private static Map<String, Node> hm = new HashMap<String, Node>();

	public SecUsuario validarLogin(Connection conn, String userName,
			String userPassword) throws SQLException {

		SecUsuario user = consultaDTO(
				conn,
				SQL_GET_USUARIO,
				SecUsuario.class,
				new Object[] { userName,
						SecurityPortal.getHmacSHA1(userPassword) });

		if (user != null) {
			SecPerfil perfil = consultaDTO(conn, SQL_GET_PERFIL_USUARIO,
					SecPerfil.class, new Object[] { user.getUser_name() });

			if (perfil != null) {
				List<SecPerfilOpcion> opciones = consultaLista(conn,
						SQL_GET_PERFIL_OPCIONES, SecPerfilOpcion.class,
						new Object[] { perfil.getCod_perfil() });

				perfil.setOpcionesPerfil(opciones);
			}

			List<SecEmpresa> empresas = consultaLista(conn,
					SQL_GET_USUARIO_EMPRESA, SecEmpresa.class,
					new Object[] { user.getUser_name() });

			PDPeriodo objPeriodo = new PDPeriodo(userName);

			for (SecEmpresa empresa : empresas) {
				empresa.setPeriodo(objPeriodo.getPeriodoAbierto(conn,
						Long.parseLong(empresa.getCod_empresa())));
			}

			user.setPerfil(perfil);
			user.setEmpresas(empresas);

		}

		return user;

	}

	public Node getOpciones(Connection conn, String codPerfil)
			throws SQLException {

		List<AplicacionOpcionDTO> opciones = consultaLista(
				conn,
				SQL_GET_OPCIONES_PERFIL_EDITAR,
				AplicacionOpcionDTO.class,
				new Object[] { Long.valueOf(codPerfil), Long.valueOf(codPerfil) });

		Node tree = createTree(opciones);

		return tree;
	}

	public Node getAllOpciones(Connection conn) throws SQLException {

		List<AplicacionOpcionDTO> opciones = consultaLista(conn,
				SQL_GET_ALL_OPCIONES, AplicacionOpcionDTO.class, null);

		Node tree = createTree(opciones);

		return tree;
	}

	public static Node createTree(List<AplicacionOpcionDTO> opciones) {
		Node treeRootNode = new Node(null);
		treeRootNode.setId("root");

		Iterator<AplicacionOpcionDTO> it = opciones.iterator();

		while (it.hasNext()) {
			AplicacionOpcionDTO opc = it.next();
			if (opc.getCod_aplicacion_opcion_padre().equalsIgnoreCase("0")) {
				Node childNode = addChild(treeRootNode,
						opc.getCod_aplicacion_opcion(), opc.getOpc_nombre(),
						opc.getOpc_url(), opc.getOpc_image(),
						opc.isOpc_selected());

				hm.put(opc.getCod_aplicacion_opcion(), childNode);
			} else {
				Node parentNode = hm.get(opc.getCod_aplicacion_opcion_padre());
				Node ch = addChild(parentNode, opc.getCod_aplicacion_opcion(),
						opc.getOpc_nombre(), opc.getOpc_url(),
						opc.getOpc_image(), opc.isOpc_selected());

				hm.put(opc.getCod_aplicacion_opcion(), ch);
			}
		}
		return treeRootNode;
	}

	private static Node addChild(Node parent, String id, String nombre,
			String url, String image, boolean selected) {
		Node node = new Node(parent);
		node.setId(id);
		node.setNombre(nombre);
		node.setUrl(url);
		node.setImage(image);
		node.setSelected(selected);

		parent.getChildren().add(node);

		return node;
	}

}
