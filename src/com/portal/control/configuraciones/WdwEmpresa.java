package com.portal.control.configuraciones;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDEmpresa;
import com.portal.bussines.PDPeriodo;
import com.portal.dto.EmpresaDTO;
import com.portal.utils.Utils;

public class WdwEmpresa extends ComposerBase {

	private static final long serialVersionUID = 1L;

	private static final Integer[] meses = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
			12 };
	private static final Integer[] anios = { 2017, 2018, 2019, 2020, 2021,
			2022, 2023, 2024, 2025, 2026, 2027, 2028, 2029, 2030, 2031, 2032,
			2033, 2034, 2035, 2036, 2037, 2038, 2039, 2040, 2041, 2042, 2043,
			2044, 2045, 2046, 2047, 2048, 2049, 2050 };

	private Textbox txtNombreEmpresa, txtNit, txtDireccion;
	private Combobox cmbMes, cmbAnio;
	private Intbox txtTelefono;
	private Window wdwEmpresa;
	private Label lblAnio, lblMes;

	private PDEmpresa objEmpresa;
	private PDPeriodo objPeriodo;

	public WdwEmpresa() {
		log = Logger.getLogger(getClass());
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		super.nombreOperacion = "Empresa";
		cargarInfoUsuario();
		cargarCatalogos();
	}

	private void cargarCatalogos() {
		cmbMes.setModel(new ListModelList<Integer>(meses));
		cmbAnio.setModel(new ListModelList<Integer>(anios));
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		Connection conn = null;

		if (txtNombreEmpresa.getText() == null
				|| txtNombreEmpresa.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Nombre de empresa es requerido.");
			return;
		}

		if (txtNit.getText() == null
				|| txtNit.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "NIT de empresa es requerido.");
			return;
		}

		if (txtDireccion.getText() == null
				|| txtDireccion.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Dirección de empresa es requerido.");
			return;
		}

		if (txtTelefono.getText() == null
				|| txtTelefono.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Teléfono de empresa es requerido.");
			return;
		}

		String codPeriodo = "";
		if (arg.get(KEY_ARG_OPERACION).toString()
				.equalsIgnoreCase(OPERACION_NUEVO)) {
			if (cmbMes.getSelectedIndex() < 0) {
				showErrorMessage(nombreOperacion,
						"Seleccione el mes inicial para la empresa.");
				return;
			}

			if (cmbAnio.getSelectedIndex() < 0) {
				showErrorMessage(nombreOperacion,
						"Seleccione el año para la empresa.");
				return;
			}

			codPeriodo = cmbMes.getSelectedItem().getLabel() + "-"
					+ cmbAnio.getSelectedItem().getLabel();

			if (codPeriodo.length() < 7) {
				codPeriodo = "0" + codPeriodo;
			}

		}

		boolean commit = false;

		try {
			conn = Utils.getConnection();
			conn.setAutoCommit(false);

			objEmpresa = new PDEmpresa();
			objPeriodo = new PDPeriodo(userLoguiado);

			if (arg.get(KEY_ARG_OPERACION).toString()
					.equalsIgnoreCase(OPERACION_NUEVO)) {

				Long codEmpresa = objEmpresa.getSecuencia(conn);

				if (objEmpresa.addEmpresa(conn, codEmpresa, txtNombreEmpresa
						.getText().trim(), txtNit.getText().trim(), txtTelefono
						.getText().trim(), txtDireccion.getText().trim(),
						usuario.getUser_name())) {

					if (objPeriodo.aperturarPeriodo(conn, codEmpresa,
							codPeriodo)) {
						commit = true;
						showInformationMessage(nombreOperacion,
								"Empresa agregada exitosamente.");
						Events.echoEvent("onClose", wdwEmpresa, null);

					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible agregar el periodo a la empresa.");
					}

				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible agregar la empresa, intente de nuevo.");
				}

			} else {
				EmpresaDTO empresa = (EmpresaDTO) arg.get(KEY_ARG_DTO);

				if (objEmpresa.updateEmpresa(conn, txtNombreEmpresa.getText()
						.trim(), txtNit.getText().trim(), txtTelefono.getText()
						.trim(), txtDireccion.getText().trim(), usuario
						.getUser_name(), empresa.getCod_empresa())) {
					commit = true;
					showInformationMessage(nombreOperacion,
							"Empresa guardada exitosamente.");
					Events.echoEvent("onClose", wdwEmpresa, null);
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible editar la empresa, intente de nuevo.");
				}
			}

		} finally {
			if (commit) {
				conn.commit();
			} else {
				conn.rollback();
			}

			Utils.closeConnection(conn);
		}

	}

	private void cargarInfoUsuario() {
		if (arg.get(KEY_ARG_OPERACION) != null
				&& arg.get(KEY_ARG_OPERACION).toString()
						.equals(OPERACION_EDITAR)) {
			EmpresaDTO empresa = (EmpresaDTO) arg.get(KEY_ARG_DTO);

			txtNombreEmpresa.setText(empresa.getEmp_nombre());
			txtNit.setText(empresa.getEmp_nit());
			txtDireccion.setText(empresa.getEmp_direccion());
			txtTelefono.setText(empresa.getEmp_telefono());
			cmbMes.setVisible(false);
			cmbAnio.setVisible(false);
			lblMes.setVisible(false);
			lblAnio.setVisible(false);
		}
	}
}
