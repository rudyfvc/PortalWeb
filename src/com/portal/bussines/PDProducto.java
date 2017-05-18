package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.ProductoDTO;

public class PDProducto extends PDAbstract {

	public static final String FILTRO_POR_CODIGO = "POR CODIGO";
	public static final String FILTRO_POR_DESCRIPCION = "POR DESCRIPCION";

	private static final String SQL_GET_ALL_PRODUCTOS = " select pro.*,"
			+ " tipo.tipo_producto," + " marca.marca,"
			+ " medida.unidad_medida" + " from pd_producto pro "
			+ " inner join pd_tipo_producto tipo "
			+ " on pro.cod_tipo_producto = tipo.cod_tipo_producto "
			+ " inner join pd_marca marca "
			+ " on pro.cod_marca =marca.cod_marca "
			+ " inner join pd_unidad_medida medida  "
			+ " on pro.cod_unidad_medida = medida.cod_unidad_medida "
			+ " order by pro.producto ";

	private static final String SQL_GET_FILTRAR_PRODUCTOS = " select pro.*,"
			+ " tipo.tipo_producto," + " marca.marca,"
			+ " medida.unidad_medida" + " from pd_producto pro "
			+ " inner join pd_tipo_producto tipo "
			+ " on pro.cod_tipo_producto = tipo.cod_tipo_producto "
			+ " inner join pd_marca marca "
			+ " on pro.cod_marca =marca.cod_marca "
			+ " inner join pd_unidad_medida medida  "
			+ " on pro.cod_unidad_medida = medida.cod_unidad_medida ";

	private static final String SQL_WHERE_POR_CODIGO = "  where pro.cod_producto = ? ";
	private static final String SQL_WHERE_POR_DESCRIPCION = "  where upper(pro.producto) like upper(?) ";
	private static final String SQL_ORDER_BY_CODIGO = "  order by pro.cod_producto desc";

	private static final String SQL_GET_PRODUCTOS_ESTADO = " select pro.*,"
			+ " tipo.tipo_producto," + " marca.marca,"
			+ " medida.unidad_medida" + " from pd_producto pro "
			+ " inner join pd_tipo_producto tipo "
			+ " on pro.cod_tipo_producto = tipo.cod_tipo_producto "
			+ " inner join pd_marca marca "
			+ " on pro.cod_marca =marca.cod_marca "
			+ " inner join pd_unidad_medida medida  "
			+ " on pro.cod_unidad_medida = medida.cod_unidad_medida "
			+ " where pro.estado = ?  " + " order by pro.cod_producto ";

	private static final String SQL_ADD_PRODUCTO = " insert into pd_producto(cod_producto,producto,cod_tipo_producto,"
			+ "cod_marca,cod_unidad_medida,estado,log_insertdate,log_insertuser) "
			+ "values(nextval('pd_producto_sq'),?,?,?,?,1,current_timestamp,?)";

	private static final String SQL_MODIFICAR_PRODUCTO = " update pd_producto "
			+ " set producto = ?, cod_tipo_producto = ? , cod_marca=?,cod_unidad_medida = ? , log_updatedate = current_timestamp, log_updateuser = ?  "
			+ "  where cod_producto = ? ";

	private static final String SQL_CAMBIAR_ESTADO = "  update pd_producto "
			+ " set estado = ?  where cod_producto = ? ";

	public PDProducto(String usuario) {
		super(usuario);
		log = Logger.getLogger(getClass());
	}

	public boolean addProducto(Connection conn, String producto,
			long codTipoProducto, long codMarca, long codUnidadMedida)
			throws SQLException {

		return dml(conn, SQL_ADD_PRODUCTO, new Object[] { producto,
				codTipoProducto, codMarca, codUnidadMedida, usuario }) > 0;
	}

	public List<ProductoDTO> getAllProductos(Connection conn)
			throws SQLException {

		return consultaLista(conn, SQL_GET_ALL_PRODUCTOS, ProductoDTO.class,
				null);

	}

	public List<ProductoDTO> getProductosEstado(Connection conn, long estado)
			throws SQLException {

		return consultaLista(conn, SQL_GET_PRODUCTOS_ESTADO, ProductoDTO.class,
				new Object[] { estado });

	}

	public boolean modificarProducto(Connection conn, long codProducto,
			String producto, long codTipoProducto, long codMarca,
			long codUnidadMedida) throws SQLException {

		return dml(conn, SQL_MODIFICAR_PRODUCTO, new Object[] { producto,
				codTipoProducto, codMarca, codUnidadMedida, usuario,
				codProducto }) > 0;
	}

	public boolean cambiarEstado(Connection conn, long estado, long codProducto)
			throws SQLException {

		return dml(conn, SQL_CAMBIAR_ESTADO,
				new Object[] { estado, codProducto }) > 0;
	}

	public List<ProductoDTO> filtrarProductos(Connection conn,
			long codProducto, String descProducto, String tipo)
			throws SQLException {
		String sql = SQL_GET_FILTRAR_PRODUCTOS;
		Object[] params = null;

		if (tipo.equalsIgnoreCase(FILTRO_POR_CODIGO)) {
			sql += SQL_WHERE_POR_CODIGO + SQL_ORDER_BY_CODIGO;
			params = new Object[] { codProducto };
		} else if (tipo.equalsIgnoreCase(FILTRO_POR_DESCRIPCION)) {
			sql += SQL_WHERE_POR_DESCRIPCION + SQL_ORDER_BY_CODIGO;
			params = new Object[] { "%" + descProducto + "%" };
		} else {
			sql += SQL_ORDER_BY_CODIGO;
		}

		return consultaLista(conn, sql, ProductoDTO.class, params);

	}

	private static final String SQL_EDITAR_PRECIO = " update pd_producto "
			+ "  set precio = ?, " + "	desc_porcentaje = ? ,"
			+ "  log_updatedate = current_timestamp, log_updateuser = ? "
			+ "   where cod_producto = ? ";

	public boolean editarPrecio(Connection conn, Double precio,
			Double descuento, Long codProducto) throws SQLException {

		return dml(conn, SQL_EDITAR_PRECIO, new Object[] { precio, descuento,
				usuario, codProducto }) > 0;
	}

	public ProductoDTO getProductoPorCodigo(Connection conn, Long codProducto)
			throws SQLException {
		String sql = SQL_GET_FILTRAR_PRODUCTOS + SQL_WHERE_POR_CODIGO;

		return consultaDTO(conn, sql, ProductoDTO.class,
				new Object[] { codProducto });
	}

}
