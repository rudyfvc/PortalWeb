package com.portal.bussines;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.DocumentoEntDetalleDTO;
import com.portal.dto.DocumentoEntEncabezadoDTO;
import com.portal.enums.EstadosDocumento;

public class PDDocumentoEnt extends PDAbstract {

	private static final String SQL_ADD_ENCABEZADO = " insert into pd_documento_ent (cod_movimiento,cod_periodo,"
			+ "	doc_tipo,"
			+ "	doc_serie,"
			+ "	doc_numero,"
			+ "	doc_fecha,"
			+ "	doc_estado,"
			+ "	cod_empresa,"
			+ "	cod_tipo_movimiento,"
			+ "	cod_proveedor,"
			+ "	doc_monto_descuento,"
			+ "	doc_monto_gravado,"
			+ "	doc_monto_iva,"
			+ "	doc_monto_total,"
			+ "	log_insertdate,"
			+ "	log_insertuser) values(?,?,?,?,?,cast(? as date),?,?,?,?,?,?,?,?,current_timestamp,?) ";

	private static final String SQL_ADD_DETALLE = "insert into pd_documento_ent_detalle(cod_movimiento,"
			+ "	cod_producto,"
			+ "	cantidad,"
			+ " precio_unitario, "
			+ "	doc_monto_gravado,"
			+ "	doc_monto_descuento,"
			+ "	doc_monto_iva,"
			+ "	doc_monto_total,"
			+ "	log_insertdate,"
			+ "	log_insertuser) "
			+ "	values(?,?,?,?,?,?,?,?,current_timestamp,?)";

	private static final String SQL_GET_SEQUENCE = " select nextval('cod_movimiento_sq')";

	private static final String SQL_GET_ENTRADAS = "	select doc.*,"
			+ "	emp.emp_nombre as nombre_empresa, "
			+ "	tipo.descripcion as tipo_movimiento, "
			+ "	prov.nombres ||  ' ' ||  prov.apellidos as proveedor "
			+ "	from pd_documento_ent doc " + "	inner join pfd_empresa emp "
			+ "	on doc.cod_empresa = emp.cod_empresa "
			+ "	inner join pd_tipo_movimiento tipo "
			+ "	on doc.cod_tipo_movimiento = tipo.cod_tipo_movimiento "
			+ "	inner join pd_proveedor prov "
			+ "	on doc.cod_proveedor = prov.cod_proveedor "
			+ "  where doc.cod_empresa = ? " + "     and doc.cod_periodo = ? "
			+ "  and doc.cod_tipo_movimiento = ? ";

	private static final String SQL_GET_DOCUMENTO = " select * from pd_documento_ent "
			+ " where doc_tipo = ? "
			+ " and doc_serie  = ? "
			+ " and doc_numero = ? "
			+ " and cod_proveedor = ? and doc_estado = ? ";

	private static final String SQL_SUM_CANTIDAD_PRODUCTO = "	select coalesce(sum(det.cantidad),0) "
			+ "	from pd_documento_ent_detalle det "
			+ "	inner join pd_documento_ent enc "
			+ "	on det.cod_movimiento = enc.cod_movimiento "
			+ "	where enc.cod_empresa = ? "
			+ "	and enc.cod_periodo = ? "
			+ "	and enc.doc_estado = 'V' " + "	and det.cod_producto = ? ";

	private static final String SQL_GET_DETALLE_DOCUMENTO = "	SELECT pro.cod_producto,	"
			+ "	pro.producto, "
			+ "	det.cantidad, "
			+ "	det.precio_unitario, "
			+ "	det.doc_monto_gravado, "
			+ "	det.doc_monto_descuento, "
			+ "	det.doc_monto_total "
			+ "	from pd_documento_ent_detalle det "
			+ "	inner join pd_producto pro "
			+ "	on det.cod_producto = pro.cod_producto "
			+ "  where det.cod_movimiento = ? ";

