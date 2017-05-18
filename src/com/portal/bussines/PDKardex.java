package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.KardexDTO;

public class PDKardex extends PDAbstract {

	public PDKardex() {
		log = Logger.getLogger(getClass());
	}

	private static final String SQL_GET_INV_INICIAL = "select 0 as cod_movimiento, "
			+ "	'01-' || ?  as doc_fecha, "
			+ "	'Inventario inicial' as descripcion, "
			+ "	inv_cantidad_inicial as inv_inicial, "
			+ "	0 inv_entrada, "
			+ "	0 inv_salida, "
			+ "	inv_cantidad_inicial inv_final "
			+ "	from pd_inventario_periodo "
			+ "	where cod_empresa = ?  "
			+ "	and cod_periodo = ? " + "and cod_producto = ?";

	private static final String SQL_GET_INV_ENTRADA = "	select enc.cod_movimiento,	enc.doc_tipo,enc.doc_serie,enc.doc_numero,	"
			+ " to_char(enc.doc_fecha,'DD-MM-YYYY') as doc_fecha ,mov.descripcion as tipo_movimiento,	"
			+ "	prov.nombres || ' ' || prov.apellidos as descripcion,	"
			+ "	0 as inv_inicial,	"
			+ "	det.cantidad as inv_entrada,	"
			+ "	0 as inv_salida,	"
			+ "	0 as inv_final	"
			+ "	from pd_documento_ent_detalle det	"
			+ "	inner join pd_documento_ent enc	"
			+ "	on enc.cod_movimiento = det.cod_movimiento	"
			+ "	inner join pd_proveedor prov 	"
			+ "	on prov.cod_proveedor = enc.cod_proveedor	"
			+ " inner join pd_tipo_movimiento mov  "
			+ " on enc.cod_tipo_movimiento = mov.cod_tipo_movimiento  "
			+ "	where enc.cod_empresa = ?	"
			+ "	and enc.cod_periodo = ?	"
			+ "	and det.cod_producto = ? ";

	private static final String SQL_GET_INV_SALIDA = "	select enc.cod_movimiento,		enc.doc_tipo,enc.doc_serie,enc.doc_numero,"
			+ "	to_char(enc.doc_fecha,'DD-MM-YYYY') as doc_fecha,	mov.descripcion as tipo_movimiento, "
			+ "	cliente.nombres || ' ' || cliente.apellidos as descripcion,	"
			+ "	0 as inv_inicial,	"
			+ "	0 as inv_entrada,	"
			+ "	det.cantidad as inv_salida,	"
			+ "	0 as inv_final	"
			+ "	from pd_documento_sal_detalle det	"
			+ "	inner join pd_documento_sal enc	"
			+ "	on enc.cod_movimiento = det.cod_movimiento	"
			+ "	inner join pd_cliente cliente 	"
			+ "	on cliente.cod_cliente = enc.cod_cliente	"
			+ " inner join pd_tipo_movimiento mov  "
			+ " on enc.cod_tipo_movimiento = mov.cod_tipo_movimiento  "
			+ "	where enc.cod_empresa = ?	"
			+ "	and enc.cod_periodo = ?	"
			+ "	and det.cod_producto = ? ";

	private static final String SQL_GET_ANULACIONES_ENTRADA = "	select enc.an_cod_movimiento as cod_movimiento,	enc.doc_tipo,enc.doc_serie,enc.doc_numero, "
			+ "			  to_char(enc.an_fecha, ? ) as doc_fecha ,	'ANULACION ENT.' as tipo_movimiento,	"
			+ "			 	prov.nombres || ' ' || prov.apellidos  as descripcion,		"
			+ "			 	0 as inv_inicial,		"
			+ "			 	0 as inv_entrada,		"
			+ "			 	det.cantidad as inv_salida,	"
			+ "			 	0 as inv_final	"
			+ "			 	from pd_documento_ent_detalle det	"
			+ "			 	inner join pd_documento_ent enc	"
			+ "			 	on enc.cod_movimiento = det.cod_movimiento	"
			+ "			 	inner join pd_proveedor prov 	"
			+ "			 	on prov.cod_proveedor = enc.cod_proveedor	"
			+ "			 	where enc.cod_empresa = ? "
			+ "			 	and enc.cod_periodo = ?	"
			+ "			 	and enc.doc_estado = 'A' "
			+ "             and det.cod_producto = ? ";

