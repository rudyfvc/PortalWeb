package com.portal.control.inventario;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDConfiguracion;
import com.portal.bussines.PDDocumentoSal;
import com.portal.bussines.PDInventario;
import com.portal.bussines.PDPeriodo;
import com.portal.bussines.PDProducto;
import com.portal.bussines.PDResolucion;
import com.portal.dto.ClienteDTO;
import com.portal.dto.DocumentoSalDetalleDTO;
import com.portal.dto.DocumentoSalEncabezadoDTO;
import com.portal.dto.ProductoDTO;
import com.portal.dto.ResolucionDTO;
import com.portal.enums.EstadosDocumento;
import com.portal.enums.TiposMovimiento;
import com.portal.security.SecEmpresa;
import com.portal.utils.Utils;

public class WdwAjuste extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwVenta;
	private Combobox cmbEmpresa, cmbTipoAjuste;
	private Textbox txtMarca, txtSerie, txtNumero, txtPeriodo, txtCodProducto,
			txtMotivoAjuste, txtProducto, txtUnidadMedida, txtTipo;
	private Intbox txtCantidad;
	private Listbox ltbDetalle;
	private Datebox txtFecha;

	private PDProducto objProducto;
	private PDInventario objInventario;
	private PDResolucion objResolucion;
	private PDDocumentoSal objDocumento;
	private List<DocumentoSalDetalleDTO> items;

	private static File fichero = null;
	private static ProductoDTO producto = null;
	private static ClienteDTO cliente = null;
	private static ResolucionDTO res = null;
	private static SecEmpresa empresa = null;

	public WdwAjuste() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Ajuste de Inventario";
		items = new ArrayList<DocumentoSalDetalleDTO>();
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		deleteFile();
		cargarCatalogos();
		cargarResolucion();
	}

	public void onOK$txtCodProducto() throws NamingException, SQLException {
		cargarProducto();
	}

	public void onOK$txtCantidad() throws SQLException, NamingException {
		Connection conn = null;
		objInventario = new PDInventario(userLoguiado);
		SecEmpresa empr = null;
		String codPeriodo = null;

		if (cmbEmpresa.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione una empresa.");
			return;
		} else {
			empr = cmbEmpresa.getSelectedItem().getValue();
		}

		if (txtPeriodo.getText() == null
				|| txtPeriodo.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"La empresa seleccionada no posee un periodo para operar.");
			return;
		} else {
			codPeriodo = txtPeriodo.getText().trim();
		}

		if (txtCodProducto.getText() == null
				|| txtCodProducto.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese un producto.");
			return;
		}

		if (txtProducto.getText() == null
				|| txtProducto.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Información del producto incompleta.");
			return;
		}

		if (txtMarca.getText() == null
				|| txtMarca.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Información del producto incompleta.");
			return;
		}

		if (txtUnidadMedida.getText() == null
				|| txtUnidadMedida.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Información del producto incompleta.");
			return;
		}

		if (txtCantidad.getText() == null
				|| txtCantidad.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese una cantidad.");
			return;
		}

		if (txtCantidad.getValue() <= 0) {
			showErrorMessage(nombreOperacion, "La cantidad debe ser mayor a 0.");
			return;
		}

		DocumentoSalDetalleDTO det = new DocumentoSalDetalleDTO();
		Long codProducto = Long.parseLong(producto.getCod_producto());
		det.setCod_producto(codProducto);
		det.setProducto(producto.getProducto());
		det.setCantidad(Long.parseLong(txtCantidad.getText()));
		det.setDoc_monto_unitario(producto.getPrecio());

		log.debug(producto.getPrecio());
		log.debug(producto.getDesc_porcentaje());

		det.setDoc_monto_descuento_unitario(0D);
		det.setDoc_monto_descuento(0D);
		det.setDoc_monto_gravado(Utils.round(0D));
		det.setDoc_monto_total(0D);

		if (buscarItem(det.getCod_producto().toString())) {
			showErrorMessage(nombreOperacion,
					"El producto ya se encuentra agregado.");
			return;
		}

		try {
			conn = Utils.getConnection();

			Long existencia = objInventario.getExistenciaProducto(conn,
					Long.parseLong(empr.getCod_empresa()), codPeriodo,
					codProducto);

			log.debug("cantidad en existencia: " + existencia);

			if (existencia.intValue() >= txtCantidad.getValue()) {
				items.add(det);
				ltbDetalle.setModel(new ListModelList<DocumentoSalDetalleDTO>(
						items));
				limpiarDatosProducto();
			} else {
				showErrorMessage(
						nombreOperacion,
						"No posee suficiente producto para despachar la cantidad ingresada, disponible: "
								+ existencia);
				return;
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	public void onRemoverItem(Event e) throws NamingException, SQLException {
		DocumentoSalDetalleDTO seleccion = (DocumentoSalDetalleDTO) e.getData();
		log.debug("Eliminando producto: " + seleccion.getCod_producto());

		items.remove(seleccion);
		ltbDetalle.setModel(new ListModelList<DocumentoSalDetalleDTO>(items));
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {

		if (cmbEmpresa.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione un empresa para realizar el ingreso de datos.");
			return;
		}

		if (txtPeriodo.getText() == null
				|| txtPeriodo.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"La empresa no tiene periodo asignado.");
			return;
		}

		if (txtTipo.getText() == null
				|| txtTipo.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Tipo de ajuste inválido");
		}

		if (txtSerie.getText() == null
				|| txtSerie.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Serie de ajuste inválida");
			return;
		}

		if (txtNumero.getText() == null
				|| txtNumero.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"No posee correlativos para crear ajustes..");
			return;
		}

		if (!Utils.isNumeric(txtNumero.getText())) {
			showErrorMessage(nombreOperacion, "Número de ajuste inválido.");
			return;
		}

		if (txtFecha.getText() == null
				|| txtFecha.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese fecha.");
			return;
		}

		if (cmbTipoAjuste.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione un tipo de ajuste.");
			return;
		}

		if (txtMotivoAjuste.getText() == null
				|| txtMotivoAjuste.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Detalle el motivo por el cual realiza el ajuste de inventario");
			return;
		}

		if (ltbDetalle.getModel() == null
				|| ltbDetalle.getModel().getSize() == 0) {
			showErrorMessage(nombreOperacion,
					"No ha ingresado detalle para el ajuste..");
			return;
		}

		log.debug("Datos validados, guardando ajuste");

		SecEmpresa empresa = cmbEmpresa.getSelectedItem().getValue();

		DocumentoSalEncabezadoDTO enc = new DocumentoSalEncabezadoDTO();

		enc.setCod_empresa(Long.parseLong(empresa.getCod_empresa()));
		enc.setCod_periodo(txtPeriodo.getText());
		enc.setCod_cliente(Long.parseLong(cliente.getCod_cliente()));
		enc.setCod_tipo_movimiento((long) TiposMovimiento.VENTA.CODIGO);
		enc.setDoc_estado(EstadosDocumento.VIGENTE.ESTADO);
		enc.setDoc_fecha(txtFecha.getText());

		enc.setDoc_monto_descuento(0d);
		enc.setDoc_monto_gravado(0d);
		enc.setDoc_monto_total(0d);
		enc.setDoc_monto_iva(0d);

		enc.setDoc_tipo(txtTipo.getText());
		enc.setDoc_serie(txtSerie.getText());
		enc.setDoc_numero(Long.parseLong(txtNumero.getText()));

		objDocumento = new PDDocumentoSal(userLoguiado);
		objResolucion = new PDResolucion(userLoguiado);

		PDPeriodo objPeriodo = new PDPeriodo(userLoguiado);

		Connection conn = null;

		try {
			conn = Utils.getConnection();
			conn.setAutoCommit(false);

			String estadoPeriodo = objPeriodo.getEstadoPeriodo(conn,
					enc.getCod_empresa(), txtPeriodo.getText());

			if (estadoPeriodo == null) {
				showErrorMessage(nombreOperacion,
						"El estado del periodo es inválido.");
				return;
			}

			if (!estadoPeriodo.equalsIgnoreCase(PDPeriodo.ESTADO_ABIERTO)) {
				showErrorMessage(nombreOperacion,
						"El periodo ya se encuentra cerrado.");
				return;
			}

			String periodoFecha = Utils.getPeriodo(txtFecha.getValue());

			log.debug("periodo de la fecha: " + periodoFecha);

			if (!txtPeriodo.getText().equalsIgnoreCase(periodoFecha)) {
				showErrorMessage(nombreOperacion,
						"La fecha que está ingresando no corresponde al periodo en curso.");
				return;
			}

			DocumentoSalEncabezadoDTO docto = objDocumento
					.buscarFacturaCliente(conn, enc);

			if (docto == null) {
				if (objDocumento.addDocumento(conn, enc, items)) {
					if (objResolucion.setUsado(conn, res.getCod_resolucion(),
							enc.getDoc_tipo(), enc.getDoc_serie(),
							enc.getDoc_numero())) {
						showInformationMessage(nombreOperacion,
								"Documento almacenado exitosamente.");
						Utils.commit(conn);
						Events.echoEvent("onClose", wdwVenta, null);
					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible marcar la factura como baja.");
						Utils.rollback(conn);
					}
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible guardar el documento.");
					Utils.rollback(conn);
				}
			} else {
				showErrorMessage(nombreOperacion,
						"El documento del proveedor ya se encuentra ingresado.");
				return;
			}

		} catch (Exception e) {
			Utils.rollback(conn);
			throw e;
		} finally {
			Utils.closeConnection(conn);
		}

	}

	public void onClick$btnCancelar() throws SQLException, NamingException {
		if (fichero.exists())
			fichero.delete();

		wdwVenta.detach();
	}

	public void onChange$cmbEmpresa() throws NamingException, SQLException {
		empresa = cmbEmpresa.getSelectedItem().getValue();
		cargarPeriodo();
		cargarResolucion();
	}

	private void cargarPeriodo() {
		if (cmbEmpresa.getSelectedIndex() > -1) {
			SecEmpresa empr = cmbEmpresa.getSelectedItem().getValue();
			if (empr.getPeriodo() != null) {
				txtPeriodo.setText(empr.getPeriodo().getCod_periodo());
			} else {
				txtPeriodo.setText(null);
			}
		}

	}

	private void deleteFile() throws NamingException, SQLException {
		Connection conn = null;
		PDConfiguracion objConf = new PDConfiguracion();

		try {
			conn = Utils.getConnection();
			String pathFile = objConf.getValorConf(conn,
					PDConfiguracion.CONF_PATH_FILE_SCAN);

			pathFile += userLoguiado + ".txt";

			log.debug("se eliminara el archivo: " + pathFile);

			fichero = new File(pathFile);

			if (fichero.exists()) {
				fichero.delete();
			}

		} finally {
			Utils.closeConnection(conn);
		}
	}

	private boolean buscarItem(String codProducto) {

		for (DocumentoSalDetalleDTO item : items) {
			if (codProducto.equalsIgnoreCase(item.getCod_producto().toString())) {
				return true;
			}
		}

		return false;
	}

	private void limpiarDatosProducto() {
		txtCodProducto.setText(null);
		txtProducto.setText(null);
		txtUnidadMedida.setText(null);
		txtMarca.setText(null);
		txtCantidad.setText(null);
		txtCodProducto.setFocus(true);
	}

	private void cargarResolucion() throws NamingException, SQLException {
		Connection conn = null;
		objResolucion = new PDResolucion(userLoguiado);

		try {
			conn = Utils.getConnection();
			res = objResolucion.getResolucion(conn,
					Long.parseLong(empresa.getCod_empresa()),
					PDResolucion.TIPO_AJUSTE);
			if (res != null) {
				txtSerie.setText(res.getDoc_serie());
				txtTipo.setText(res.getDoc_tipo());

				Long correlativo = objResolucion.getCorrelativo(conn,
						res.getCod_resolucion());

				if (correlativo != null) {
					txtNumero.setText(correlativo.toString());
				}

			} else {
				txtSerie.setText(null);
				txtTipo.setText(null);
				txtNumero.setText(null);

				showErrorMessage(nombreOperacion,
						"No se encontró resolución de ajustes de inventario para la empresa.");
				return;
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarCatalogos() throws SQLException, NamingException {
		txtFecha.setValue(new Date());
		objProducto = new PDProducto(userLoguiado);

		for (SecEmpresa each : usuario.getEmpresas()) {
			Comboitem item = new Comboitem();
			item.setValue(each);
			item.setLabel(each.getEmp_nombre());
			item.setParent(cmbEmpresa);
		}

		if (usuario.getEmpresas().size() > 0) {
			cmbEmpresa.setSelectedIndex(0);
			empresa = cmbEmpresa.getSelectedItem().getValue();
			cargarPeriodo();
			cargarResolucion();
		}

	}

	private void cargarProducto() throws NamingException, SQLException {
		Connection conn = null;
		objProducto = new PDProducto(userLoguiado);

		if (txtCodProducto.getText() == null
				|| txtCodProducto.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese un código de producto.");
			return;
		}

		if (!Utils.isNumeric(txtCodProducto.getText().trim())) {
			showErrorMessage(nombreOperacion, "Código de producto inválido.");
			return;
		}

		Long codProducto = Long.parseLong(txtCodProducto.getValue());

		try {
			conn = Utils.getConnection();
			producto = objProducto.getProductoPorCodigo(conn, codProducto);
			if (producto == null) {
				showErrorMessage(nombreOperacion, "Producto no encontrado.");
				limpiarDatosProducto();
				return;
			}

			if (producto.getPrecio() == null) {
				showErrorMessage(nombreOperacion,
						"El producto no tiene precio configurado");
				return;
			}

			txtProducto.setText(producto.getProducto());
			txtUnidadMedida.setText(producto.getUnidad_medida());
			txtMarca.setText(producto.getMarca());
			txtCantidad.setFocus(true);
		} finally {
			Utils.closeConnection(conn);
		}

	}
}
