package com.portal.control.configuraciones;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.base.Pages;
import com.portal.bussines.PDEmpresa;
import com.portal.dto.EmpresaDTO;
import com.portal.utils.Utils;

public class WdwEmpresas extends ComposerBase {

	private static final long serialVersionUID = 1L;
	private PDEmpresa objEmpresa;
	private EmpresaDTO itemSeleccionado;

	private Listbox ltbEmpresas;
	private Window wdwEmpresa;

	public WdwEmpresas() {
		log = Logger.getLogger(getClass());
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		cargarEmpresas();
	}

	private void cargarEmpresas() throws SQLException, NamingException {
		Connection conn = null;

		objEmpresa = new PDEmpresa();

		try {
			conn = Utils.getConnection();
			ltbEmpresas.setModel(new ListModelList<EmpresaDTO>(objEmpresa
					.getAllEmpresas(conn)));
		} finally {
			Utils.closeConnection(conn);
		}

	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarEmpresas();
		}
	};

	public void onClick$btnNuevaEmpresa() {
		args.put(KEY_ARG_OPERACION, OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_EMPRESA,
				wdwEmpresa, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

	public void onUpdateItem(final Event event) {
		objEmpresa = new PDEmpresa();
		itemSeleccionado = (EmpresaDTO) event.getData();

		args.put(KEY_ARG_DTO, itemSeleccionado);
		args.put(KEY_ARG_OPERACION, OPERACION_EDITAR);

		Window wd = (Window) execution.createComponents(Pages.URL_EMPRESA,
				wdwEmpresa, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

}
