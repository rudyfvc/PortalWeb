package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.MarcaDTO;

public class PDMarca extends PDAbstract {

	private static final String SQL_INSERT_MARCA = " insert into pd_marca(cod_marca, marca,"
			+ "estado,"
			+ " log_insertdate,log_insertuser) values(nextval('pd_marca_SQ'),?,1,current_timestamp,?)";

	private static final String SQL_GET_MARCA = " select cod_marca, marca, estado, log_insertdate, log_insertuser,log_updatedate,log_updateuser "
			+ " from pd_marca " + "  order by marca ";

	private static final String SQL_GET_MARCAS_ACTIVAS = " select cod_marca, marca, estado, log_insertdate, log_insertuser,log_updatedate,log_updateuser "
			+ " from pd_marca where estado = 1 " + "  order by marca ";

	private static final String SQL_BUSCAR_MARCA = " select count(*) from pd_marca "
			+ " where upper(marca) = upper(?) ";

	private static final String SQL_CAMBIAR_ESTADO = " update pd_marca  "
			+ " set estado  = ?  " + " where cod_marca = ? ";

	private static final String SQL_ACTUALIZAR_MARCA = " update pd_marca  "
			+ " set marca = ?, log_updatedate = current_timestamp, log_updateuser = ?  "
			+ " where cod_marca = ? ";

	public PDMarca(String usuario) {
		super(usuario);
		log = Logger.getLogger(getClass());
	}

	public boolean addMarca(Connection conn, String unidadMedida)
			throws SQLException {

		return dml(conn, SQL_INSERT_MARCA,
				new Object[] { unidadMedida, usuario }) > 0;
	}

	public List<MarcaDTO> getAllMarcas(Connection conn) throws SQLException {

		return consultaLista(conn, SQL_GET_MARCA, MarcaDTO.class, null);
	}

	public List<MarcaDTO> getMarcasActivas(Connection conn) throws SQLException {

		return consultaLista(conn, SQL_GET_MARCAS_ACTIVAS, MarcaDTO.class, null);
	}

	public boolean buscarMarca(Connection conn, String unidadMedida)
			throws SQLException {

		return count(conn, SQL_BUSCAR_MARCA, new Object[] { unidadMedida }) > 0;
	}

	public boolean cambiarEstado(Connection conn, long codUnidadMedida,
			long estado) throws SQLException {

		log.debug("se cambiara el estado: " + estado);
		log.debug("Para el código: " + codUnidadMedida);

		return dml(conn, SQL_CAMBIAR_ESTADO, new Object[] { estado,
				codUnidadMedida }) > 0;

	}

	public boolean actualizarMarca(Connection conn, long codUnidadMedida,
			String unidadMedida) throws SQLException {

		return dml(conn, SQL_ACTUALIZAR_MARCA, new Object[] { unidadMedida,
				usuario, codUnidadMedida }) > 0;
	}

}
