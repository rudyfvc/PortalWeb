package com.portal.control.egresos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.base.Pages;
import com.portal.bussines.PDDocumentoSal;
import com.portal.bussines.PDPeriodo;
import com.portal.dto.DocumentoSalEncabezadoDTO;
import com.portal.dto.PeriodoDTO;
import com.portal.enums.TiposMovimiento;
import com.portal.security.SecEmpresa;
import com.portal.utils.Utils;

public class WdwVentas extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwVenta;
	private Window wdwConsultaVenta;
	private Listbox ltbSalidas;
	private Combobox cmbEmpresa, cmbPeriodo;

	private PDPeriodo objPeriodo;

	public WdwVentas() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Ventas";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarEmpresas();
	}

	public void onClick$btnAgregar() {
		args.put(KEY_ARG_OPERACION, OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_VENTA,
				wdwVenta, null);
		wd.addEventListener("onClose", onClose);
		wd.doModal();

	}

	public void onChange$cmbEmpresa() throws NamingException, SQLException {
		ltbSalidas.getItems().clear();
		cargarPeriodos();
	}

	public void onChange$cmbPeriodo() throws NamingException, SQLException {
		cargarVentas();
	}

	public void cargarVentas() throws NamingException, SQLException {
		Connection conn = null;

		if (cmbEmpresa.getSelectedIndex() > -1
				&& cmbPeriodo.getSelectedIndex() > -1) {
			SecEmpresa emp = cmbEmpresa.getSelectedItem().getValue();
			PeriodoDTO per = cmbPeriodo.getSelectedItem().getValue();

			PDDocumentoSal objDocumento = new PDDocumentoSal(userLoguiado);

			try {
				Long codEmpresa = Long.parseLong(emp.getCod_empresa());
				String codPeriodo = per.getCod_periodo();
				Long codTipoMovimiento = (long) TiposMovimiento.VENTA.CODIGO;

				conn = Utils.getConnection();
				List<DocumentoSalEncabezadoDTO> entradas = objDocumento
						.getSalidasPorTipo(conn, codEmpresa, codPeriodo,
								codTipoMovimiento);

				if (entradas.size() > 0) {
					ListModelList<DocumentoSalEncabezadoDTO> modelo = new ListModelList<DocumentoSalEncabezadoDTO>(
							entradas);
					ltbSalidas
							.setModel(new ListModelList<DocumentoSalEncabezadoDTO>(
									modelo));
				} else {
					ltbSalidas.getItems().clear();
					Clients.showNotification(
							"No existen ventas con los parametros seleccionados.",
							cmbPeriodo);
				}

			} finally {
				Utils.closeConnection(conn);
			}
		}

	}

	private void cargarEmpresas() {
		if (usuario.getEmpresas() != null) {
			for (SecEmpresa emp : usuario.getEmpresas()) {
				Comboitem item = new Comboitem();
				item.setLabel(emp.getEmp_nombre());
				item.setValue(emp);
				item.setParent(cmbEmpresa);
			}
		}
	}

	private void cargarPeriodos() throws NamingException, SQLException {
		Connection conn = null;
		cmbPeriodo.getItems().clear();
		cmbPeriodo.setValue(DEFAULT_LABEL_CMB);

		if (cmbEmpresa.getSelectedIndex() > -1) {
			SecEmpresa emp = cmbEmpresa.getSelectedItem().getValue();
			objPeriodo = new PDPeriodo(userLoguiado);
			Long codEmpresa = Long.parseLong(emp.getCod_empresa());

			try {
				conn = Utils.getConnection();
				List<PeriodoDTO> periodos = objPeriodo.getPeriodosPorEmpresa(
						conn, codEmpresa);

				if (periodos != null) {
					for (PeriodoDTO periodo : periodos) {
						Comboitem item = new Comboitem();
						item.setValue(periodo);
						item.setLabel(periodo.getCod_periodo());
						item.setParent(cmbPeriodo);
					}

					cmbPeriodo.setSelectedIndex(0);
					cargarVentas();
				}

			} finally {
				Utils.closeConnection(conn);
			}
		}

	}

	public void onUpdateItem(final Event event) {
		DocumentoSalEncabezadoDTO seleccion = (DocumentoSalEncabezadoDTO) event
				.getData();

		args.put(KEY_ARG_DTO, seleccion);

		Window wd = (Window) execution.createComponents(
				Pages.URL_CONSULTA_VENTA, wdwConsultaVenta, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();

	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarVentas();
		}
	};
}
