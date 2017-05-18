package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.TipoEntidadDTO;

public class PDTipoEntidad extends PDAbstract {

	private static final String SQL_GET_ENTIDAD = "select cod_tipo_entidad,descripcion as tipo_entidad, estado "
			+ " from pd_tipo_entidad  ";

	private static final String SQL_WHERE_ESTADO_TIPO_ENTIDAD = " where estado = ? ";
	private static final String SQL_ORDER_BY_CODIGO = " order by descripcion ";

	public PDTipoEntidad(String usuario) {
		super(usuario);
		log = Logger.getLogger(getClass());
	}

	public List<TipoEntidadDTO> getAllEntidades(Connection conn)
			throws SQLException {
		String sql = SQL_GET_ENTIDAD + SQL_ORDER_BY_CODIGO;

		return consultaLista(conn, sql, TipoEntidadDTO.class, null);
	}

	public List<TipoEntidadDTO> getTipoEntidadesPorEstado(Connection conn,
			long estado) throws SQLException {
		String sql = SQL_GET_ENTIDAD + SQL_WHERE_ESTADO_TIPO_ENTIDAD
				+ SQL_ORDER_BY_CODIGO;

		return consultaLista(conn, sql, TipoEntidadDTO.class,
				new Object[] { estado });
	}
}
