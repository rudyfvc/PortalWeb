package com.portal.control.catalogos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Caption;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDTipoProducto;
import com.portal.dto.TipoProductoDTO;
import com.portal.enums.Estados;
import com.portal.utils.Utils;

public class WdwTipoProducto extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwTipoProducto() {
		log = Logger.getLogger(getClass());
	}

	private Listbox ltbTipoProducto;
	private Textbox txtTipoProducto;
	private Caption cptAccion;

	private PDTipoProducto objTipoProducto;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		nombreOperacion = "Tipo Producto";
		cargarTipoProductos();
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		if (txtTipoProducto == null
				|| txtTipoProducto.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese una descripción para el tipo de producto.");
			return;
		}

		objTipoProducto = new PDTipoProducto(userLoguiado);

		Connection conn = null;
		try {
			conn = Utils.getConnection();

			if (!objTipoProducto.buscarTipoProducto(conn, txtTipoProducto
					.getText().trim())) {

				if (operacion.equalsIgnoreCase(OPERACION_EDITAR)) {
					if (objTipoProducto.actualizarTipoProducto(conn,
							Long.parseLong(itemSeleccionado
									.getCod_tipo_producto()), txtTipoProducto
									.getText().trim())) {
						showInformationMessage(nombreOperacion,
								"Tipo de producto actualizado correctamente.");
						cargarTipoProductos();
					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible guardar el tipo de producto.");
					}
					operacion = OPERACION_NUEVO;
					txtTipoProducto.setText(null);
					cptAccion.setLabel("Nuevo Item");
				} else {

					if (objTipoProducto.addTipoProducto(conn, txtTipoProducto
							.getText().trim())) {
						showInformationMessage(nombreOperacion,
								"Tipo de producto almacenado correctamente.");
						txtTipoProducto.setText(null);
						cargarTipoProductos();
					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible guardar el tipo de producto.");
					}
				}
			} else {
				showErrorMessage(nombreOperacion,
						"El valor " + txtTipoProducto.getText()
								+ " ya se encuentra ingresado.");
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarTipoProductos() throws NamingException, SQLException {
		objTipoProducto = new PDTipoProducto(userLoguiado);
		Connection conn = null;
		try {
			conn = Utils.getConnection();

			List<TipoProductoDTO> lista = objTipoProducto
					.getTiposProducto(conn);

			log.debug("tmaño lista " + lista.size());

			ltbTipoProducto.setModel(new ListModelList<TipoProductoDTO>(lista));
		} finally {
			Utils.closeConnection(conn);
		}

	}

	private String operacion = "";
	TipoProductoDTO itemSeleccionado = null;

	public void onUpdateItem(final Event event) {
		itemSeleccionado = (TipoProductoDTO) event.getData();
		txtTipoProducto.setText(itemSeleccionado.getTipo_producto());
		operacion = OPERACION_EDITAR;
		cptAccion.setLabel("Editando Item "
				+ itemSeleccionado.getCod_tipo_producto());
	}

	public void onHabilitarItem(final Event event) throws NamingException,
			SQLException {
		Connection portal = null;
		objTipoProducto = new PDTipoProducto(userLoguiado);
		final TipoProductoDTO itemSeleccionado = (TipoProductoDTO) event
				.getData();

		String mensaje = "";
		int estado = 0;

		if (itemSeleccionado.getEstado().equalsIgnoreCase(Estados.ACTIVO.name())) {
			mensaje = "Tipo producto desactivado correctamente.";
			estado = Estados.INACTIVO.CODIGO;
		} else {
			mensaje = "Tipo producto activado correctamente.";
			estado = Estados.ACTIVO.CODIGO;
		}

		try {
			portal = Utils.getConnection();
			if (objTipoProducto.cambiarEstado(portal,
					Long.parseLong(itemSeleccionado.getCod_tipo_producto()),
					estado)) {
				showInformationMessage(nombreOperacion, mensaje);
				cargarTipoProductos();
			} else {
				showErrorMessage(nombreOperacion,
						"No fue posible realizar la operación, intente de nuevo.");
			}

		} finally {
			Utils.closeConnection(portal);
		}

	}
}
