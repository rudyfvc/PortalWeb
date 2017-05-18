package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.DocumentoSalDetalleDTO;

public class PDDetalleCurso extends PDAbstract {

	private static final String SQL_ADD_DETALLE = " insert into pd_documento_curso (cod_transaccion, user_name, cod_producto, cantidad, "
			+ "precio_unitario,doc_monto_descuento_unitario, doc_monto_gravado, doc_monto_descuento, doc_monto_total)values(nextval('cod_transaccion_sq'),?,?,?,?,?,?,?,?)";

	private static final String SQL_DELETE_DETALLE = " delete from pd_documento_curso where cod_producto = ?  and user_name = ?  ";

	private static final String SQL_DELETE_ALL_DETALLE = " delete from pd_documento_curso where user_name = ?  ";

	private static final String SQL_GET_DETALLE = " select cur.*, pro.producto  "
			+ "  from pd_documento_curso cur "
			+ "  inner join pd_producto pro "
			+ "  on cur.cod_producto = pro.cod_producto "
			+ "   where cur.user_name = ? ";

	public PDDetalleCurso(String userLoguiado) {
		usuario = userLoguiado;
		log = Logger.getLogger(getClass());
	}

	public boolean addDoctoCurso(Connection conn, DocumentoSalDetalleDTO det)
			throws SQLException {

		return dml(
				conn,
				SQL_ADD_DETALLE,
				new Object[] { usuario, det.getCod_producto(),
						det.getCantidad(), det.getDoc_monto_unitario(),
						det.getDoc_monto_descuento_unitario(),
						det.getDoc_monto_gravado(),
						det.getDoc_monto_descuento(), det.getDoc_monto_total() }) > 0;
	}

	public boolean eliminarProducto(Connection conn, Long codProducto)
			throws SQLException {

		return dml(conn, SQL_DELETE_DETALLE, new Object[] { codProducto,
				usuario }) > 0;
	}

	public boolean eliminarTransacciones(Connection conn) throws SQLException {
		return dml(conn, SQL_DELETE_ALL_DETALLE, new Object[] { usuario }) > 0;
	}

	public List<DocumentoSalDetalleDTO> getDetalles(Connection conn)
			throws SQLException {

		return consultaLista(conn, SQL_GET_DETALLE,
				DocumentoSalDetalleDTO.class, new Object[] { usuario });

	}
}
