package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.ClienteDTO;

public class PDCliente extends PDAbstract {

	private static final String SQL_GET_CLIENTE = " select cliente.*,  "
			+ "	ent.descripcion as tipo_entidad "
			+ " from pd_cliente cliente  " + "	inner join pd_tipo_entidad ent"
			+ "	on cliente.cod_tipo_entidad = ent.cod_tipo_entidad ";

	private static final String SQL_WHERE_ESTADO_CLIENTE = " where cliente.estado = ? ";
	private static final String SQL_ORDER_BY_CODIGO = " order by cliente.cod_cliente ";

	private static final String SQL_WHERE_CLIENTE_POR_NIT = "  where cliente.nit = ?   ";

	private static final String SQL_INSERT_CLIENTE = " insert into pd_cliente "
			+ " (cod_cliente,"
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
			+ " values(nextval('pd_cliente_sq'),cast(? as numeric),?,?,?,?,?,?,?,?,?,?,?,?,1,current_timestamp,?)";

	private static final String SQL_UPDATE_CLIENTE = " update pd_cliente set "
			+ " cod_tipo_entidad = cast(? as numeric) ," + " nombres = ? ,"
			+ " apellidos = ? ," + " nit = ? ," + " dpi = ? ,"
			+ " pasaporte =?," + " patente =?," + " registro_fiscal = ?,"
			+ " email = ?," + " direccion = ?," + " telefono_fijo = ?,"
			+ " telefono_celular = ? ," + " telefono_otro = ? ,"
			+ " estado = ? , " + " log_updatedate = current_timestamp, "
			+ " log_updateuser = ? "
			+ "  where cod_cliente = cast(? as numeric) ";

	public PDCliente(String usuario) {
		super(usuario);
		log = Logger.getLogger(getClass());
	}

	public List<ClienteDTO> getAllClientes(Connection conn) throws SQLException {
		String sql = SQL_GET_CLIENTE + SQL_ORDER_BY_CODIGO;

		return consultaLista(conn, sql, ClienteDTO.class, null);
	}

	public List<ClienteDTO> getClientesPorEstado(Connection conn, long estado)
			throws SQLException {
		String sql = SQL_GET_CLIENTE + SQL_WHERE_ESTADO_CLIENTE
				+ SQL_ORDER_BY_CODIGO;

		return consultaLista(conn, sql, ClienteDTO.class,
				new Object[] { estado });
	}

	public boolean addCliente(Connection conn, ClienteDTO cliente)
			throws SQLException {

		return dml(
				conn,
				SQL_INSERT_CLIENTE,
				new Object[] { cliente.getCod_tipo_entidad(),
						cliente.getNombres(), cliente.getApellidos(),
						cliente.getNit().toUpperCase(), cliente.getDpi(),
						cliente.getPasaporte(), cliente.getPatente(),
						cliente.getRegistro_fiscal(), cliente.getEmail(),
						cliente.getDireccion(), cliente.getTelefono_fijo(),
						cliente.getTelefono_celular(),
						cliente.getTelefono_otro(), usuario }) > 0;
	}

	public boolean actualizarCliente(Connection conn, ClienteDTO cliente)
			throws SQLException {
		return dml(
				conn,
				SQL_UPDATE_CLIENTE,
				new Object[] { cliente.getCod_tipo_entidad(),
						cliente.getNombres(), cliente.getApellidos(),
						cliente.getNit().toUpperCase(), cliente.getDpi(),
						cliente.getPasaporte(), cliente.getPatente(),
						cliente.getRegistro_fiscal(), cliente.getEmail(),
						cliente.getDireccion(), cliente.getTelefono_fijo(),
						cliente.getTelefono_celular(),
						cliente.getTelefono_otro(),
						Long.parseLong(cliente.getEstado()), usuario,
						cliente.getCod_cliente() }) > 0;
	}

	public ClienteDTO getClientePorNit(Connection conn, String nit)
			throws SQLException {
		String sql = SQL_GET_CLIENTE + SQL_WHERE_CLIENTE_POR_NIT;

		return consultaDTO(conn, sql, ClienteDTO.class,
				new Object[] { nit.toUpperCase() });
	}
}
