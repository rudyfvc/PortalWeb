package com.portal.control.ingresos;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDDocumentoEnt;
import com.portal.bussines.PDPeriodo;
import com.portal.bussines.PDProducto;
import com.portal.bussines.PDProveedor;
import com.portal.dto.DocumentoEntDetalleDTO;
import com.portal.dto.DocumentoEntEncabezadoDTO;
import com.portal.dto.ProductoDTO;
import com.portal.dto.ProveedorDTO;
import com.portal.enums.Estados;
import com.portal.enums.EstadosDocumento;
import com.portal.enums.TiposMovimiento;
import com.portal.security.SecEmpresa;
import com.portal.security.SecUsuario;
import com.portal.utils.Utils;

public class WdwCompra extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwCompra;
	private Combobox cmbProducto, cmbEmpresa, cmbProveedor, cmbTipoDocumento;
	private Intbox txtCantidad;
	private Decimalbox txtPrecio, txtDescuento;
	private Listbox ltbDetalle;
	private Textbox txtNit, txtMarca, txtSerie, txtNumero, txtPeriodo;
	private Datebox txtFecha;
	private Label lblSubTotal, lblDescuento, lblTotal;

	private PDProveedor objProveedor;
	private PDProducto objProducto;
	private List<DocumentoEntDetalleDTO> items;

	public WdwCompra() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Compra";
		items = new ArrayList<DocumentoEntDetalleDTO>();
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarCatalogos();
	}

	public void onOK$txtDescuento(Event event) {

		if (cmbProducto.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione un producto.");
			cmbProducto.setFocus(true);
			return;
		}

		if (txtCantidad.getText() == null
				|| txtCantidad.getText().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese una cantidad.");
			txtCantidad.setFocus(true);
			return;
		}

		if (txtCantidad.getValue() <= 0) {
			showErrorMessage(nombreOperacion, "La cantidad debe ser mayor a 0.");
			txtCantidad.setFocus(true);
			return;
		}

		if (txtPrecio.getText() == null
				|| txtPrecio.getText().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese un precio.");
			txtPrecio.setFocus(true);
			return;
		}

		if (txtPrecio.getValue().compareTo(new BigDecimal(0)) != 1) {
			showErrorMessage(nombreOperacion, "El precio debe ser mayor a 0.");
			txtPrecio.setFocus(true);
			return;
		}

		if (txtDescuento.getValue() != null) {
			if (txtDescuento.getValue().doubleValue() > 0) {
				if (txtDescuento.getValue().doubleValue() > txtPrecio
						.getValue().doubleValue()) {
					showErrorMessage(nombreOperacion,
							"El descuento no puede ser mayor al precio.");
					txtDescuento.setFocus(true);
					return;
				}
			}
		}

		ProductoDTO proSeleccionado = cmbProducto.getSelectedItem().getValue();

		if (buscarItem(proSeleccionado.getCod_producto())) {
			showErrorMessage(nombreOperacion,
					"El producto ya se encuentra ingresado.");
			return;
		}

		DocumentoEntDetalleDTO item = new DocumentoEntDetalleDTO();
		item.setCantidad(Long.parseLong(txtCantidad.getText()));
		item.setCod_producto(Long.parseLong(proSeleccionado.getCod_producto()));
		item.setProducto(proSeleccionado.getProducto());

		item.setDoc_monto_unitario(Utils.round(txtPrecio.getValue()
				.doubleValue()));
		item.setDoc_monto_descuento_unitario(Utils.round(txtDescuento
				.getValue() == null ? 0 : Utils.round(txtDescuento.getValue()
				.doubleValue())));

		item.setDoc_monto_gravado(item.getCantidad()
				* Utils.round(txtPrecio.getValue().doubleValue()));

		item.setDoc_monto_descuento(txtDescuento.getValue() == null ? 0 : Utils
				.round(txtDescuento.getValue().doubleValue())
				* item.getCantidad());

		item.setDoc_monto_total(item.getDoc_monto_gravado()
				- item.getDoc_monto_descuento());

		items.add(item);
		ltbDetalle.setModel(new ListModelList<DocumentoEntDetalleDTO>(items));

		cmbProducto.setSelectedIndex(-1);
		cmbProducto.setValue(DEFAULT_LABEL_CMB);
		txtMarca.setText(null);
		txtCantidad.setText(null);
		txtPrecio.setText(null);
		txtDescuento.setText(null);

		cargarMontos();
	}

	public void onRemoverItem(Event e) {
		DocumentoEntDetalleDTO seleccion = (DocumentoEntDetalleDTO) e.getData();
		log.debug("Eliminando producto: " + seleccion.getCod_producto());
		items.remove(seleccion);
		ltbDetalle.setModel(new ListModelList<DocumentoEntDetalleDTO>(items));
	}

	public void onDoubleClick$ltbDetalle(Event e) {
		if (ltbDetalle.getItems().size() > 0) {
			if (ltbDetalle.getSelectedCount() > 0) {
				DocumentoEntDetalleDTO seleccion = ltbDetalle.getSelectedItem()
						.getValue();
				cmbProducto.setSelectedIndex(getIndexProducto(seleccion
						.getCod_producto().toString()));
				cargarMarcaProducto();
				txtCantidad.setText(seleccion.getCantidad().toString());
				txtPrecio.setText(seleccion.getDoc_monto_unitario().toString());
				txtDescuento.setText(seleccion
						.getDoc_monto_descuento_unitario().toString());
				items.remove(seleccion);
				ltbDetalle.setModel(new ListModelList<DocumentoEntDetalleDTO>(
						items));

				cargarMontos();
			}
		}
	}

	private int getIndexProducto(String codProducto) {
		int i = 0;
		for (Comboitem each : cmbProducto.getItems()) {
			ProductoDTO pro = each.getValue();
			if (codProducto.equalsIgnoreCase(pro.getCod_producto())) {
				return i;
			}
			i++;
		}

		return -1;
	}

	public void onChange$cmbProveedor() {
		cargarNitProveedor();
	}

	public void onChange$cmbProducto() {
		cargarMarcaProducto();
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

		if (txtSerie.getText() == null
				|| txtSerie.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese la serie de la factura.");
			return;
		}

		if (txtNumero.getText() == null
				|| txtNumero.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese el número de la factura.");
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

		if (cmbProveedor.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione proveedor.");
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
		ProveedorDTO prov = cmbProveedor.getSelectedItem().getValue();

		DocumentoEntEncabezadoDTO enc = new DocumentoEntEncabezadoDTO();

		enc.setCod_empresa(Long.parseLong(empresa.getCod_empresa()));
		enc.setCod_periodo(txtPeriodo.getText());
		enc.setCod_proveedor(Long.parseLong(prov.getCod_proveedor()));
		enc.setCod_tipo_movimiento((long) TiposMovimiento.COMPRA.CODIGO);
		enc.setDoc_estado(EstadosDocumento.VIGENTE.ESTADO);
		enc.setDoc_fecha(txtFecha.getText());

		Map<String, Double> montos = getMontos();
		enc.setDoc_monto_descuento(montos.get(MAP_MONTO_DESCUENTO));
		enc.setDoc_monto_gravado(montos.get(MAP_MONTO_SUBTOTAL));
		enc.setDoc_monto_total(montos.get(MAP_MONTO_TOTAL));
		enc.setDoc_monto_iva(enc.getDoc_monto_gravado() / 1.12 * 0.12);

		enc.setDoc_numero(Long.parseLong(txtNumero.getText()));
		enc.setDoc_serie(txtSerie.getText());
		enc.setDoc_tipo(cmbTipoDocumento.getSelectedItem().getValue()
				.toString());

		PDDocumentoEnt objDocumento = new PDDocumentoEnt(userLoguiado);
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

			DocumentoEntEncabezadoDTO docto = objDocumento
					.buscarFacturaProveedor(conn, enc);

			if (docto == null) {
				if (objDocumento.addDocumento(conn, enc, items)) {
					showInformationMessage(nombreOperacion,
							"Documento almacenado exitosamente.");
					conn.commit();
					Events.echoEvent("onClose", wdwCompra, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible guardar el documento.");
					conn.rollback();
				}
			} else {
				showErrorMessage(nombreOperacion,
						"El documento del proveedor ya se encuentra ingresado.");
				return;
			}

		} catch (Exception e) {
			conn.rollback();
			throw e;
		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarNitProveedor() {
		ProveedorDTO select = cmbProveedor.getSelectedItem().getValue();

		if (select != null) {
			txtNit.setText(select.getNit());
		} else {
			txtNit.setText(null);
		}
	}

	private void cargarMarcaProducto() {
		ProductoDTO select = cmbProducto.getSelectedItem().getValue();

		if (select != null) {
			txtMarca.setText(select.getMarca());
		} else {
			txtMarca.setText(null);
		}
	}

	private void cargarCatalogos() throws SQLException, NamingException {

		objProveedor = new PDProveedor(userLoguiado);
		objProducto = new PDProducto(userLoguiado);

		Connection conn = null;
		try {
			conn = Utils.getConnection();

			for (ProveedorDTO each : objProveedor.getProveedoresPorEstado(conn,
					Estados.ACTIVO.CODIGO)) {
				Comboitem item = new Comboitem();
				item.setValue(each);
				item.setLabel(each.getCod_proveedor() + " - "
						+ each.getNombres() + " " + each.getApellidos());
				item.setParent(cmbProveedor);
			}

			for (ProductoDTO each : objProducto.getProductosEstado(conn,
					Estados.ACTIVO.CODIGO)) {
				Comboitem item = new Comboitem();
				item.setValue(each);
				item.setLabel(each.getCod_producto() + " - "
						+ each.getProducto());
				item.setParent(cmbProducto);
			}

			SecUsuario user = (SecUsuario) session.getAttribute(ATT_KEY_USER);

			for (SecEmpresa each : user.getEmpresas()) {
				Comboitem item = new Comboitem();
				item.setValue(each);
				item.setLabel(each.getEmp_nombre());
				item.setParent(cmbEmpresa);
			}

			if (user.getEmpresas().size() == 1) {
				cmbEmpresa.setSelectedIndex(0);
				cargarPeriodo();
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	public void onChange$cmbEmpresa() {
		cargarPeriodo();
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

		for (DocumentoEntDetalleDTO det : items) {
			subtotal += det.getDoc_monto_gravado();
			descuento += det.getDoc_monto_descuento();
			total += det.getDoc_monto_total();
		}

		lblSubTotal.setValue(formatter.format(subtotal));
		lblDescuento.setValue(formatter.format(descuento));
		lblTotal.setValue(formatter.format(total));

	}

	private static final String MAP_MONTO_DESCUENTO = "doc_monto_descuento";
	private static final String MAP_MONTO_TOTAL = "doc_monto_total";
	private static final String MAP_MONTO_SUBTOTAL = "doc_monto_gravado";

	private Map<String, Double> getMontos() {
		Map<String, Double> montos = new HashMap<String, Double>();

		double subtotal = 0;
		double descuento = 0;
		double total = 0;

		for (DocumentoEntDetalleDTO det : items) {
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

		for (DocumentoEntDetalleDTO item : items) {
			if (codProducto.equalsIgnoreCase(item.getCod_producto().toString())) {
				return true;
			}
		}

		return false;
	}

}
