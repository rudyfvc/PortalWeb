package com.portal.bussines;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.ResolucionDTO;

public class PDResolucion extends PDAbstract {
	public static final String TIPO_FACTURA = "FAC";
	public static final String TIPO_AJUSTE = "AJU";

	private static final String SQL_ADD_RESOLUCION = " insert into pd_resolucion(cod_resolucion,cod_empresa, resolucion,fecha_resolucion,estado,"
			+ " doc_tipo,doc_serie,doc_numero_inicial, doc_numero_final, doc_numero_actual, log_insertdate, log_insertuser) "
			+ " values(?,?,?,cast(? as date),?,?,?,?,?,?,current_timestamp,?)";

	private static final String SQL_GET_SEQUENCE = "select nextval('pd_resolucion_sq') ";

	private static final String SQL_GET_RESOLUCIONES = "select res.cod_resolucion,	"
			+ "	res.cod_empresa,	"
			+ "	res.resolucion,	"
			+ "	res.fecha_resolucion,	"
			+ "	case res.estado when 1 then	"
			+ "	'ACTIVO' else	"
			+ "	'INACTIVO'	"
			+ "	  END AS estado,	"
			+ "	res.doc_tipo,	"
			+ "	res.doc_serie,	"
			+ "	res.doc_numero_inicial,	"
			+ "	res.doc_numero_final,	"
			+ "	res.doc_numero_actual	"
			+ "	from pd_resolucion res	"
			+ "	where res.cod_empresa = ? ";

	private static final String SQL_WHERE_ESTADO = "   and res.estado = ? ";

	private static final String SQL_WHERE_SERIE = "   and res.doc_serie = ? ";

	private static final String SQL_WHERE_TIPO = "   and res.doc_tipo = ? ";

	private static final String SQL_UPDATE_RESOLUCION = " update pd_resolucion "
			+ "  set doc_numero_final  = ?, estado = ?,   "
			+ "  log_updateuser = ?, "
			+ "	log_updatedate = current_timestamp "
			+ "  where cod_resolucion = ? ";

	private static final String SQL_ADD_DETALLE_RESOLUCION = " insert into pd_resolucion_detalle("
			+ "	cod_resolucion_detalle,cod_resolucion, cod_empresa, "
			+ "			doc_tipo,doc_serie, doc_numero,estado, log_insertdate,log_insertuser) "
			+ "			values(nextval('pd_resolucion_detalle_sq'), "
			+ "			?, "
			+ "			?,"
			+ "			?,"
			+ "			?,"
			+ "			?,"
			+ "			'L',"
			+ "			current_timestamp," + "			?)";

	private static final String SQL_UPDATE_NUMERO_ACTUAL = "	 update pd_resolucion "
			+ "	set doc_numero_actual = ? " + "	where cod_resolucion = ? ";

	private static final String SQL_GET_CORRELATIVO = "	select 	min(doc_numero) as doc_numero  "
			+ "	from pd_resolucion_detalle"
			+ "	where cod_resolucion = ? and estado = 'L'	"
			+ "	group by doc_tipo,doc_serie ";

	private static final String SQL_SET_USADO = " update pd_resolucion_detalle  "
			+ "  set estado = 'U' "
			+ " where cod_resolucion  = ? "
			+ "  and doc_tipo = ? "
			+ "  and doc_serie = ? "
			+ " and doc_numero = ? ";

	public PDResolucion(String userLoguiado) {
		usuario = userLoguiado;
		log = Logger.getLogger(getClass());
	}

	public boolean addResolucion(Connection conn, ResolucionDTO res)
			throws SQLException {

		return dml(
				conn,
				SQL_ADD_RESOLUCION,
				new Object[] { res.getCod_resolucion(), res.getCod_empresa(),
						res.getResolucion(), res.getFecha_resolucion(), 1,
						res.getDoc_tipo(), res.getDoc_serie(),
						res.getDoc_numero_inicial(), res.getDoc_numero_final(),
						res.getDoc_numero_inicial(), usuario }) > 0;
	}

	public Long getSecuencia(Connection conn) throws SQLException {
		Long b = consultaEscalar(conn, SQL_GET_SEQUENCE, Long.class, null);

		return b;
	}

	public boolean editarResolucion(Connection conn, Long numeroFinal,
			Long estado, Long codResolucion) throws SQLException {

		return dml(conn, SQL_UPDATE_RESOLUCION, new Object[] { numeroFinal,
				estado, usuario, codResolucion }) > 0;
	}

	public List<ResolucionDTO> getResoluciones(Connection conn, Long codEmpresa)
			throws SQLException {

		return consultaLista(conn, SQL_GET_RESOLUCIONES, ResolucionDTO.class,
				new Object[] { codEmpresa });
	}

	public ResolucionDTO getResolucion(Connection conn, Long codEmpresa,
			String docTipo) throws SQLException {
		String sql = SQL_GET_RESOLUCIONES + SQL_WHERE_ESTADO + SQL_WHERE_TIPO;

		return consultaDTO(conn, sql, ResolucionDTO.class, new Object[] {
				codEmpresa, 1, docTipo });
	}

	public ResolucionDTO existeResolucion(Connection conn, Long codEmpresa,
			String docTipo, String docSerie) throws SQLException {
		String sql = SQL_GET_RESOLUCIONES + SQL_WHERE_SERIE + SQL_WHERE_TIPO;

		return consultaDTO(conn, sql, ResolucionDTO.class, new Object[] {
				codEmpresa, docSerie, docTipo });
	}

	public boolean existeResolucionActiva(Connection conn, Long codEmpresa,
			String docTipo) throws SQLException {

		String sql = SQL_GET_RESOLUCIONES + SQL_WHERE_ESTADO + SQL_WHERE_TIPO;

		List<ResolucionDTO> resoluciones = consultaLista(conn, sql,
				ResolucionDTO.class, new Object[] { codEmpresa, 1, docTipo });

		if (resoluciones.size() > 0) {
			return true;
		}

		return false;

	}

	public boolean addDetalleRes(Connection conn, Long codResolucion,
			String docTipo, String docSerie, Long docNumero, Long codEmpresa)
			throws SQLException {

		return dml(conn, SQL_ADD_DETALLE_RESOLUCION, new Object[] {
				codResolucion, codEmpresa, docTipo, docSerie, docNumero,
				usuario }) > 0;
	}

	public boolean setNumeroActual(Connection conn, Long codResolucion,
			Long docNumero) throws SQLException {

		return dml(conn, SQL_UPDATE_NUMERO_ACTUAL, new Object[] { docNumero,
				codResolucion }) > 0;
	}

	public Long getCorrelativo(Connection conn, Long codResolucion)
			throws SQLException {

		BigDecimal b = consultaEscalar(conn, SQL_GET_CORRELATIVO,
				BigDecimal.class, new Object[] { codResolucion });

		return b == null ? null : b.longValue();

	}

	public boolean setUsado(Connection conn, Long codResolucion,
			String docTipo, String docSerie, Long docNumero)
			throws SQLException {
		return dml(conn, SQL_SET_USADO, new Object[] { codResolucion, docTipo,
				docSerie, docNumero }) > 0;

	}

}
