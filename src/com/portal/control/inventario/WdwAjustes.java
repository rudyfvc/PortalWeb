package com.portal.control.inventario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.base.Pages;
import com.portal.bussines.PDDocumentoEnt;
import com.portal.bussines.PDDocumentoSal;
import com.portal.bussines.PDPeriodo;
import com.portal.dto.AjusteDTO;
import com.portal.dto.DocumentoEntEncabezadoDTO;
import com.portal.dto.DocumentoSalEncabezadoDTO;
import com.portal.dto.PeriodoDTO;
import com.portal.enums.TiposMovimiento;
import com.portal.security.SecEmpresa;
import com.portal.utils.Utils;

public class WdwAjustes extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwAjustes;
	private Window wdwAjuste;
	private Listbox ltbAjustes;
	private Combobox cmbEmpresa, cmbPeriodo;

	private PDDocumentoEnt objDoctoEntrada;
	private PDDocumentoSal objDoctoSalida;
	private PDPeriodo objPeriodo;

	public WdwAjustes() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Ajustes de Inventario";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarEmpresas();
	}

	public void onClick$btnAgregar() {
		args.put(KEY_ARG_OPERACION, OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_AJUSTE,
				wdwAjustes, null);
		wd.addEventListener("onClose", onClose);
		wd.doModal();

	}

	public void onChange$cmbEmpresa() throws NamingException, SQLException {
		ltbAjustes.getItems().clear();
		cargarPeriodos();
	}

	public void onChange$cmbPeriodo() throws NamingException, SQLException {
		cargarAjustes();
	}

	public void cargarAjustes() throws NamingException, SQLException {
		Connection conn = null;
		objDoctoEntrada = new PDDocumentoEnt(userLoguiado);
		objDoctoSalida = new PDDocumentoSal(userLoguiado);

		if (cmbEmpresa.getSelectedIndex() > -1
				&& cmbPeriodo.getSelectedIndex() > -1) {
			SecEmpresa emp = cmbEmpresa.getSelectedItem().getValue();
			PeriodoDTO per = cmbPeriodo.getSelectedItem().getValue();

			try {
				Long codEmpresa = Long.parseLong(emp.getCod_empresa());
				String codPeriodo = per.getCod_periodo();
				Long codTipoMovimiento = (long) TiposMovimiento.AJUSTE.CODIGO;

				conn = Utils.getConnection();

				List<DocumentoEntEncabezadoDTO> ents = objDoctoEntrada
						.getEntradasPorTipo(conn, codEmpresa, codPeriodo,
								codTipoMovimiento);

				List<DocumentoSalEncabezadoDTO> sals = objDoctoSalida
						.getSalidasPorTipo(conn, codEmpresa, codPeriodo,
								codTipoMovimiento);

				List<AjusteDTO> ajustes = new ArrayList<AjusteDTO>();

				for (DocumentoEntEncabezadoDTO ent : ents) {
					AjusteDTO ajuste = new AjusteDTO();
					ajuste.setDoc_tipo(ent.getDoc_tipo());
					ajuste.setDoc_serie(ent.getDoc_serie());
					ajuste.setDoc_numero(ent.getDoc_numero());
					ajuste.setDoc_fecha(ent.getDoc_fecha());
					ajuste.setDoc_estado(ent.getDoc_estado());
					ajuste.setCod_empresa(ent.getCod_empresa());
					ajuste.setCod_movimiento(ent.getCod_movimiento());
					ajuste.setCod_periodo(ent.getCod_periodo());
					ajuste.setCod_tipo_movimiento(ent.getCod_tipo_movimiento());
					ajustes.add(ajuste);
				}

				for (DocumentoSalEncabezadoDTO sal : sals) {
					AjusteDTO ajuste = new AjusteDTO();
					ajuste.setDoc_tipo(sal.getDoc_tipo());
					ajuste.setDoc_serie(sal.getDoc_serie());
					ajuste.setDoc_numero(sal.getDoc_numero());
					ajuste.setDoc_fecha(sal.getDoc_fecha());
					ajuste.setDoc_estado(sal.getDoc_estado());
					ajuste.setCod_empresa(sal.getCod_empresa());
					ajuste.setCod_movimiento(sal.getCod_movimiento());
					ajuste.setCod_periodo(sal.getCod_periodo());
					ajuste.setCod_tipo_movimiento(sal.getCod_tipo_movimiento());
					ajustes.add(ajuste);
				}

				ltbAjustes.setModel(new ListModelList<AjusteDTO>(ajustes));

				conn = Utils.getConnection();

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
					cargarAjustes();
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
				Pages.URL_CONSULTA_VENTA, wdwAjuste, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();

	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarAjustes();
		}
	};
}
