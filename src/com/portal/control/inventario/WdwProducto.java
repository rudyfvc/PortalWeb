package com.portal.control.inventario;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDMarca;
import com.portal.bussines.PDProducto;
import com.portal.bussines.PDTipoProducto;
import com.portal.bussines.PDUnidadMedida;
import com.portal.dto.MarcaDTO;
import com.portal.dto.ProductoDTO;
import com.portal.dto.TipoProductoDTO;
import com.portal.dto.UnidadMedidaDTO;
import com.portal.utils.Utils;

public class WdwProducto extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwProducto;

	private Combobox cmbTipoProducto;
	private Combobox cmbUnidadMedida;
	private Combobox cmbMarca;
	private Textbox txtCodigoProducto, txtProducto;

	private PDTipoProducto objTipoProducto;
	private PDUnidadMedida objUnidadMedida;
	private PDMarca objMarca;
	private PDProducto objProducto;

	public WdwProducto() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Producto";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarTiposProducto();
		cargarMarcas();
		cargarUnidadesMedida();
		cargarInfoProducto();
	}

	public void onClick$btnGuardar() throws SQLException, NamingException {
		log.debug("guardando producto...");

		if (txtProducto.getText() == null
				|| txtProducto.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese una descripción del producto.");
			return;
		}

		if (cmbTipoProducto.getValue() == null
				|| cmbTipoProducto.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione un tipo de producto.");
			return;
		}

		if (cmbMarca.getValue() == null || cmbMarca.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione una marca de producto.");
			return;
		}

		if (cmbUnidadMedida.getValue() == null
				|| cmbUnidadMedida.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione una unidad de medida de producto.");
			return;
		}

		String producto = txtProducto.getText().trim();

		objProducto = new PDProducto(userLoguiado);

		Connection conn = null;
		TipoProductoDTO tipoProducto = cmbTipoProducto.getSelectedItem()
				.getValue();
		UnidadMedidaDTO unidadMedida = cmbUnidadMedida.getSelectedItem()
				.getValue();
		MarcaDTO marca = cmbMarca.getSelectedItem().getValue();

		long codTipoProducto = Long.parseLong(tipoProducto
				.getCod_tipo_producto());
		long codMarca = Long.parseLong(marca.getCod_marca());
		long codUnidadMedida = Long.parseLong(unidadMedida
				.getCod_unidad_medida());

		try {
			conn = Utils.getConnection();
			if (arg.get(KEY_ARG_OPERACION) != null
					&& arg.get(KEY_ARG_OPERACION).toString()
							.equals(OPERACION_EDITAR)) {

				ProductoDTO dtoProd = (ProductoDTO) arg.get(KEY_ARG_DTO);
				if (objProducto.modificarProducto(conn,
						Long.parseLong(dtoProd.getCod_producto()), producto,
						codTipoProducto, codMarca, codUnidadMedida)) {
					showInformationMessage(nombreOperacion,
							"Producto almacenado exitosamente.");
					Events.echoEvent("onClose", wdwProducto, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible actualizar el producto.");
				}
			} else {

				if (objProducto.addProducto(conn, producto, codTipoProducto,
						codMarca, codUnidadMedida)) {
					showInformationMessage(nombreOperacion,
							"Producto almacenado exitosamente.");
					Events.echoEvent("onClose", wdwProducto, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible ingresar el nuevo producto.");
				}

			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarTiposProducto() throws NamingException, SQLException {
		objTipoProducto = new PDTipoProducto(userLoguiado);

		Connection conn = null;

		try {
			conn = Utils.getConnection();
			for (TipoProductoDTO tipo : objTipoProducto
					.getTiposProductoActivos(conn)) {

				Comboitem item = new Comboitem();
				item.setValue(tipo);
				item.setLabel(tipo.getTipo_producto());

				item.setParent(cmbTipoProducto);

			}
		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarMarcas() throws NamingException, SQLException {
		objMarca = new PDMarca(userLoguiado);
		Connection conn = null;

		try {
			conn = Utils.getConnection();
			for (MarcaDTO each : objMarca.getMarcasActivas(conn)) {

				Comboitem item = new Comboitem();
				item.setValue(each);
				item.setLabel(each.getMarca());

				item.setParent(cmbMarca);

			}
		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarUnidadesMedida() throws NamingException, SQLException {
		objUnidadMedida = new PDUnidadMedida(userLoguiado);

		Connection conn = null;
		try {
			conn = Utils.getConnection();
			for (UnidadMedidaDTO each : objUnidadMedida
					.getUnidadesMedidaActivas(conn)) {

				Comboitem item = new Comboitem();
				item.setValue(each);
				item.setLabel(each.getUnidad_medida());

				item.setParent(cmbUnidadMedida);

			}
		} finally {
			Utils.closeConnection(conn);
		}
	}

	private void cargarInfoProducto() {
		if (arg.get(KEY_ARG_OPERACION) != null
				&& arg.get(KEY_ARG_OPERACION).toString()
						.equals(OPERACION_EDITAR)) {
			wdwProducto.setTitle("Edición de Producto");
			txtCodigoProducto.setText("");
			ProductoDTO producto = (ProductoDTO) arg.get(KEY_ARG_DTO);
			txtCodigoProducto.setText(producto.getCod_producto());
			txtProducto.setText(producto.getProducto());

			for (Comboitem item : cmbTipoProducto.getItems()) {
				TipoProductoDTO tipo = item.getValue();
				if (tipo.getCod_tipo_producto().equalsIgnoreCase(
						producto.getCod_tipo_producto())) {
					cmbTipoProducto.setSelectedItem(item);
					break;
				}
			}

			for (Comboitem item : cmbMarca.getItems()) {
				MarcaDTO tipo = item.getValue();
				if (tipo.getCod_marca().equalsIgnoreCase(
						producto.getCod_marca())) {
					cmbMarca.setSelectedItem(item);
					break;
				}
			}

			for (Comboitem item : cmbUnidadMedida.getItems()) {
				UnidadMedidaDTO tipo = item.getValue();
				if (tipo.getCod_unidad_medida().equalsIgnoreCase(
						producto.getCod_unidad_medida())) {
					cmbUnidadMedida.setSelectedItem(item);
					break;
				}
			}

		} else {
			txtCodigoProducto.setText("Autogenerado");
			wdwProducto.setTitle("Nuevo Producto");
		}

	}

}
