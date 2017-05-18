package com.portal.control.inventario;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDProducto;
import com.portal.dto.ProductoDTO;
import com.portal.utils.Utils;

public class WdwPrecio extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwProductoPrecio;

	private Textbox txtProducto, txtTipoProducto, txtUnidadMedida, txtMarca;
	private Decimalbox txtPrecio;
	private Decimalbox txtDescuento;

	private ProductoDTO producto;
	private PDProducto objProducto;

	public WdwPrecio() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Precios";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		producto = (ProductoDTO) arg.get(KEY_ARG_DTO);
		cargarInfo();
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		Connection conn = null;
		objProducto = new PDProducto(userLoguiado);

		if (txtPrecio.getText() == null
				|| txtPrecio.getText().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese un precio.");
			return;
		}

		if (txtPrecio.getValue().doubleValue() < 0) {
			showErrorMessage(nombreOperacion,
					"El precio no puede ser menor a 0");
			return;
		}

		Double descuento_porc = 0D;

		if (txtDescuento.getValue() != null) {
			descuento_porc = txtDescuento.getValue().doubleValue();

			if (descuento_porc > 100) {
				Clients.showNotification(
						"El porcentaje de descuento no puede ser mayor que 100",
						txtDescuento);
				return;
			}

		}

		Double precio = txtPrecio.getValue().doubleValue();
		Long codProducto = Long.parseLong(producto.getCod_producto());

		try {
			conn = Utils.getConnection();

			if (objProducto.editarPrecio(conn, precio, descuento_porc,
					codProducto)) {
				showInformationMessage(nombreOperacion,
						"Operación realizada con éxito.");
				Events.echoEvent("onClose", wdwProductoPrecio, null);
			} else {
				showErrorMessage(nombreOperacion,
						"No fue posible guardar el precio.");
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarInfo() throws NamingException, SQLException {

		txtProducto.setText(producto.getProducto());
		txtTipoProducto.setText(producto.getTipo_producto());
		txtUnidadMedida.setText(producto.getUnidad_medida());
		txtMarca.setText(producto.getMarca());
		if (producto.getPrecio() != null) {
			txtPrecio.setValue(producto.getPrecio().toString());
		}

		if (producto.getDesc_porcentaje() != null) {
			txtDescuento.setValue(producto.getDesc_porcentaje().toString());
		}

	}

}
