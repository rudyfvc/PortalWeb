package com.portal.bussines;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.portal.dto.InventarioDisponibleDTO;

public class PDInventario extends PDAbstract {

	private static final String SQL_GET_INVENTARIO_DISPONIBLE = "select * from fun_get_inventario_disponible(?,?)";

	private static final String SQL_GET_INVENTARIO_DISPONIBLE_PRODUCTO = " select coalesce(fun_get_inv_por_producto(?,?,?),0) as existencia; ";

	public PDInventario(String userLoguiado) {
		this.usuario = userLoguiado;
	}

	public List<InventarioDisponibleDTO> getInventarioDisponible(
			Connection conn, Long codEmpresa, String codPeriodo)
			throws SQLException {

		return consultaLista(conn, SQL_GET_INVENTARIO_DISPONIBLE,
				InventarioDisponibleDTO.class, new Object[] { codEmpresa,
						codPeriodo, });
	}

	public Long getExistenciaProducto(Connection conn, Long codEmpresa,
			String codPeriodo, Long codProducto) throws SQLException {

		BigDecimal existencia = consultaEscalar(conn,
				SQL_GET_INVENTARIO_DISPONIBLE_PRODUCTO, BigDecimal.class,
				new Object[] { codEmpresa, codPeriodo, codProducto });

		if (existencia == null) {
			existencia = new BigDecimal(0);
		}

		return existencia.longValue();

	}
}
