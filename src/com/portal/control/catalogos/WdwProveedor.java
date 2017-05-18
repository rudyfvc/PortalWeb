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
import com.portal.bussines.PDProveedor;
import com.portal.bussines.PDTipoEntidad;
import com.portal.dto.ProveedorDTO;
import com.portal.dto.TipoEntidadDTO;
import com.portal.enums.Estados;
import com.portal.utils.Utils;

public class WdwProveedor extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwProveedor() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Proveedore";
	}

	private Window wdwProveedor;
	private Combobox cmbTipoProveedor;
	private Textbox txtCodigo, txtNombres, txtApellidos, txtNit, txtDpi,
			txtPasaporte, txtPatenteComercio, txtRegistroFiscal,
			txtCorreoElectronico, txtDireccion;
	private Intbox txtTelefonoFijo, txtTelefonoCelular;

	private PDProveedor objProveedor;
	private PDTipoEntidad objTipoEntidad;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarTipoProveedores();
		cargarInfoProveedor();
	}

	private void cargarTipoProveedores() throws NamingException, SQLException {
		objTipoEntidad = new PDTipoEntidad(userLoguiado);
		Connection conn = null;
		try {
			conn = Utils.getConnection();

			for (TipoEntidadDTO each : objTipoEntidad
					.getTipoEntidadesPorEstado(conn, Estados.ACTIVO.CODIGO)) {
				Comboitem item = new Comboitem();
				item.setValue(each);
				item.setLabel(each.getTipo_entidad());
				item.setParent(cmbTipoProveedor);
			}

		} finally {
			Utils.closeConnection(conn);
		}
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		objProveedor = new PDProveedor(userLoguiado);
		Connection conn = null;
		if (cmbTipoProveedor.getSelectedIndex() < 0) {
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

		if (txtDireccion.getText() == null
				|| txtDireccion.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese dirección de el proveedor.");
			return;
		}

		ProveedorDTO prov = new ProveedorDTO();
		TipoEntidadDTO tipo = cmbTipoProveedor.getSelectedItem().getValue();

		prov.setCod_tipo_entidad(tipo.getCod_tipo_entidad());
		prov.setNombres(txtNombres.getText());
		prov.setApellidos(txtApellidos.getText());
		prov.setNit(txtNit.getText());
		prov.setDpi(txtDpi.getValue());
		prov.setEmail(txtCorreoElectronico.getText());
		prov.setDireccion(txtDireccion.getText());
		prov.setTelefono_celular(txtTelefonoCelular.getText());
		prov.setTelefono_fijo(txtTelefonoFijo.getText());
		prov.setPasaporte(txtPasaporte.getText());
		prov.setPatente(txtPatenteComercio.getText());
		prov.setRegistro_fiscal(txtRegistroFiscal.getText());
		prov.setEstado("1");

		try {
			conn = Utils.getConnection();
			if (getOperacion().equalsIgnoreCase(OPERACION_EDITAR)) {
				prov.setCod_proveedor(seleccion.getCod_proveedor());
				if (objProveedor.actualizarProveedor(conn, prov)) {
					showInformationMessage(nombreOperacion,
							"Operación realizada exitosamente.");
					Events.echoEvent("onClose", wdwProveedor, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible realizar la operación.");
				}
			} else {
				if (objProveedor.addProveedor(conn, prov)) {
					showInformationMessage(nombreOperacion,
							"Operación realizada exitosamente.");
					Events.echoEvent("onClose", wdwProveedor, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible realizar la operación.");
				}
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private ProveedorDTO seleccion = null;

	private void cargarInfoProveedor() {
		if (getOperacion().equalsIgnoreCase(OPERACION_EDITAR)) {
			wdwProveedor.setTitle("Edición de Proveedor");
			seleccion = (ProveedorDTO) getValorOperacion();
			txtCodigo.setText(seleccion.getCod_proveedor());
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

			for (Comboitem item : cmbTipoProveedor.getItems()) {
				TipoEntidadDTO tipo = item.getValue();
				if (tipo.getCod_tipo_entidad().equalsIgnoreCase(
						seleccion.getCod_tipo_entidad())) {
					cmbTipoProveedor.setSelectedItem(item);
					break;
				}
			}

		} else {
			wdwProveedor.setTitle("Nuevo Proveedor");
			txtCodigo.setValue("AUTOGENERADO");
		}
	}
}
