package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;

public class PDConfiguracion extends PDAbstract {

	public static final String CONF_PATH_FILE_SCAN = "PATH_FILE_SCAN";
	public static final String CONF_CANTIDAD_CORRELATIVOS = "CANT_MAX_CORRELATIVOS";

	private static final String SQL_GET_CONF = " select conf_valor from pd_configuracion where conf_clave = ? ";

	public String getValorConf(Connection conn, String confClave)
			throws SQLException {

		return consultaEscalar(conn, SQL_GET_CONF, String.class,
				new Object[] { confClave });
	}

}