	private static final String SQL_CAMBIAR_ESTADO_DOCTO = " update pd_documento_ent "
			+ " set doc_estado = ? ,"
			+ "  an_cod_movimiento = nextval('cod_movimiento_sq') , "
			+ "  an_fecha = current_timestamp, an_usuario = ? , "
			+ " log_updatedate = current_timestamp, log_updateuser = ? "
			+ "  where cod_movimiento = ? ";

	public PDDocumentoEnt(String usuario) {
		log = Logger.getLogger(getClass());
		this.usuario = usuario;
	}

	public Long getSecuencia(Connection conn) throws SQLException {

		return consultaEscalar(conn, SQL_GET_SEQUENCE, Long.class, null);
	}

	public boolean addDocumento(Connection conn, DocumentoEntEncabezadoDTO enc,
			List<DocumentoEntDetalleDTO> detalles) throws SQLException {

		Long codDocumento = getSecuencia(conn);

		boolean addEnc = dml(
				conn,
				SQL_ADD_ENCABEZADO,
				new Object[] { codDocumento, enc.getCod_periodo(),
						enc.getDoc_tipo(), enc.getDoc_serie(),
						enc.getDoc_numero(), enc.getDoc_fecha(),
						enc.getDoc_estado(), enc.getCod_empresa(),
						enc.getCod_tipo_movimiento(), enc.getCod_proveedor(),
						enc.getDoc_monto_descuento(),
						enc.getDoc_monto_gravado(), enc.getDoc_monto_iva(),
						enc.getDoc_monto_total(), usuario, }) > 0;

		if (addEnc) {
			for (DocumentoEntDetalleDTO det : detalles) {
				boolean addDet = dml(
						conn,
						SQL_ADD_DETALLE,
						new Object[] { codDocumento, det.getCod_producto(),
								det.getCantidad(), det.getDoc_monto_unitario(),
								det.getDoc_monto_gravado(),
								det.getDoc_monto_descuento(),
								det.getDoc_monto_iva(),
								det.getDoc_monto_total(), usuario }) > 0;

				if (!addDet) {
					log.debug("No fue posible insertar el producto: "
							+ det.getCod_producto());
					return false;
				}
			}
		} else {
			log.debug("No fue posible insertar el encabezado.");
			return false;
		}

		return true;
	}

	public List<DocumentoEntEncabezadoDTO> getEntradasPorTipo(Connection conn,
			Long codEmpresa, String codPeriodo, Long codTipoMovimiento)
			throws SQLException {

		return consultaLista(conn, SQL_GET_ENTRADAS,
				DocumentoEntEncabezadoDTO.class, new Object[] { codEmpresa,
						codPeriodo, codTipoMovimiento });

	}

	public DocumentoEntEncabezadoDTO buscarFacturaProveedor(Connection conn,
			DocumentoEntEncabezadoDTO enc) throws SQLException {

		return consultaDTO(
				conn,
				SQL_GET_DOCUMENTO,
				DocumentoEntEncabezadoDTO.class,
				new Object[] { enc.getDoc_tipo(), enc.getDoc_serie(),
						enc.getDoc_numero(), enc.getCod_proveedor(),
						enc.getDoc_estado() });

	}

	public int getCantidadPeriodo(Connection conn, Long codEmpresa,
			Long codProducto, String codPeriodo) throws SQLException {
		BigDecimal cantidad = new BigDecimal(0);

		cantidad = consultaEscalar(conn, SQL_SUM_CANTIDAD_PRODUCTO,
				BigDecimal.class, new Object[] { codEmpresa, codPeriodo,
						codProducto });

		return cantidad.intValue();
	}

	public List<DocumentoEntDetalleDTO> getDetalle(Connection conn,
			Long codMovimiento) throws SQLException {

		return consultaLista(conn, SQL_GET_DETALLE_DOCUMENTO,
				DocumentoEntDetalleDTO.class, new Object[] { codMovimiento });

	}

	public boolean anularDocumento(Connection conn, Long codMovimiento)
			throws SQLException {

		return dml(conn, SQL_CAMBIAR_ESTADO_DOCTO, new Object[] {
				EstadosDocumento.ANULADO.ESTADO, usuario, usuario,
				codMovimiento }) > 0;
	}

}
