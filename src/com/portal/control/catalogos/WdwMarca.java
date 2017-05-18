package com.portal.control.catalogos;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Caption;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDMarca;
import com.portal.dto.MarcaDTO;
import com.portal.enums.Estados;
import com.portal.utils.Utils;

public class WdwMarca extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwMarca() {
		log = Logger.getLogger(getClass());
	}

	private Listbox ltbMarca;
	private Textbox txtMarca;
	private Caption cptAccion;

	private PDMarca objMarca;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		nombreOperacion = "Marca de Productos";
		cargarMarcas();
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		if (txtMarca == null || txtMarca.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese una descripción para el item.");
			return;
		}

		objMarca = new PDMarca(userLoguiado);

		Connection conn = null;
		try {
			conn = Utils.getConnection();

			if (!objMarca.buscarMarca(conn, txtMarca.getText().trim())) {

				if (operacion.equalsIgnoreCase(OPERACION_EDITAR)) {
					if (objMarca.actualizarMarca(conn,
							Long.parseLong(itemSeleccionado.getCod_marca()),
							txtMarca.getText().trim())) {
						showInformationMessage(nombreOperacion,
								"Item actualizado correctamente.");
						cargarMarcas();
					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible guardar el Item seleccionado.");
					}
					operacion = OPERACION_NUEVO;
					txtMarca.setText(null);
					cptAccion.setLabel("Nuevo Item");
				} else {

					if (objMarca.addMarca(conn, txtMarca.getText().trim())) {
						showInformationMessage(nombreOperacion,
								"Item almacenado correctamente.");
						txtMarca.setText(null);
						cargarMarcas();
					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible guardar el Item ingresado.");
					}
				}
			} else {
				showErrorMessage(nombreOperacion,
						"El valor " + txtMarca.getText()
								+ " ya se encuentra ingresado.");
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
			ltbMarca.setModel(new ListModelList<MarcaDTO>(objMarca
					.getAllMarcas(conn)));
		} finally {
			Utils.closeConnection(conn);
		}

	}

	private String operacion = "";
	MarcaDTO itemSeleccionado = null;

	public void onUpdateItem(final Event event) {
		itemSeleccionado = (MarcaDTO) event.getData();
		txtMarca.setText(itemSeleccionado.getMarca());
		operacion = OPERACION_EDITAR;
		cptAccion.setLabel("Editando Item con código: "
				+ itemSeleccionado.getCod_marca());
	}

	public void onHabilitarItem(final Event event) throws NamingException,
			SQLException {
		Connection portal = null;
		objMarca = new PDMarca(userLoguiado);
		final MarcaDTO itemSeleccionado = (MarcaDTO) event.getData();

		String mensaje = "";
		int estado = 0;

		log.debug("estado de seleccion: " + itemSeleccionado.getEstado());

		if (itemSeleccionado.getEstado().equalsIgnoreCase(Estados.ACTIVO.name())) {
			mensaje = "Item desactivado correctamente.";
			estado = Estados.INACTIVO.CODIGO;
		} else {
			mensaje = "Item activado correctamente.";
			estado = Estados.ACTIVO.CODIGO;
		}

		try {
			portal = Utils.getConnection();
			if (objMarca.cambiarEstado(portal,
					Long.parseLong(itemSeleccionado.getCod_marca()), estado)) {
				showInformationMessage(nombreOperacion, mensaje);
				cargarMarcas();
			} else {
				showErrorMessage(nombreOperacion,
						"No fue posible realizar la operación, intente de nuevo.");
			}

		} finally {
			Utils.closeConnection(portal);
		}

	}
}
