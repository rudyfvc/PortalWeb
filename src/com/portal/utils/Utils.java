package com.portal.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.portal.security.Node;
import com.portal.security.SecPerfilOpcion;
import com.portal.security.SecUsuario;

public class Utils {

	private static final Logger log = Logger.getLogger(Utils.class);
	public static final String NAME_CONN_PORTAL = "PORTAL";

	public static final Connection getConnection() throws NamingException,
			SQLException {

		return getConexion(NAME_CONN_PORTAL);

	}

	public static final void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.debug("Error cerrando conexión: " + conn.toString(), e);
			}
		}
	}

	public static final void commit(Connection conn) {
		if (conn != null) {
			try {
				conn.commit();
			} catch (SQLException e) {
				log.debug(
						"Error realizando commit conexión: " + conn.toString(),
						e);
			}
		}
	}

	public static final void rollback(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				log.debug(
						"Error realizando rollback conexión: "
								+ conn.toString(), e);
			}
		}
	}

	private static Connection getConexion(String nombre)
			throws NamingException, SQLException {

		Context ctx = new InitialContext();
		return ((DataSource) ctx.lookup("java:comp/env/" + nombre))
				.getConnection();
	}

	public static final String getUrlLogin() {

		return "http://localhost:8080/PortalWeb/";
	}

	private static Map<String, Node> hm = new HashMap<String, Node>();

	public static Node createTree(SecUsuario user) {
		Node treeRootNode = new Node(null);
		treeRootNode.setId("root");

		if (user != null) {
			List<SecPerfilOpcion> opciones = user.getPerfil()
					.getOpcionesPerfil();

			Iterator<SecPerfilOpcion> it = opciones.iterator();

			while (it.hasNext()) {
				SecPerfilOpcion opc = it.next();
				if (opc.getCod_aplicacion_opcion_padre().equalsIgnoreCase("0")) {
					Node childNode = addChild(treeRootNode,
							opc.getCod_aplicacion_opcion(),
							opc.getOpc_nombre(), opc.getOpc_url(),
							opc.getOpc_image());
					hm.put(opc.getCod_aplicacion_opcion(), childNode);
				} else {
					Node parentNode = hm.get(opc
							.getCod_aplicacion_opcion_padre());
					Node ch = addChild(parentNode,
							opc.getCod_aplicacion_opcion(),
							opc.getOpc_nombre(), opc.getOpc_url(),
							opc.getOpc_image());
					hm.put(opc.getCod_aplicacion_opcion(), ch);
				}
			}
		}

		return treeRootNode;
	}

	private static Node addChild(Node parent, String id, String nombre,
			String url, String image) {
		Node node = new Node(parent);
		node.setId(id);
		node.setNombre(nombre);
		node.setUrl(url);
		node.setImage(image);
		parent.getChildren().add(node);

		return node;
	}

	public static String getSystemDate() {
		Calendar calendar = Calendar.getInstance();
		String month = "";
		switch (calendar.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
			month = "Enero";
			break;
		case Calendar.FEBRUARY:
			month = "Febrero";
			break;
		case Calendar.MARCH:
			month = "Marzo";
			break;
		case Calendar.APRIL:
			month = "Abril";
			break;
		case Calendar.MAY:
			month = "Mayo";
			break;
		case Calendar.JUNE:
			month = "Junio";
			break;
		case Calendar.JULY:
			month = "Julio";
			break;
		case Calendar.AUGUST:
			month = "Agosto";
			break;
		case Calendar.SEPTEMBER:
			month = "Septiembre";
			break;
		case Calendar.OCTOBER:
			month = "Octubre";
			break;
		case Calendar.NOVEMBER:
			month = "Noviembre";
			break;
		case Calendar.DECEMBER:
			month = "Diciembre";
			break;
		default:
			break;
		}
		return calendar.get(Calendar.DATE) + " de " + month + " de "
				+ calendar.get(Calendar.YEAR);
	}

	public static final double round(double v) {

		return Math.round(v * 100) / 100;

	}

	public static final boolean isNumeric(String v) {
		boolean resultado = false;

		try {
			Long.parseLong(v);
			resultado = true;
		} catch (Exception e) {
			log.debug("no es número..");
		}

		return resultado;

	}

	public static String getPeriodo(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int mes = calendar.get(Calendar.MONTH) + 1;
		int anio = calendar.get(Calendar.YEAR);

		String periodo = "";

		if (mes < 10) {
			periodo = "0" + mes + "-" + anio;
		} else {
			periodo = mes + "-" + anio;
		}

		return periodo;
	}

	public static Date getDate(String fecha) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat();

		return sdf.parse(fecha);

	}

	public static Date getDate(String fecha, String formato)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(formato);

		return sdf.parse(fecha);

	}

	public static boolean isEmail(String email) {
		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		Pattern pattern = Pattern.compile(regex);

		Matcher matcher = pattern.matcher((CharSequence) email);

		boolean valide = matcher.matches();

		log.debug("email válido: " + valide);

		return valide;
	}

	public static boolean isNIT(String nit) {

		return true;
	}

	private static final String PATTERN_CUI = "^[0-9]{4}\\s?[0-9]{5}\\s?[0-9]{4}$";

	public static Boolean ValidaCUI(String Cui) {
		// SE inicializar el booleano
		boolean valido = true;

		// Acá se compara la expresión regular con el string ingresado
		Pattern pattern = Pattern.compile(PATTERN_CUI);
		// Match the given input against this pattern
		Matcher matcher = pattern.matcher(Cui);
		boolean validCui = matcher.matches();

		// Se reemplazan los espacios en blanco en la cadena (si tiene)
		Cui = Cui.replace(" ", "");

		// Si la cadena cumple con la expresion regular debemos de verificar que
		// sea un CUI válido
		if (validCui == true) {

			// Extraemos el numero del DPI
			String no = Cui.substring(0, 8);
			// Extraemos el numero de Departamento
			int depto = Integer.parseInt(Cui.substring(9, 11));
			// Extraemos el numero de Municipio
			int muni = Integer.parseInt(Cui.substring(11, 13));

			// Se extra el numero validador
			int ver = Integer.parseInt(Cui.substring(8, 9));

			// Array con la cantidad de municipios que contiene cada
			// departamento.

			int munisPorDepto[] = {
			/* 01 - Guatemala tiene: */17 /* municipios. */,
			/* 02 - El Progreso tiene: */8 /* municipios. */,
			/* 03 - Sacatepéquez tiene: */16 /* municipios. */,
			/* 04 - Chimaltenango tiene: */16 /* municipios. */,
			/* 05 - Escuintla tiene: */13 /* municipios. */,
			/* 06 - Santa Rosa tiene: */14 /* municipios. */,
			/* 07 - Sololá tiene: */19 /* municipios. */,
			/* 08 - Totonicapán tiene: */8 /* municipios. */,
			/* 09 - Quetzaltenango tiene: */24 /* municipios. */,
			/* 10 - Suchitepéquez tiene: */21 /* municipios. */,
			/* 11 - Retalhuleu tiene: */9 /* municipios. */,
			/* 12 - San Marcos tiene: */30 /* municipios. */,
			/* 13 - Huehuetenango tiene: */32 /* municipios. */,
			/* 14 - Quiché tiene: */21 /* municipios. */,
			/* 15 - Baja Verapaz tiene: */8 /* municipios. */,
			/* 16 - Alta Verapaz tiene: */17 /* municipios. */,
			/* 17 - Petén tiene: */14 /* municipios. */,
			/* 18 - Izabal tiene: */5 /* municipios. */,
			/* 19 - Zacapa tiene: */11 /* municipios. */,
			/* 20 - Chiquimula tiene: */11 /* municipios. */,
			/* 21 - Jalapa tiene: */7 /* municipios. */,
			/* 22 - Jutiapa tiene: */17 /* municipios. */
			};

			// Verificamos que no se haya ingresado 0 en la posicion de depto o
			// municipio
			if ((muni == 0 || depto == 0) || (muni == 0 && depto == 0)) {
				valido = false;
				log.debug("CUI no válido, el depto y municipio no pueden ser 0");
			}

			else {
				// Si el numero de depto ingresado en la cadena es mayor 22 es
				// cui invalido
				log.debug("munixdepto: " + munisPorDepto.length);
				if (depto > munisPorDepto.length) {
					valido = false;
					log.debug("CUI no válido Departamento fuera de rango");
				} else {
					// si depto es menor o igual a 22

					System.out.println("Municipios maximos: "
							+ munisPorDepto[depto - 1]);
					// se valida que el municipio ingresado en la cadena este
					// dentro del rango del depto
					if (muni > munisPorDepto[depto - 1]) {
						valido = false;
						log.debug("CUI no válido municipio fuera de rango");
					}

					else {

						// si es valido
						int total = 0;
						// Se realiza la siguiente Ooperación
						for (int i = 0; i < no.length(); i++) {
							System.out.println("-" + no.substring(i, i + 1));
							total += (Integer.parseInt(no.substring(i, i + 1)))
									* (i + 2);
						}

						// al total de la anterior operación se le saca el mod
						// 11

						int modulo = total % 11;
						log.debug("cui con modulo" + modulo);

						// Si el mod es igual al numero verificador el cui es
						// valido , sino es invalido
						if (modulo != ver) {
							valido = false;
						}
					}
				}
			}

		}

		else {
			log.debug("CUI no válido, longitud no válida");
			valido = false;
		}

		// se retorna el booleano que indica si el cui es válido o no
		return valido;
	}

}
