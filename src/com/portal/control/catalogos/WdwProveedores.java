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
import com.portal.bussines.PDProveedor;
import com.portal.dto.ProveedorDTO;
import com.portal.utils.Utils;

public class WdwProveedores extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwProveedores() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Proveedores";
	}

	private Window wdwProveedor;
	private Listbox ltbProveedores;

	private PDProveedor objProveedor;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarProveedores();
	}

	public void onClick$btnAgregar() {
		args.put(KEY_ARG_OPERACION, OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_PROVEEDOR,
				wdwProveedor, null);
		wd.addEventListener("onClose", onClose);
		wd.doModal();

	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarProveedores();
		}
	};

	public void onUpdateItem(final Event event) {
		objProveedor = new PDProveedor(userLoguiado);
		ProveedorDTO seleccion = (ProveedorDTO) event.getData();

		args.put(KEY_ARG_DTO, seleccion);
		args.put(KEY_ARG_OPERACION, OPERACION_EDITAR);

		Window wd = (Window) execution.createComponents(Pages.URL_PROVEEDOR,
				wdwProveedor, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

	private void cargarProveedores() throws NamingException, SQLException {
		objProveedor = new PDProveedor(userLoguiado);
		Connection conn = null;
		try {
			conn = Utils.getConnection();
			ltbProveedores.setModel(new ListModelList<ProveedorDTO>(
					objProveedor.getAllProveedores(conn)));
		} finally {
			Utils.closeConnection(conn);
		}

	}

}
