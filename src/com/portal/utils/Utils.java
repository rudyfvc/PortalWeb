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

}
