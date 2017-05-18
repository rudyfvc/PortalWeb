package com.portal.control.egresos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDCliente;
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

public class WdwVenta extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwVenta;
	private Combobox cmbEmpresa;
	private Textbox txtNit, txtMarca, txtSerie, txtNumero, txtPeriodo,
			txtCodProducto, txtCliente, txtProducto, txtUnidadMedida, txtTipo;
	private Intbox txtCantidad;
	private Listbox ltbDetalle;
	private Datebox txtFecha;
	private Label lblSubTotal, lblDescuento, lblTotal;

	private PDCliente objCliente;
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

	public WdwVenta() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Venta";
		items = new ArrayList<DocumentoSalDetalleDTO>();
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		deleteFile();
		cargarCatalogos();
		cargarResolucion();
	}

	public void onOK$txtNit(Event event) throws NamingException, SQLException {
		Connection conn = null;
		objCliente = new PDCliente(userLoguiado);

		if (txtNit.getText() == null
				|| txtNit.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese un NIT.");
			return;
		}

		txtNit.setText(txtNit.getText().toUpperCase());
		String nit = txtNit.getText().trim();

		try {
			conn = Utils.getConnection();
			cliente = objCliente.getClientePorNit(conn, nit);

			if (cliente == null) {
				showErrorMessage(nombreOperacion, "Cliente no encontrado.");
				txtCliente.setText(null);
				return;
			}

			txtCliente.setText(cliente.getNombres() + " "
					+ cliente.getApellidos());
			txtCodProducto.setFocus(true);
		} finally {
			Utils.closeConnection(conn);
		}
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

		det.setDoc_monto_descuento_unitario(Utils.round(producto.getPrecio()
				* (producto.getDesc_porcentaje() / 100)));
		det.setDoc_monto_descuento(Utils.round(det
				.getDoc_monto_descuento_unitario() * det.getCantidad()));
		det.setDoc_monto_gravado(Utils.round(det.getCantidad()
				* det.getDoc_monto_unitario()));
		det.setDoc_monto_total(Utils.round(det.getDoc_monto_gravado()
				- det.getDoc_monto_descuento()));

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
				cargarMontos();
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
		cargarMontos();

	}

	public void onClick$btnGuardar() throws NamingException, SQLException {

		if (cmbEmpresa.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione un empresa para realizar el ingreso.");
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
			showErrorMessage(nombreOperacion, "Tipo de factura inválido");
		}

		if (txtSerie.getText() == null
				|| txtSerie.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Serie de factura inválida");
			return;
		}

		if (txtNumero.getText() == null
				|| txtNumero.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"No posee correlativos de facturación.");
			return;
		}

		if (!Utils.isNumeric(txtNumero.getText())) {
			showErrorMessage(nombreOperacion, "Número de factura inválida.");
			return;
		}

		if (txtFecha.getText() == null
				|| txtFecha.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese fecha.");
			return;
		}

		if (ltbDetalle.getModel() == null
				|| ltbDetalle.getModel().getSize() == 0) {
			showErrorMessage(nombreOperacion,
					"No ha ingresado detalle de la compra.");
			return;
		}

		log.debug("Datos validados, guardando venta");

		SecEmpresa empresa = cmbEmpresa.getSelectedItem().getValue();

		DocumentoSalEncabezadoDTO enc = new DocumentoSalEncabezadoDTO();

		enc.setCod_empresa(Long.parseLong(empresa.getCod_empresa()));
		enc.setCod_periodo(txtPeriodo.getText());
		enc.setCod_cliente(Long.parseLong(cliente.getCod_cliente()));
		enc.setCod_tipo_movimiento((long) TiposMovimiento.VENTA.CODIGO);
		enc.setDoc_estado(EstadosDocumento.VIGENTE.ESTADO);
		enc.setDoc_fecha(txtFecha.getText());

		Map<String, Double> montos = getMontos();
		enc.setDoc_monto_descuento(montos.get(MAP_MONTO_DESCUENTO));
		enc.setDoc_monto_gravado(montos.get(MAP_MONTO_SUBTOTAL));
		enc.setDoc_monto_total(montos.get(MAP_MONTO_TOTAL));
		enc.setDoc_monto_iva(enc.getDoc_monto_gravado() / 1.12 * 0.12);

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

	public void onChange$cmbEmpresa() {
		cargarPeriodo();
	}

	private static final String MAP_MONTO_DESCUENTO = "doc_monto_descuento";
	private static final String MAP_MONTO_TOTAL = "doc_monto_total";
	private static final String MAP_MONTO_SUBTOTAL = "doc_monto_gravado";

	public void onTimer$timer() {
		try {
			FileReader fr = null;
			BufferedReader br = null;
			if (fichero.exists()) {
				fr = new FileReader(fichero);
				br = new BufferedReader(fr);
				String linea = br.readLine();
				br.close();
				fr.close();
				txtCodProducto.setText(linea);
				onOK$txtCodProducto();
				fichero.delete();
			}
		} catch (Exception e) {
			showErrorMessage(nombreOperacion,
					"Ocurrió un error con el escaner de productos.");
		}
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

	private void cargarMontos() {
		double subtotal = 0;
		double descuento = 0;
		double total = 0;
		log.debug("cargando montos...");
		NumberFormat formatter = new DecimalFormat("#,##0.00");

		for (DocumentoSalDetalleDTO det : items) {
			subtotal += det.getDoc_monto_gravado();
			descuento += det.getDoc_monto_descuento();
			total += det.getDoc_monto_total();
		}

		lblSubTotal.setValue(formatter.format(subtotal));
		lblDescuento.setValue(formatter.format(descuento));
		lblTotal.setValue(formatter.format(total));

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

	private Map<String, Double> getMontos() {
		Map<String, Double> montos = new HashMap<String, Double>();

		double subtotal = 0;
		double descuento = 0;
		double total = 0;

		for (DocumentoSalDetalleDTO det : items) {
			subtotal += det.getDoc_monto_gravado();
			descuento += det.getDoc_monto_descuento();
			total += det.getDoc_monto_total();
		}

		montos.put(MAP_MONTO_SUBTOTAL, subtotal);
		montos.put(MAP_MONTO_DESCUENTO, descuento);
		montos.put(MAP_MONTO_TOTAL, total);

		return montos;

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
					PDResolucion.TIPO_FACTURA);
			if (res != null) {
				txtSerie.setText(res.getDoc_serie());
				txtTipo.setText(res.getDoc_tipo());

				Long correlativo = objResolucion.getCorrelativo(conn,
						res.getCod_resolucion());

				if (correlativo != null) {
					txtNumero.setText(correlativo.toString());
				}

			} else {
				showErrorMessage(nombreOperacion,
						"No se encontró resolución de facturación para la empresa.");
				return;
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarCatalogos() throws SQLException, NamingException {
		txtFecha.setValue(new Date());
		objCliente = new PDCliente(userLoguiado);
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
		}

		txtNit.setFocus(true);

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
