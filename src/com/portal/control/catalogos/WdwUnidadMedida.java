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
import com.portal.bussines.PDUnidadMedida;
import com.portal.dto.UnidadMedidaDTO;
import com.portal.enums.Estados;
import com.portal.utils.Utils;

public class WdwUnidadMedida extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwUnidadMedida() {
		log = Logger.getLogger(getClass());
	}

	private Listbox ltbUnidadMedida;
	private Textbox txtUnidadMedida;
	private Caption cptAccion;

	private PDUnidadMedida objUnidadMedida;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		nombreOperacion = "Unidad de Medida";
		cargarUnidadesMedida();
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		if (txtUnidadMedida == null
				|| txtUnidadMedida.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese una descripción para el item.");
			return;
		}

		objUnidadMedida = new PDUnidadMedida(userLoguiado);

		Connection conn = null;
		try {
			conn = Utils.getConnection();

			if (!objUnidadMedida.buscarUnidadMedida(conn, txtUnidadMedida
					.getText().trim())) {

				if (operacion.equalsIgnoreCase(OPERACION_EDITAR)) {
					if (objUnidadMedida.actualizarUnidadMedida(conn,
							Long.parseLong(itemSeleccionado
									.getCod_unidad_medida()), txtUnidadMedida
									.getText().trim())) {
						showInformationMessage(nombreOperacion,
								"Item actualizado correctamente.");
						cargarUnidadesMedida();
					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible guardar el Item seleccionado.");
					}
					operacion = OPERACION_NUEVO;
					txtUnidadMedida.setText(null);
					cptAccion.setLabel("Nuevo Item");
				} else {

					if (objUnidadMedida.addUnidadMedida(conn, txtUnidadMedida
							.getText().trim())) {
						showInformationMessage(nombreOperacion,
								"Item almacenado correctamente.");
						txtUnidadMedida.setText(null);
						cargarUnidadesMedida();
					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible guardar el Item ingresado.");
					}
				}
			} else {
				showErrorMessage(nombreOperacion,
						"El valor " + txtUnidadMedida.getText()
								+ " ya se encuentra ingresado.");
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
			ltbUnidadMedida.setModel(new ListModelList<UnidadMedidaDTO>(
					objUnidadMedida.getUnidadesMedida(conn)));
		} finally {
			Utils.closeConnection(conn);
		}

	}

	private String operacion = "";
	UnidadMedidaDTO itemSeleccionado = null;

	public void onUpdateItem(final Event event) {
		itemSeleccionado = (UnidadMedidaDTO) event.getData();
		txtUnidadMedida.setText(itemSeleccionado.getUnidad_medida());
		operacion = OPERACION_EDITAR;
		cptAccion.setLabel("Editando Item con código: "
				+ itemSeleccionado.getCod_unidad_medida());
	}

	public void onHabilitarItem(final Event event) throws NamingException,
			SQLException {
		Connection portal = null;
		objUnidadMedida = new PDUnidadMedida(userLoguiado);
		final UnidadMedidaDTO itemSeleccionado = (UnidadMedidaDTO) event
				.getData();

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
			if (objUnidadMedida.cambiarEstado(portal,
					Long.parseLong(itemSeleccionado.getCod_unidad_medida()),
					estado)) {
				showInformationMessage(nombreOperacion, mensaje);
				cargarUnidadesMedida();
			} else {
				showErrorMessage(nombreOperacion,
						"No fue posible realizar la operación, intente de nuevo.");
			}

		} finally {
			Utils.closeConnection(portal);
		}

	}
}
