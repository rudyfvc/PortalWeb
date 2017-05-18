package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.UnidadMedidaDTO;

public class PDUnidadMedida extends PDAbstract {

	private static final String SQL_INSERT_UNIDAD_MEDIDA = " insert into pd_unidad_medida(cod_unidad_medida, unidad_medida,"
			+ "estado,"
			+ " log_insertdate,log_insertuser) values(nextval('PD_UNIDAD_MEDIDA_SQ'),?,1,current_timestamp,?)";

	private static final String SQL_GET_UNIDAD_MEDIDA = " select cod_unidad_medida, unidad_medida, estado, log_insertdate, log_insertuser,log_updatedate,log_updateuser "
			+ " from pd_unidad_medida " + "  order by unidad_medida ";

	private static final String SQL_GET_UNIDAD_MEDIDA_ACTIVAS = " select cod_unidad_medida, unidad_medida, estado, log_insertdate, log_insertuser,log_updatedate,log_updateuser "
			+ " from pd_unidad_medida where estado = 1 "
			+ "  order by unidad_medida ";

	private static final String SQL_BUSCAR_UNIDAD_MEDIDA = " select count(*) from pd_unidad_medida "
			+ " where upper(unidad_medida) = upper(?) ";

	private static final String SQL_CAMBIAR_ESTADO = " update pd_unidad_medida  "
			+ " set estado  = ?  " + " where cod_unidad_medida = ? ";

	private static final String SQL_ACTUALIZAR_UNIDAD_MEDIDA = " update pd_unidad_medida  "
			+ " set unidad_medida = ?, log_updatedate = current_timestamp, log_updateuser = ?  "
			+ " where cod_unidad_medida = ? ";

	public PDUnidadMedida(String usuario) {
		super(usuario);
		log = Logger.getLogger(getClass());
	}

	public boolean addUnidadMedida(Connection conn, String unidadMedida)
			throws SQLException {

		return dml(conn, SQL_INSERT_UNIDAD_MEDIDA, new Object[] { unidadMedida,
				usuario }) > 0;
	}

	public List<UnidadMedidaDTO> getUnidadesMedida(Connection conn)
			throws SQLException {

		return consultaLista(conn, SQL_GET_UNIDAD_MEDIDA,
				UnidadMedidaDTO.class, null);
	}

	public List<UnidadMedidaDTO> getUnidadesMedidaActivas(Connection conn)
			throws SQLException {

		return consultaLista(conn, SQL_GET_UNIDAD_MEDIDA_ACTIVAS,
				UnidadMedidaDTO.class, null);
	}

	public boolean buscarUnidadMedida(Connection conn, String unidadMedida)
			throws SQLException {

		return count(conn, SQL_BUSCAR_UNIDAD_MEDIDA,
				new Object[] { unidadMedida }) > 0;
	}

	public boolean cambiarEstado(Connection conn, long codUnidadMedida,
			long estado) throws SQLException {

		log.debug("se cambiara el estado: " + estado);
		log.debug("Para el código: " + codUnidadMedida);

		return dml(conn, SQL_CAMBIAR_ESTADO, new Object[] { estado,
				codUnidadMedida }) > 0;

	}

	public boolean actualizarUnidadMedida(Connection conn,
			long codUnidadMedida, String unidadMedida) throws SQLException {

		return dml(conn, SQL_ACTUALIZAR_UNIDAD_MEDIDA, new Object[] {
				unidadMedida, usuario, codUnidadMedida }) > 0;
	}

}
