package com.portal.control.inventario;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.base.Pages;
import com.portal.bussines.PDProducto;
import com.portal.dto.ProductoDTO;
import com.portal.enums.Estados;
import com.portal.utils.Utils;

public class WdwProductos extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwProducto;
	private Listbox ltbProductos;
	private Intbox txtCodigoProducto;
	private Textbox txtNombreProducto;

	private PDProducto objProducto;

	public WdwProductos() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Producto";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarProductos();
	}

	public void onClick$btnFiltrar() throws SQLException, NamingException {
		objProducto = new PDProducto(userLoguiado);
		Connection conn = null;
		try {
			conn = Utils.getConnection();

			if (txtCodigoProducto.getText() != null
					&& !txtCodigoProducto.getText().trim().equalsIgnoreCase("")) {

				ltbProductos.setModel(new ListModelList<ProductoDTO>(
						objProducto.filtrarProductos(conn, Long
								.parseLong(txtCodigoProducto.getText().trim()),
								null, PDProducto.FILTRO_POR_CODIGO)));
				return;
			}

			if (txtNombreProducto.getText() != null
					&& !txtNombreProducto.getText().trim().equalsIgnoreCase("")) {
				ltbProductos.setModel(new ListModelList<ProductoDTO>(
						objProducto.filtrarProductos(conn, 0, txtNombreProducto
								.getText().trim(),
								PDProducto.FILTRO_POR_DESCRIPCION)));
				return;
			}

			ltbProductos.setModel(new ListModelList<ProductoDTO>(objProducto
					.filtrarProductos(conn, 0, "", "")));

		} finally {
			Utils.closeConnection(conn);
		}

	}

	public void onClick$btnAgregar() {
		args.put(KEY_ARG_OPERACION, OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_PRODUCTO,
				wdwProducto, null);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarProductos();
		}
	};

	private void cargarProductos() throws NamingException, SQLException {
		Connection conn = null;

		try {
			conn = Utils.getConnection();
			objProducto = new PDProducto(userLoguiado);

			ltbProductos.setModel(new ListModelList<ProductoDTO>(objProducto
					.getAllProductos(conn)));

		} finally {
			Utils.closeConnection(conn);
		}
	}

	public void onUpdateItem(final Event event) {
		objProducto = new PDProducto(userLoguiado);
		ProductoDTO itemSeleccionado = (ProductoDTO) event.getData();

		args.put(KEY_ARG_DTO, itemSeleccionado);
		args.put(KEY_ARG_OPERACION, OPERACION_EDITAR);

		Window wd = (Window) execution.createComponents(Pages.URL_PRODUCTO,
				wdwProducto, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

	public void onHabilitarItem(final Event event) throws NamingException,
			SQLException {

		objProducto = new PDProducto(userLoguiado);
		ProductoDTO seleccion = (ProductoDTO) event.getData();

		Connection portal = null;
		try {
			portal = Utils.getConnection();
			long estado = 0;
			String mensaje = "Item activado exitosamente.";

			if (seleccion.getEstado().equalsIgnoreCase(Estados.ACTIVO.name())) {
				estado = Estados.INACTIVO.CODIGO;
				mensaje = "Item desactivado exitosamente.";
			} else {
				estado = Estados.ACTIVO.CODIGO;
				mensaje = "Item activado exitosamente.";
			}

			if (objProducto.cambiarEstado(portal, estado,
					Long.parseLong(seleccion.getCod_producto()))) {
				showInformationMessage(nombreOperacion, mensaje);
				cargarProductos();
			} else {
				showErrorMessage(nombreOperacion,
						"No fue posible realizar la operación.");
			}

		} finally {
			Utils.closeConnection(portal);
		}
	}
}
