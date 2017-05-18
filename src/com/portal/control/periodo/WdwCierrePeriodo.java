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
import com.portal.bussines.PDDocumentoEnt;
import com.portal.bussines.PDDocumentoSal;
import com.portal.bussines.PDInventarioPeriodo;
import com.portal.bussines.PDPeriodo;
import com.portal.bussines.PDProducto;
import com.portal.dto.ProductoDTO;
import com.portal.exception.PDException;
import com.portal.security.SecEmpresa;
import com.portal.security.SecUsuario;
import com.portal.utils.Utils;

public class WdwCierrePeriodo extends ComposerBase {
	private static final long serialVersionUID = 1L;
	public static final String TEXTO_SIN_PERIODO_ABIERTO = "No tiene periodo abierto.";

	private Combobox cmbEmpresa;
	private Textbox txtPeriodo;

	private PDPeriodo objPeriodo;
	private PDProducto objProducto;
	private PDInventarioPeriodo objInventarioPeriodo;
	private PDDocumentoEnt objDocumentoEnt;
	private PDDocumentoSal objDocumentoSal;
	private SecUsuario user;

	public WdwCierrePeriodo() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Cierre Periodo";
	}

	public void doAfterCompose(Component cmp) throws Exception {
		super.doAfterCompose(cmp);
		user = (SecUsuario) session.getAttribute(ATT_KEY_USER);
		cargarCatalogos();
	}

	public void onClick$btnCerrar() throws Exception {
		Connection conn = null;
		SecEmpresa empresa = null;

		if (cmbEmpresa.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione una empresa para efectuar el cierre.");
			return;
		} else {
			empresa = cmbEmpresa.getSelectedItem().getValue();
		}

		if (txtPeriodo.getText() == null
				|| txtPeriodo.getText().trim().equalsIgnoreCase("")
				|| txtPeriodo.getText().trim()
						.equalsIgnoreCase(TEXTO_SIN_PERIODO_ABIERTO)) {
			showErrorMessage(nombreOperacion,
					"La empresa seleccionada no posee periodo abierto.");
			return;
		}

		try {
			conn = Utils.getConnection();
			conn.setAutoCommit(false);

			if (cierre(conn, Long.parseLong(empresa.getCod_empresa()),
					txtPeriodo.getText())) {
				conn.commit();
				showInformationMessage(nombreOperacion,
						"Cierre realizado exitosamente.");
			} else {
				conn.rollback();
				showErrorMessage(nombreOperacion,
						"No fue posible realizar el cierre, intente de nuevo.");
			}

		} catch (PDException e) {
			conn.rollback();
			showErrorMessage(nombreOperacion, e.getMessage());
		} finally {
			Utils.closeConnection(conn);
		}
	}

	public void onChange$cmbEmpresa() {
		cargarPeriodo();
	}

	private void cargarPeriodo() {

		if (cmbEmpresa.getSelectedItem() != null) {
			SecEmpresa emp = cmbEmpresa.getSelectedItem().getValue();
			if (emp.getPeriodo() != null) {
				txtPeriodo.setText(emp.getPeriodo().getCod_periodo());
			} else {
				txtPeriodo.setText(TEXTO_SIN_PERIODO_ABIERTO);
			}
		}

	}

	private void cargarCatalogos() throws NamingException, SQLException {
		objPeriodo = new PDPeriodo(userLoguiado);

		for (SecEmpresa emp : user.getEmpresas()) {
			Comboitem item = new Comboitem();
			item.setValue(emp);
			item.setLabel(emp.getEmp_nombre());
			item.setParent(cmbEmpresa);
		}

	}

	private boolean cierre(Connection conn, Long codEmpresa, String codPeriodo)
			throws SQLException, PDException {
		objProducto = new PDProducto(userLoguiado);
		objInventarioPeriodo = new PDInventarioPeriodo(userLoguiado);
		objPeriodo = new PDPeriodo(userLoguiado);
		objDocumentoEnt = new PDDocumentoEnt(userLoguiado);
		objDocumentoSal = new PDDocumentoSal(userLoguiado);

		String estadoPeriodo = objPeriodo.getEstadoPeriodo(conn, codEmpresa,
				codPeriodo);

		if (estadoPeriodo == null
				|| !estadoPeriodo.equalsIgnoreCase(PDPeriodo.ESTADO_ABIERTO)) {
			lanzarError("El periodo ya se encuentra cerrado.");
		}

		if (!objPeriodo.cerrarPeriodo(conn, codEmpresa, codPeriodo)) {
			lanzarError("No fue posible cerrar el periodo.");
		}

		for (ProductoDTO prod : objProducto.getAllProductos(conn)) {
			Long codProducto = Long.parseLong(prod.getCod_producto());

			int cantidadIngresada = objDocumentoEnt.getCantidadPeriodo(conn,
					codEmpresa, codProducto, codPeriodo);

			int cantidadEgresada = objDocumentoSal.getCantidadPeriodo(conn,
					codEmpresa, codProducto, codPeriodo);

			int cantidadTotal = cantidadIngresada - cantidadEgresada;

			if (!objInventarioPeriodo.existeProducto(conn, codPeriodo,
					codEmpresa, codProducto)) {
				if (!objInventarioPeriodo.addInventario(conn, codPeriodo,
						codEmpresa, codProducto, 0, cantidadTotal)) {
					lanzarError("No fue posible hacer la inserción del cierre del producto: "
							+ codProducto);
				}

			} else {

				if (!objInventarioPeriodo.actualizarInventario(conn,
						codPeriodo, codEmpresa, codProducto, cantidadTotal)) {

					lanzarError("No fue posible hacer la actualización del cierre del producto: "
							+ codProducto);

				}
			}

		}

		return true;
	}
}
