package com.portal.bussines;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public class PDInventarioPeriodo extends PDAbstract {

	private static final String SQL_BUSCAR_PRODUCTO_EMPRESA_PERIODO = " select count(*) "
			+ "  from pd_inventario_periodo "
			+ "  where cod_periodo = ? "
			+ "	and cod_empresa = ? " + "  and cod_producto = ? ";

	private static final String SQL_INSERT_INVENTARIO = " insert into pd_inventario_periodo(cod_periodo,cod_empresa,cod_producto, "
			+ " inv_cantidad_inicial, inv_cantidad_final, log_insertdate, log_insertuser) "
			+ " values(?,?,?,?,?,current_timestamp,?)";

	private static final String SQL_UPDATE_INVENTARIO = " update pd_inventario_periodo "
			+ " set inv_cantidad_final = inv_cantidad_inicial + ? , "
			+ " log_updatedate = current_timestamp,  log_updateuser = ? "
			+ "  where cod_periodo = ? "
			+ "	and cod_empresa = ? "
			+ "  and cod_producto = ? ";

	private static final String SQL_INVENTARIO_FINAL = " select coalesce(inv_cantidad_final,0) "
			+ " from pd_inventario_periodo "
			+ "  where cod_periodo = ? "
			+ "	and cod_empresa = ? " + "  and cod_producto = ? ";

	private static final String SQL_INVENTARIO_INICIAL = " select coalesce(inv_cantidad_inicial,0) "
			+ " from pd_inventario_periodo "
			+ "  where cod_periodo = ? "
			+ "	and cod_empresa = ? " + "  and cod_producto = ? ";

	public PDInventarioPeriodo(String userLoguiado) {
		this.usuario = userLoguiado;
	}

	public boolean existeProducto(Connection conn, String codPeriodo,
			Long codEmpresa, Long codProducto) throws SQLException {

		Long existe = consultaEscalar(conn,
				SQL_BUSCAR_PRODUCTO_EMPRESA_PERIODO, Long.class, new Object[] {
						codPeriodo, codEmpresa, codProducto });

		return existe.intValue() > 0;

	}

	public boolean addInventario(Connection conn, String codPeriodo,
			Long codEmpresa, Long codProducto, int invInicial, int invFinal)
			throws SQLException {

		return dml(conn, SQL_INSERT_INVENTARIO, new Object[] { codPeriodo,
				codEmpresa, codProducto, invInicial, invFinal, usuario }) > 0;

	}

	public boolean actualizarInventario(Connection conn, String codPeriodo,
			Long codEmpresa, Long codProducto, int cantidad)
			throws SQLException {

		return dml(conn, SQL_UPDATE_INVENTARIO, new Object[] { cantidad,
				usuario, codPeriodo, codEmpresa, codProducto }) > 0;

	}

	public int getInventarioFinal(Connection conn, String codPeriodo,
			Long codEmpresa, Long codProducto) throws SQLException {

		BigDecimal b = consultaEscalar(conn, SQL_INVENTARIO_FINAL,
				BigDecimal.class, new Object[] { codPeriodo, codEmpresa,
						codProducto });

		if (b != null) {
			return b.intValue();
		} else {
			return 0;
		}

	}

	public int getInventarioInicial(Connection conn, String codPeriodo,
			Long codEmpresa, Long codProducto) throws SQLException {

		BigDecimal b = consultaEscalar(conn, SQL_INVENTARIO_INICIAL,
				BigDecimal.class, new Object[] { codPeriodo, codEmpresa,
						codProducto });

		if (b != null) {
			return b.intValue();
		} else {
			return 0;
		}

	}

}
