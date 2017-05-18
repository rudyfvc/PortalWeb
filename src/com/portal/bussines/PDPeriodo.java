package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.portal.dto.PeriodoDTO;

public class PDPeriodo extends PDAbstract {

	public static final String ESTADO_ABIERTO = "A";
	public static final String ESTADO_CERRADO = "C";

	private static final String SQL_GET_PERIODOS = " select * from pd_periodo where estado = 1 ";

	private static final String SQL_GET_PERIODO = "	select "
			+ "	empp.cod_empresa," + "	per.cod_periodo, " + "	empp.estado, "
			+ "	empp.log_insertdate fecha_apertura, "
			+ "	empp.log_updatedate fecha_cierre " + "	from pd_periodo per "
			+ "	inner join pd_empresa_periodo empp "
			+ "	on per.cod_periodo = empp.cod_periodo "
			+ "	and empp.estado = 'A' " + "	and empp.cod_empresa = ? ";

	private static final String SQL_GET_ESTADO_PERIODO = " select estado from pd_empresa_periodo "
			+ "  where cod_empresa = ? " + "  and cod_periodo = ? ";

	private static final String SQL_CERRAR_PERIODO = " update pd_empresa_periodo "
			+ "  set estado = 'C' "
			+ "  where cod_empresa = ? "
			+ "  and cod_periodo = ? ";

	private static final String SQL_GET_ULTIMO_PERIODO = "	select empp.* "
			+ "	from pd_empresa_periodo empp " + "	inner join pd_periodo per"
			+ "	on empp.cod_periodo = per.cod_periodo"
			+ "	where empp.cod_empresa = ? "
			+ "	order by per.anio desc, per.mes desc limit 1 ";

	private static final String SQL_GET_PERIODO_APERTURAR = " select * "
			+ "  from  pd_periodo per "
			+ "  where to_date(mes || '-' ||anio,'MM-YYYY') > to_date(?,'MM-YYYY') "
			+ "  order by anio asc, mes asc  LIMIT 12 ";

	private static final String SQL_GET_PERIODOS_EMPRESA = " 	select *	"
			+ " 	from(	"
			+ " 		select empp.* 	"
			+ " 				 	from pd_empresa_periodo empp   	inner join pd_periodo per	"
			+ " 				 	on empp.cod_periodo = per.cod_periodo	"
			+ " 				 	where empp.cod_empresa = ? 	"
			+ " 				 	order by per.anio asc, per.mes asc limit 12 ) as periodos	"
			+ " 	order by to_date(periodos.cod_periodo,'MM-YYYY') desc  ";

	private static final String SQL_NUEVO_PERIODO = " insert into pd_empresa_periodo (cod_empresa_periodo, cod_empresa,cod_periodo,estado,log_insertdate,log_insertuser)"
			+ " values(nextval('pd_empresa_periodo_sq'),?,?,'A',current_timestamp,?)";

	public PDPeriodo(String userLoguiado) {
		super(userLoguiado);
	}

	public List<PeriodoDTO> getPeriodos(Connection conn) throws SQLException {

		return consultaLista(conn, SQL_GET_PERIODOS, PeriodoDTO.class, null);
	}

	public PeriodoDTO getPeriodoAbierto(Connection conn, Long codEmpresa)
			throws SQLException {

		return consultaDTO(conn, SQL_GET_PERIODO, PeriodoDTO.class,
				new Object[] { codEmpresa });
	}

	public String getEstadoPeriodo(Connection conn, Long codEmpresa,
			String codPeriodo) throws SQLException {

		return consultaEscalar(conn, SQL_GET_ESTADO_PERIODO, String.class,
				new Object[] { codEmpresa, codPeriodo });
	}

	public boolean aperturarPeriodo(Connection conn, Long codEmpresa,
			String codPeriodo) throws SQLException {

		return dml(conn, SQL_NUEVO_PERIODO, new Object[] { codEmpresa,
				codPeriodo, usuario }) > 0;

	}

	public boolean cerrarPeriodo(Connection conn, Long codEmpresa,
			String codPeriodo) throws SQLException {

		return dml(conn, SQL_CERRAR_PERIODO, new Object[] { codEmpresa,
				codPeriodo }) > 0;
	}

	public PeriodoDTO getUltimoPeriodo(Connection conn, Long codEmpresa)
			throws SQLException {

		return consultaDTO(conn, SQL_GET_ULTIMO_PERIODO, PeriodoDTO.class,
				new Object[] { codEmpresa });
	}

	public List<PeriodoDTO> getSiguientesPeriodos(Connection conn, String fecha)
			throws SQLException {

		return consultaLista(conn, SQL_GET_PERIODO_APERTURAR, PeriodoDTO.class,
				new Object[] { fecha });
	}

	public List<PeriodoDTO> getPeriodosPorEmpresa(Connection conn,
			Long codEmpresa) throws SQLException {

		return consultaLista(conn, SQL_GET_PERIODOS_EMPRESA, PeriodoDTO.class,
				new Object[] { codEmpresa });
	}

}
