package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.ProveedorDTO;

public class PDProveedor extends PDAbstract {

	private static final String SQL_GET_PROVEEDORES = " select prov.*,  "
			+ "	ent.descripcion as tipo_entidad " + " from pd_proveedor prov  "
			+ "	inner join pd_tipo_entidad ent"
			+ "	on prov.cod_tipo_entidad = ent.cod_tipo_entidad ";

	private static final String SQL_WHERE_ESTADO_PROVEEDORES = " where prov.estado = ? ";
	private static final String SQL_ORDER_BY_CODIGO = " order by prov.cod_proveedor ";

	private static final String SQL_WHERE_PROVEEDOR_POR_NIT = "  where prov.nit = ?   ";

	private static final String SQL_INSERT_PROVEEDOR = " insert into pd_proveedor "
			+ " (cod_proveedor,"
			+ " cod_tipo_entidad,"
			+ " nombres ,"
			+ " apellidos,"
			+ " nit ,"
			+ " dpi ,"
			+ " pasaporte,"
			+ " patente ,"
			+ " registro_fiscal,"
			+ " email,"
			+ " direccion,"
			+ " telefono_fijo,"
			+ " telefono_celular,"
			+ " telefono_otro,"
			+ " estado, "
			+ " log_insertdate,"
			+ " log_insertuser)"
			+ " values(nextval('pd_proveedor_sq'),cast(? as numeric),?,?,?,?,?,?,?,?,?,?,?,?,1,current_timestamp,?)";

	private static final String SQL_UPDATE_PROVEEDOR = " update pd_proveedor set "
			+ " cod_tipo_entidad = cast(? as numeric) ,"
			+ " nombres = ? ,"
			+ " apellidos = ? ,"
			+ " nit = ? ,"
			+ " dpi = ? ,"
			+ " pasaporte =?,"
			+ " patente =?,"
			+ " registro_fiscal = ?,"
			+ " email = ?,"
			+ " direccion = ?,"
			+ " telefono_fijo = ?,"
			+ " telefono_celular = ? ,"
			+ " telefono_otro = ? ,"
			+ " estado = ?, "
			+ " log_updatedate = current_timestamp, "
			+ " log_updateuser = ? "
			+ "  where cod_proveedor = cast(? as numeric) ";

	public PDProveedor(String usuario) {
		super(usuario);
		log = Logger.getLogger(getClass());
	}

	public List<ProveedorDTO> getAllProveedores(Connection conn)
			throws SQLException {
		String sql = SQL_GET_PROVEEDORES + SQL_ORDER_BY_CODIGO;

		return consultaLista(conn, sql, ProveedorDTO.class, null);
	}

	public List<ProveedorDTO> getProveedoresPorEstado(Connection conn,
			long estado) throws SQLException {
		String sql = SQL_GET_PROVEEDORES + SQL_WHERE_ESTADO_PROVEEDORES
				+ SQL_ORDER_BY_CODIGO;

		return consultaLista(conn, sql, ProveedorDTO.class,
				new Object[] { estado });
	}

	public boolean addProveedor(Connection conn, ProveedorDTO proveedor)
			throws SQLException {

		return dml(
				conn,
				SQL_INSERT_PROVEEDOR,
				new Object[] { proveedor.getCod_tipo_entidad(),
						proveedor.getNombres(), proveedor.getApellidos(),
						proveedor.getNit(), proveedor.getDpi(),
						proveedor.getPasaporte(), proveedor.getPatente(),
						proveedor.getRegistro_fiscal(), proveedor.getEmail(),
						proveedor.getDireccion(), proveedor.getTelefono_fijo(),
						proveedor.getTelefono_celular(),
						proveedor.getTelefono_otro(), usuario }) > 0;
	}

	public boolean actualizarProveedor(Connection conn, ProveedorDTO proveedor)
			throws SQLException {
		return dml(
				conn,
				SQL_UPDATE_PROVEEDOR,
				new Object[] { proveedor.getCod_tipo_entidad(),
						proveedor.getNombres(), proveedor.getApellidos(),
						proveedor.getNit(), proveedor.getDpi(),
						proveedor.getPasaporte(), proveedor.getPatente(),
						proveedor.getRegistro_fiscal(), proveedor.getEmail(),
						proveedor.getDireccion(), proveedor.getTelefono_fijo(),
						proveedor.getTelefono_celular(),
						proveedor.getTelefono_otro(),
						Long.parseLong(proveedor.getEstado()), usuario,
						proveedor.getCod_proveedor() }) > 0;
	}

	public ProveedorDTO getProveedorPorNit(Connection conn, String nit)
			throws SQLException {
		String sql = SQL_GET_PROVEEDORES + SQL_WHERE_PROVEEDOR_POR_NIT;

		return consultaDTO(conn, sql, ProveedorDTO.class,
				new Object[] { nit.toUpperCase() });
	}

}
