package com.portal.control.periodo;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDInventarioPeriodo;
import com.portal.bussines.PDPeriodo;
import com.portal.bussines.PDProducto;
import com.portal.dto.PeriodoDTO;
import com.portal.dto.ProductoDTO;
import com.portal.exception.PDException;
import com.portal.security.SecEmpresa;
import com.portal.security.SecUsuario;
import com.portal.utils.Utils;

public class WdwAperturaPeriodo extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Combobox cmbEmpresa, cmbSiguientePeriodo;
	private Textbox txtUltimoPeriodo, txtEstadoUltimoPeriodo;

	private PDPeriodo objPeriodo;
	private PDProducto objProducto;
	private PDInventarioPeriodo objInventarioPeriodo;
	private SecUsuario user;

	public WdwAperturaPeriodo() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Cierre Periodo";
	}

	public void doAfterCompose(Component cmp) throws Exception {
		super.doAfterCompose(cmp);
		user = (SecUsuario) session.getAttribute(ATT_KEY_USER);
		cargarCatalogos();
	}

	public void onClick$btnAceptar() throws Exception {
		Connection conn = null;
		SecEmpresa empresa = null;
		PeriodoDTO perSeleccionado = null;
		String codPeriodoAnterior = null;

		objPeriodo = new PDPeriodo(userLoguiado);
		objInventarioPeriodo = new PDInventarioPeriodo(userLoguiado);
		objProducto = new PDProducto(userLoguiado);

		if (cmbEmpresa.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione una empresa.");
			return;
		} else {
			empresa = cmbEmpresa.getSelectedItem().getValue();
		}

		if (txtUltimoPeriodo.getText() == null
				|| txtUltimoPeriodo.getText().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"La empresa no tiene un periodo inicial o anterior.");
			return;
		}

		if (txtEstadoUltimoPeriodo.getText() == null
				|| txtEstadoUltimoPeriodo.getText().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"El estado del periodo anterior no es válido.");
			return;
		}

		if (txtEstadoUltimoPeriodo.getText().equalsIgnoreCase(
				PDPeriodo.ESTADO_ABIERTO)) {
			showErrorMessage(
					nombreOperacion,
					"El periodo anterior aún se encuentra abierto, por favor cierre e intente de nuevo");
			return;
		} else {
			codPeriodoAnterior = txtUltimoPeriodo.getText().trim();
		}

		if (cmbSiguientePeriodo.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione el periodo a aperturar.");
			return;
		} else {
			perSeleccionado = cmbSiguientePeriodo.getSelectedItem().getValue();
		}

		try {
			conn = Utils.getConnection();
			conn.setAutoCommit(false);

			String codPeriodo = perSeleccionado.getCod_periodo();
			Long codEmpresa = Long.parseLong(empresa.getCod_empresa());

			if (objPeriodo.aperturarPeriodo(conn, codEmpresa, codPeriodo)) {

				for (ProductoDTO prod : objProducto.getAllProductos(conn)) {
					Long codProducto = Long.parseLong(prod.getCod_producto());

					int invFinalAnterior = objInventarioPeriodo
							.getInventarioFinal(conn, codPeriodoAnterior,
									codEmpresa, codProducto);

					if (!objInventarioPeriodo.addInventario(conn,
							codPeriodo, codEmpresa, codProducto,
							invFinalAnterior, 0)) {
						lanzarError("No fue posible realizar el cierre del producto: "
								+ codProducto);
					}

				}

				conn.commit();
				showInformationMessage(nombreOperacion,
						"Periodo aperturado exitosamente, Inicie sesión de nuevo.");
				return;
			} else {
				showErrorMessage(nombreOperacion,
						"No fue posible aperturar el periodo seleccionado.");
			}

		} catch (PDException e) {
			conn.rollback();
			showErrorMessage(nombreOperacion, e.getMessage());
		} finally {
			Utils.closeConnection(conn);
		}

	}

	public void onChange$cmbEmpresa() throws NumberFormatException,
			SQLException, NamingException {
		cargarPeriodo();
	}

	private void cargarPeriodo() throws NumberFormatException, SQLException,
			NamingException {
		Connection conn = null;
		cmbSiguientePeriodo.getItems().clear();
		
		if (cmbEmpresa.getSelectedItem() != null) {
			SecEmpresa emp = cmbEmpresa.getSelectedItem().getValue();
			try {
				conn = Utils.getConnection();
				conn.setAutoCommit(false);

				objPeriodo = new PDPeriodo(userLoguiado);

				PeriodoDTO ultimo = objPeriodo.getUltimoPeriodo(conn,
						Long.parseLong(emp.getCod_empresa()));

				if (ultimo != null) {
					txtUltimoPeriodo.setText(ultimo.getCod_periodo());
					txtEstadoUltimoPeriodo.setText(ultimo.getEstado());

					for (PeriodoDTO periodo : objPeriodo.getSiguientesPeriodos(
							conn, ultimo.getCod_periodo())) {
						Comboitem item = new Comboitem();
						item.setLabel(periodo.getCod_periodo());
						item.setValue(periodo);
						item.setParent(cmbSiguientePeriodo);
					}

				} else {
					txtUltimoPeriodo.setText(null);
					txtEstadoUltimoPeriodo.setText(null);
					showErrorMessage(nombreOperacion,
							"No existe un periodo anterior.");
				}

			} finally {
				Utils.closeConnection(conn);
			}
		}

	}

	private void cargarCatalogos() throws NamingException, SQLException {
		objPeriodo = new PDPeriodo(userLoguiado);
		cmbEmpresa.getItems().clear();
		for (SecEmpresa emp : user.getEmpresas()) {
			Comboitem item = new Comboitem();
			item.setValue(emp);
			item.setLabel(emp.getEmp_nombre());
			item.setParent(cmbEmpresa);
		}

	}

}
