package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.EmpresaDTO;
import com.portal.dto.PerfilDTO;
import com.portal.dto.UsuarioDTO;

public class PDUsuario extends PDAbstract {

	private static final String SQL_ADD_USUARIO = "  insert into sec_usuario( "
			+ " user_name, user_password, user_nombres,"
			+ " user_apellidos, user_email, cod_estado, log_insertdate, log_insertuser) "
			+ "	values( " + "?,?,?,?,?,1,current_date,?) ";

	private static final String SQL_INSERT_USUARIO_PERFIL = " insert into sec_usuario_perfil(user_name,cod_perfil,log_insertdate,log_insertuser) "
			+ " values(?,?,current_timestamp,?)";

	private static final String SQL_INSERT_USUARIO_EMPRESA = " insert into sec_usuario_empresa (user_name,cod_empresa,log_insertdate,log_insertuser)"
			+ " values(?,?,current_timestamp,?)";

	private static final String SQL_BUSCAR_USUARIO = " select count(user_name) "
			+ " from sec_usuario  " + " where user_name = ? ";

	private static final String SQL_UPTATE_USUARIO = " update sec_usuario "
			+ " set user_nombres = ?,  " + "  user_apellidos = ?, "
			+ "  user_email = ?,  " + "  log_updatedate = current_date, "
			+ "  log_updateuser = ? " + "  where user_name = ? ";

	private static final String SQL_GET_USUARIOS = "	SELECT user_name, "
			+ "  user_password, "
			+ "  user_nombres, "
			+ "  user_apellidos, user_email,"
			+ "  case cod_estado when 1 then 'ACTIVO' else 'INACTIVO' end as user_estado, "
			+ "  user_ultimo_login " + "FROM sec_usuario  ";

	private static final String SQL_GET_USUARIO_PERFIL = " select * from sec_usuario_perfil "
			+ "  where user_name = ? ";

	private static final String SQL_GET_USUARIO_EMPRESAS = "SELECT emp.* "
			+ "	FROM sec_usuario_empresa use " + "INNER JOIN pfd_empresa emp "
			+ "	ON use.cod_empresa  = emp.cod_empresa "
			+ "	WHERE use.user_name = ? ";

	private static final String SQL_INHABILITAR_USUARIO = " update sec_usuario "
			+ " set cod_estado = ? " + " where user_name = ? ";

	private static final String SQL_RESETEAR_PASSWORD = " update sec_usuario "
			+ " set user_password = ? " + " where user_name = ? ";

	private static final String SQL_ORDER_BY_USUARIO = "  order by user_name asc ";

	private static final String SQL_WHERE_FILTRO_USERNAME = " where user_name like ? ";

	private static final String SQL_WHERE_FILTRO_USUARIO_NOMBRES = " where user_nombres like ? ";

	private static final String SQL_DELETE_USUARIO_PERFIL = " delete from sec_usuario_perfil where user_name = ? ";
	private static final String SQL_DELETE_USUARIO_EMPRESAS = "delete from sec_usuario_empresa where user_name = ? ";

	public PDUsuario() {
		log = Logger.getLogger(getClass());
	}

	public boolean addUsuario(Connection portal, String userName,
			String userPass, String userNombres, String userApellidos,
			String userEmail, String userLogInsertUser, String codPerfil,
			List<EmpresaDTO> empresas) throws SQLException {

		boolean resultado = false;

		log.debug("Agregando nuevo usuario: " + userName);

		int addUsuario = dml(portal, SQL_ADD_USUARIO, new Object[] { userName,
				userPass, userNombres, userApellidos, userEmail,
				userLogInsertUser });

		if (addUsuario > 0) {
			int addPerfil = dml(portal, SQL_INSERT_USUARIO_PERFIL,
					new Object[] { userName, Long.parseLong(codPerfil),
							userLogInsertUser });

			if (addPerfil > 0) {
				for (EmpresaDTO empresa : empresas) {
					if (dml(portal, SQL_INSERT_USUARIO_EMPRESA, new Object[] {
							userName, Long.parseLong(empresa.getCod_empresa()),
							userLogInsertUser }) == 0) {
						return false;
					}
				}

				resultado = true;
			}
		}

		return resultado;

	}