	private static final String SQL_GET_ANULACIONES_SALIDAS = "  select enc.an_cod_movimiento as cod_movimiento, "
			+ "			enc.doc_tipo,enc.doc_serie,enc.doc_numero,	"
			+ "			  to_char(enc.an_fecha,?) as doc_fecha , 'ANULACION SAL.' as tipo_movimiento,			"
			+ "			 	cliente.nombres || ' ' || cliente.apellidos   as descripcion,		"
			+ "			 	0 as inv_inicial,		"
			+ "			 	det.cantidad as inv_entrada,		"
			+ "			 	0 as inv_salida,		"
			+ "			 	0 as inv_final	"
			+ "			 	from pd_documento_sal_detalle det	"
			+ "			 	inner join pd_documento_sal enc	"
			+ "			 	on enc.cod_movimiento = det.cod_movimiento "
			+ "			 	inner join pd_cliente cliente "
			+ "			 	on cliente.cod_cliente = enc.cod_cliente "
			+ "			 	where enc.cod_empresa = ? "
			+ "			 	and enc.cod_periodo = ? "
			+ "			 	and enc.doc_estado = 'A'  "
			+ "                and det.cod_producto = ? ";

	public KardexDTO getInvInicial(Connection conn, Long codEmpresa,
			String codPeriodo, Long codProducto) throws SQLException {

		KardexDTO k = consultaDTO(
				conn,
				SQL_GET_INV_INICIAL,
				KardexDTO.class,
				new Object[] { codPeriodo, codEmpresa, codPeriodo, codProducto });

		if (k == null) {
			k = new KardexDTO();
			k.setCod_movimiento(0L);
			k.setDoc_fecha("01-" + codPeriodo);
			k.setInv_entrada(0L);
			k.setInv_final(0L);
			k.setInv_inicial(0L);
			k.setInv_salida(0L);
		}

		return k;

	}

	public List<KardexDTO> getEntradas(Connection conn, Long codEmpresa,
			String codPeriodo, Long codProducto) throws SQLException {

		List<KardexDTO> entradas = consultaLista(conn, SQL_GET_INV_ENTRADA,
				KardexDTO.class, new Object[] { codEmpresa, codPeriodo,
						codProducto });

		return entradas;
	}

	public List<KardexDTO> getSalidas(Connection conn, Long codEmpresa,
			String codPeriodo, Long codProducto) throws SQLException {

		List<KardexDTO> entradas = consultaLista(conn, SQL_GET_INV_SALIDA,
				KardexDTO.class, new Object[] { codEmpresa, codPeriodo,
						codProducto });

		return entradas;
	}

	public List<KardexDTO> getAnulacionesEntradas(Connection conn,
			Long codEmpresa, String codPeriodo, Long codProducto)
			throws SQLException {

		List<KardexDTO> entradas = consultaLista(conn,
				SQL_GET_ANULACIONES_ENTRADA, KardexDTO.class, new Object[] {
						FORMATO_FECHA, codEmpresa, codPeriodo, codProducto });

		return entradas;
	}

	public List<KardexDTO> getAnulacionesSalidas(Connection conn,
			Long codEmpresa, String codPeriodo, Long codProducto)
			throws SQLException {

		List<KardexDTO> entradas = consultaLista(conn,
				SQL_GET_ANULACIONES_SALIDAS, KardexDTO.class, new Object[] {
						FORMATO_FECHA, codEmpresa, codPeriodo, codProducto });

		return entradas;
	}
}
