package com.portal.control.catalogos;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDCliente;
import com.portal.bussines.PDTipoEntidad;
import com.portal.dto.ClienteDTO;
import com.portal.dto.TipoEntidadDTO;
import com.portal.enums.Estados;
import com.portal.utils.Utils;

public class WdwCliente extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwCliente() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Cliente";
	}

	private Window wdwCliente;
	private Combobox cmbTipoCliente;
	private Textbox txtCodigo, txtNombres, txtApellidos, txtNit, txtDpi,
			txtPasaporte, txtPatenteComercio, txtRegistroFiscal,
			txtCorreoElectronico, txtDireccion;
	private Intbox txtTelefonoFijo, txtTelefonoCelular;

	private PDCliente objCliente;
	private PDTipoEntidad objTipoEntidad;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarTipoClientes();
		cargarInfoCliente();
	}

	private void cargarTipoClientes() throws NamingException, SQLException {
		objTipoEntidad = new PDTipoEntidad(userLoguiado);
		Connection conn = null;
		try {
			conn = Utils.getConnection();

			for (TipoEntidadDTO each : objTipoEntidad
					.getTipoEntidadesPorEstado(conn, Estados.ACTIVO.CODIGO)) {
				Comboitem item = new Comboitem();
				item.setValue(each);
				item.setLabel(each.getTipo_entidad());
				item.setParent(cmbTipoCliente);
			}

		} finally {
			Utils.closeConnection(conn);
		}
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		objCliente = new PDCliente(userLoguiado);
		Connection conn = null;
		if (cmbTipoCliente.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione un tipo de proveedor.");
			return;
		}

		if (txtNombres.getText() == null
				|| txtNombres.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese un nombre para el proveedor.");
			return;
		}

		if (txtNit.getText() == null
				|| txtNit.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese nit para el proveedor.");
			return;
		}

		if (txtDpi.getText() != null && txtDpi.getText().trim().length() > 0) {
			if (!Utils.ValidaCUI(txtDpi.getText().trim())) {
				showErrorMessage(nombreOperacion,
						"El DPI ingresado no es válido");
				return;
			}
		}

		if (txtCorreoElectronico.getText() != null
				&& txtCorreoElectronico.getText().trim().length() > 0) {
			if (!Utils.isEmail(txtCorreoElectronico.getText().trim())) {
				showErrorMessage(nombreOperacion,
						"El correo electrónico ingresado es inválido.");
				return;
			}
		}

		if (txtDireccion.getText() == null
				|| txtDireccion.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese dirección de el proveedor.");
			return;
		}

		ClienteDTO cliente = new ClienteDTO();
		TipoEntidadDTO tipo = cmbTipoCliente.getSelectedItem().getValue();

		cliente.setCod_tipo_entidad(tipo.getCod_tipo_entidad());
		cliente.setNombres(txtNombres.getText());
		cliente.setApellidos(txtApellidos.getText());
		cliente.setNit(txtNit.getText());
		cliente.setDpi(txtDpi.getValue());
		cliente.setEmail(txtCorreoElectronico.getText());
		cliente.setDireccion(txtDireccion.getText());
		cliente.setTelefono_celular(txtTelefonoCelular.getText());
		cliente.setTelefono_fijo(txtTelefonoFijo.getText());
		cliente.setPasaporte(txtPasaporte.getText());
		cliente.setPatente(txtPatenteComercio.getText());
		cliente.setRegistro_fiscal(txtRegistroFiscal.getText());
		cliente.setEstado("1");

		try {
			conn = Utils.getConnection();

			ClienteDTO clienteNIT = objCliente.getClientePorNit(conn,
					cliente.getNit());

			if (clienteNIT != null) {
				if (getOperacion().equalsIgnoreCase(OPERACION_EDITAR)) {
					if (!seleccion.getCod_cliente().equalsIgnoreCase(
							clienteNIT.getCod_cliente())) {
						showErrorMessage(nombreOperacion,
								"Ya existe un cliente con el mismo NIT.");
						return;
					}
				} else {
					showErrorMessage(nombreOperacion,
							"Ya existe un cliente con el mismo NIT.");
					return;
				}
			}

			if (getOperacion().equalsIgnoreCase(OPERACION_EDITAR)) {
				cliente.setCod_cliente(seleccion.getCod_cliente());
				if (objCliente.actualizarCliente(conn, cliente)) {
					showInformationMessage(nombreOperacion,
							"Operación realizada exitosamente.");
					Events.echoEvent("onClose", wdwCliente, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible realizar la operación.");
				}
			} else {
				if (objCliente.addCliente(conn, cliente)) {
					showInformationMessage(nombreOperacion,
							"Operación realizada exitosamente.");
					Events.echoEvent("onClose", wdwCliente, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible realizar la operación.");
				}
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private ClienteDTO seleccion = null;

	private void cargarInfoCliente() {

		if (getOperacion().equalsIgnoreCase(OPERACION_EDITAR)) {
			wdwCliente.setTitle("Edición de Cliente");
			seleccion = (ClienteDTO) getValorOperacion();
			txtCodigo.setText(seleccion.getCod_cliente());
			txtNombres.setText(seleccion.getNombres());
			txtApellidos.setText(seleccion.getApellidos());
			txtCorreoElectronico.setText(seleccion.getEmail());
			txtDireccion.setText(seleccion.getDireccion());
			txtDpi.setText(seleccion.getDpi());
			txtNit.setText(seleccion.getNit());
			txtPasaporte.setText(seleccion.getPasaporte());
			txtPatenteComercio.setText(seleccion.getPatente());
			txtRegistroFiscal.setText(seleccion.getRegistro_fiscal());
			txtTelefonoCelular.setText(seleccion.getTelefono_celular());
			txtTelefonoFijo.setText(seleccion.getTelefono_fijo());

			for (Comboitem item : cmbTipoCliente.getItems()) {
				TipoEntidadDTO tipo = item.getValue();
				if (tipo.getCod_tipo_entidad().equalsIgnoreCase(
						seleccion.getCod_tipo_entidad())) {
					cmbTipoCliente.setSelectedItem(item);
					break;
				}
			}

		} else {
			wdwCliente.setTitle("Nuevo Cliente");
			txtCodigo.setValue("AUTOGENERADO");
		}
	}
}