	public boolean buscarUsuario(Connection portal, String userName)
			throws SQLException {
		log.debug("buscando usuario: " + userName);

		return count(portal, SQL_BUSCAR_USUARIO, new Object[] { userName }) > 0;
	}

	public boolean editarUsuario(Connection portal, String userName,
			String userNombres, String userApellidos, String userEmail,
			String logUpdateUser, String codPerfil, List<EmpresaDTO> empresas)
			throws SQLException {
		log.debug("actualizando usuario: " + userName);

		dml(portal, SQL_DELETE_USUARIO_PERFIL,
				new Object[] {userName});
		dml(portal, SQL_DELETE_USUARIO_EMPRESAS,
				new Object[] {userName});

		int addPerfil = dml(portal, SQL_INSERT_USUARIO_PERFIL, new Object[] {
				userName, Long.parseLong(codPerfil), logUpdateUser });

		if (addPerfil > 0) {
			for (EmpresaDTO empresa : empresas) {
				if (dml(portal, SQL_INSERT_USUARIO_EMPRESA, new Object[] {
						userName, Long.parseLong(empresa.getCod_empresa()),
						logUpdateUser }) == 0) {
					return false;
				}
			}

			return dml(portal, SQL_UPTATE_USUARIO, new Object[] { userNombres,
					userApellidos, userEmail, logUpdateUser, userName }) > 0;
		}

		return false;

	}

	public boolean inhabilitarUsuario(Connection portal, String userName)
			throws SQLException {
		log.debug("Inhabilitando usuario: " + userName);

		return dml(portal, SQL_INHABILITAR_USUARIO,
				new Object[] { 0, userName }) > 0;
	}

	public boolean habilitarUsuario(Connection portal, String userName)
			throws SQLException {
		log.debug("Habilitando usuario: " + userName);

		return dml(portal, SQL_INHABILITAR_USUARIO,
				new Object[] { 1, userName }) > 0;
	}

	public boolean resetearPassword(Connection portal, String userName,
			String userPassword) throws SQLException {
		log.debug("resetenado password para el usuario: " + userName);

		return dml(portal, SQL_RESETEAR_PASSWORD, new Object[] { userPassword,
				userName }) > 0;
	}

	public List<UsuarioDTO> getUsuarios(Connection portal) throws SQLException {
		log.debug("obteniendo usuarios...");

		return consultaLista(portal, SQL_GET_USUARIOS + SQL_ORDER_BY_USUARIO,
				UsuarioDTO.class, null);
	}

	public List<UsuarioDTO> getUsuariosFiltrados(Connection portal,
			String userName, String userNombre) throws SQLException {
		log.debug("obteniendo usuarios...");

		String sql = SQL_GET_USUARIOS;
		Object[] params = null;

		if (userName != null && !userName.equalsIgnoreCase("")) {
			sql += SQL_WHERE_FILTRO_USERNAME + SQL_ORDER_BY_USUARIO;
			params = new Object[] { "%" + userName + "%" };
		} else if (userNombre != null && userNombre.equalsIgnoreCase("")) {
			sql += SQL_WHERE_FILTRO_USUARIO_NOMBRES + SQL_ORDER_BY_USUARIO;
			params = new Object[] { "%" + userNombre + "%" };
		}

		return consultaLista(portal, sql, UsuarioDTO.class, params);
	}

	public PerfilDTO getUsuarioPerfil(Connection conn, String userName)
			throws SQLException {

		return consultaDTO(conn, SQL_GET_USUARIO_PERFIL, PerfilDTO.class,
				new Object[] { userName });
	}

	public List<EmpresaDTO> getUsuarioEmpresas(Connection conn, String userName)
			throws SQLException {

		return consultaLista(conn, SQL_GET_USUARIO_EMPRESAS, EmpresaDTO.class,
				new Object[] { userName });
	}
}
