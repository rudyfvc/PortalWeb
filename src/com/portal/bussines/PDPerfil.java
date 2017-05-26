package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.PerfilDTO;

public class PDPerfil extends PDAbstract {

	private static final String SQL_GET_PERMISOS = "SELECT app.app_nombre, "
			+ "  per.cod_perfil, " + "  per.per_nombre, "
			+ "  CASE per.cod_estado " + "    WHEN 1 " + "    THEN 'ACTIVO' "
			+ "    ELSE 'INACTIVO' " + "  END AS per_estado "
			+ "	FROM sec_perfil per " + "INNER JOIN pfd_estado est "
			+ "	ON per.cod_estado = est.cod_estado "
			+ "	INNER JOIN sec_aplicacion app "
			+ "	ON per.cod_aplicacion = app.cod_aplicacion ";

	private static final String SQL_UPDATE_ESTADO_PERFIL = " update sec_perfil  "
			+ " set cod_estado = cast(? as numeric)  "
			+ "  where cod_perfil = cast(? as numeric) ";

	private static final String SQL_GET_SEQUENCE_PERFIL = " select nextval('SEC_PERFIL_SQ') ";

	private static final String SQL_ADD_PERFIL = " insert into sec_perfil(cod_perfil,cod_aplicacion,per_nombre,cod_estado,log_insertdate,log_insertuser)"
			+ "  values(?,?,?,?,current_date,?)";

	private static final String SQL_ADD_PERFIL_OPCION = " insert into sec_perfil_opcion (cod_perfil,cod_aplicacion_opcion,log_insertdate,log_insertuser)"
			+ "values(?,?,current_date,?)";

	private static final String SQL_UPDATE_PERFIL = "  update sec_perfil "
			+ " set per_nombre = ?  " + "  where cod_perfil = ? ";

	private static final String SQL_DELETE_PERFIL_OPCION = "delete from sec_perfil_opcion  "
			+ " where cod_perfil = ? ";

	public PDPerfil() {
		super.log = Logger.getLogger(getClass());
	}

	public List<PerfilDTO> getPerfiles(Connection portal) throws SQLException {
		log.debug("obteniendo perfiles...");

		return consultaLista(portal, SQL_GET_PERMISOS, PerfilDTO.class, null);
	}

	public boolean cambiarEstado(Connection portal, String codEstado,
			String codPerfil) throws SQLException {

		log.debug("inHabilitando perfil...");

		return dml(portal, SQL_UPDATE_ESTADO_PERFIL, new Object[] { codEstado,
				codPerfil }) > 0;
	}

	public boolean addPerfil(Connection portal, String codAplicacion,
			String perNombre, String usuario, List<String> opciones)
			throws SQLException {

		Long secuencia = consultaEscalar(portal, SQL_GET_SEQUENCE_PERFIL,
				Long.class, null);

		boolean addPerfil = dml(portal, SQL_ADD_PERFIL, new Object[] {
				secuencia, Long.parseLong(codAplicacion), perNombre,
				ESTADO_ACTIVO, usuario }) > 0;

		if (addPerfil) {
			for (String opcion : opciones) {
				if (dml(portal, SQL_ADD_PERFIL_OPCION, new Object[] {
						secuencia, Long.parseLong(opcion), usuario }) <= 0)
					return false;
			}
		} else {
			return false;
		}

		return true;
	}

	public boolean updatePerfil(Connection portal, String codAplicacion,
			Long codPerfil, String perNombre, String usuario,
			List<String> opciones) throws SQLException {

		boolean updatePerfil = dml(portal, SQL_UPDATE_PERFIL, new Object[] {
				perNombre, codPerfil }) > 0;

		if (updatePerfil) {
			dml(portal, SQL_DELETE_PERFIL_OPCION, new Object[] { codPerfil });

			for (String opcion : opciones) {
				if (dml(portal, SQL_ADD_PERFIL_OPCION, new Object[] {
						codPerfil, Long.parseLong(opcion), usuario }) <= 0) {
					log.debug("falló la inserción de la opcion: " + opcion);
					return false;
				}

			}

		} else {
			log.debug("no fue posible editar el perfil.");
			return false;
		}

		return true;
	}

}
