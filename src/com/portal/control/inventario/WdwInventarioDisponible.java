package com.portal.control.inventario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.base.Pages;
import com.portal.bussines.PDInventario;
import com.portal.dto.InventarioDisponibleDTO;
import com.portal.security.SecEmpresa;
import com.portal.utils.Utils;

public class WdwInventarioDisponible extends ComposerBase {
	private static final long serialVersionUID = 1L;
	

	private Window wdwKardex;
	private Combobox cmbEmpresa;
	private Listbox ltbInventario;
	private Textbox txtPeriodo;

	private PDInventario objInventario;

	public WdwInventarioDisponible() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Inventario Disponible";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarEmpresas();
	}

	public void onChange$cmbEmpresa() throws NamingException, SQLException {
		cargarInventario();
	}

	public void onDoubleClick$ltbInventario() {
		if (ltbInventario.getSelectedIndex() > -1) {
			InventarioDisponibleDTO itemSeleccionado = ltbInventario
					.getSelectedItem().getValue();
			
			SecEmpresa empresa = cmbEmpresa.getSelectedItem().getValue();

			args.put(KEY_ARG_DTO, itemSeleccionado);
			
			args.put("EMPRESA", empresa.getEmp_nombre());
			args.put("COD_EMPRESA", empresa.getCod_empresa());
			args.put("COD_PRODUCTO", itemSeleccionado.getCod_producto());
			args.put("COD_PERIODO", txtPeriodo.getText());

			Window wd = (Window) execution.createComponents(Pages.URL_KARDEX,
					wdwKardex, args);
			wd.addEventListener("onClose", onClose);
			wd.doModal();
		}

	}

	private void cargarEmpresas() {

		for (SecEmpresa emp : usuario.getEmpresas()) {
			Comboitem item = new Comboitem();
			item.setLabel(emp.getEmp_nombre());
			item.setValue(emp);
			item.setParent(cmbEmpresa);
		}

	}

	private void cargarInventario() throws NamingException, SQLException {
		Connection conn = null;
		ltbInventario.getItems().clear();
		objInventario = new PDInventario(userLoguiado);

		if (cmbEmpresa.getSelectedIndex() > -1) {
			SecEmpresa emp = cmbEmpresa.getSelectedItem().getValue();
			Long codEmpresa = Long.parseLong(emp.getCod_empresa());
			String codPeriodo = "";
			if (emp.getPeriodo() != null) {
				codPeriodo = emp.getPeriodo().getCod_periodo();
			}

			if (codPeriodo.equalsIgnoreCase("")) {
				showInformationMessage(nombreOperacion,
						"La empresa no posee un periodo abierto o asignado.");
				txtPeriodo.setText("");
				return;
			}

			txtPeriodo.setText(codPeriodo);

			try {
				conn = Utils.getConnection();

				List<InventarioDisponibleDTO> inv = objInventario
						.getInventarioDisponible(conn, codEmpresa, codPeriodo);

				if (inv != null) {
					ltbInventario
							.setModel(new ListModelList<InventarioDisponibleDTO>(
									inv));
				}
			} finally {
				Utils.closeConnection(conn);
			}

		}

	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {

		}
	};

}
