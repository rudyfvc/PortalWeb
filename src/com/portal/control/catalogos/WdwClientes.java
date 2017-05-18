package com.portal.control.catalogos;

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
import com.portal.bussines.PDCliente;
import com.portal.dto.ClienteDTO;
import com.portal.utils.Utils;

public class WdwClientes extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwClientes() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Clientes";
	}

	private Window wdwCliente;
	private Listbox ltbClientes;

	private PDCliente objCliente;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarClientes();
	}

	public void onClick$btnAgregar() {
		args.put(KEY_ARG_OPERACION, OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_CLIENTE,
				wdwCliente, null);
		wd.addEventListener("onClose", onClose);
		wd.doModal();

	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarClientes();
		}
	};

	public void onUpdateItem(final Event event) {
		objCliente = new PDCliente(userLoguiado);
		ClienteDTO seleccion = (ClienteDTO) event.getData();

		args.put(KEY_ARG_DTO, seleccion);
		args.put(KEY_ARG_OPERACION, OPERACION_EDITAR);

		Window wd = (Window) execution.createComponents(Pages.URL_CLIENTE,
				wdwCliente, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

	private void cargarClientes() throws NamingException, SQLException {
		objCliente = new PDCliente(userLoguiado);
		Connection conn = null;
		try {
			conn = Utils.getConnection();
			ltbClientes.setModel(new ListModelList<ClienteDTO>(objCliente
					.getAllClientes(conn)));
		} finally {
			Utils.closeConnection(conn);
		}

	}

}
