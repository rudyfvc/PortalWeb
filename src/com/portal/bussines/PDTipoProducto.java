package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.TipoProductoDTO;

public class PDTipoProducto extends PDAbstract {

	private static final String SQL_INSERT_TIPO_PRODUCTO = " insert into pd_tipo_producto(cod_tipo_producto, tipo_producto,"
			+ "estado,"
			+ " log_insertdate,log_insertuser) values(nextval('PD_TIPO_PRODUCTO_SQ'),?,1,current_timestamp,?)";

	private static final String SQL_GET_TIPO_PRODUCTO = " select cod_tipo_producto, tipo_producto, estado, log_insertdate, log_insertuser,log_updatedate,log_updateuser "
			+ " from pd_tipo_producto " + "  order by tipo_producto ";

	private static final String SQL_GET_TIPO_PRODUCTO_ACTIVOS = " select cod_tipo_producto, tipo_producto, estado, log_insertdate, log_insertuser,log_updatedate,log_updateuser "
			+ " from pd_tipo_producto where estado = 1 "
			+ "  order by tipo_producto ";

	private static final String SQL_BUSCAR_TIPO_PRODUCTO = " select count(*) from pd_tipo_producto "
			+ " where upper(tipo_producto) = upper(?) ";

	private static final String SQL_CAMBIAR_ESTADO = " update pd_tipo_producto  "
			+ " set estado  = ?  " + " where cod_tipo_producto = ? ";

	private static final String SQL_ACTUALIZAR_TIPO_PRODUCTO = " update pd_tipo_producto  "
			+ " set tipo_producto = ?, log_updatedate = current_timestamp, log_updateuser = ?  "
			+ " where cod_tipo_producto = ? ";

	public PDTipoProducto(String usuario) {
		super(usuario);
		log = Logger.getLogger(getClass());
	}

	public boolean addTipoProducto(Connection conn, String tipoProducto)
			throws SQLException {

		return dml(conn, SQL_INSERT_TIPO_PRODUCTO, new Object[] { tipoProducto,
				usuario }) > 0;
	}

	public List<TipoProductoDTO> getTiposProducto(Connection conn)
			throws SQLException {

		return consultaLista(conn, SQL_GET_TIPO_PRODUCTO,
				TipoProductoDTO.class, null);
	}

	public List<TipoProductoDTO> getTiposProductoActivos(Connection conn)
			throws SQLException {

		return consultaLista(conn, SQL_GET_TIPO_PRODUCTO_ACTIVOS,
				TipoProductoDTO.class, null);
	}

	public boolean buscarTipoProducto(Connection conn, String tipoProducto)
			throws SQLException {

		return count(conn, SQL_BUSCAR_TIPO_PRODUCTO,
				new Object[] { tipoProducto }) > 0;
	}

	public boolean cambiarEstado(Connection conn, long codTipoProducto,
			long estado) throws SQLException {

		return dml(conn, SQL_CAMBIAR_ESTADO, new Object[] { estado,
				codTipoProducto }) > 0;

	}

	public boolean actualizarTipoProducto(Connection conn,
			long codTipoProducto, String tipoProducto) throws SQLException {

		return dml(conn, SQL_ACTUALIZAR_TIPO_PRODUCTO, new Object[] {
				tipoProducto, usuario, codTipoProducto }) > 0;
	}

}
