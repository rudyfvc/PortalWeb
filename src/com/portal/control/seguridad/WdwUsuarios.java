package com.portal.control.seguridad;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.base.Pages;
import com.portal.bussines.PDUsuario;
import com.portal.dto.UsuarioDTO;
import com.portal.utils.SecurityPortal;
import com.portal.utils.Utils;

public class WdwUsuarios extends ComposerBase {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(WdwUsuarios.class);

	/************ WIRE *************/
	private Window wdwUsuario;
	private Listbox ltbUsuarios;
	private Textbox txtUsuario, txtNombres;

	/************ VARIABLES *************/
	private ListModelList<UsuarioDTO> modeloUsuarios = new ListModelList<UsuarioDTO>();
	private PDUsuario objUsuario;
	private List<UsuarioDTO> listaUsuarios;

	private Connection portal = null;
	private UsuarioDTO usuarioSeleccionado;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		log.debug("doAfterCompose() in WdwUsuarios");
		args = new HashMap<String, Object>();
		cargarUsuarios();
	}

	@Command
	public void onClick$btnAgregar() {
		args.put(WdwUsuario.KEY_ARG_OPERACION, WdwUsuario.OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_USUARIO,
				wdwUsuario, null);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarUsuarios();
		}
	};

	public void onClick$btnFiltrar() throws NamingException, SQLException {
		objUsuario = new PDUsuario();
		try {
			portal = Utils.getConnection();
			if (txtUsuario.getText().trim().equalsIgnoreCase("")
					&& txtNombres.getText().trim().equalsIgnoreCase("")) {
				ltbUsuarios.setModel(new ListModelList<UsuarioDTO>(objUsuario
						.getUsuarios(portal)));
			} else {
				ltbUsuarios.setModel(new ListModelList<UsuarioDTO>(objUsuario
						.getUsuariosFiltrados(portal, txtUsuario.getText()
								.trim(), txtNombres.getText().trim())));

			}
		} finally {
			Utils.closeConnection(portal);
		}
	}

	private void cargarUsuarios() throws SQLException, NamingException {
		log.debug("cargando usuarios disponibles...");
		modeloUsuarios.clear();
		objUsuario = new PDUsuario();

		try {
			portal = Utils.getConnection();
			listaUsuarios = objUsuario.getUsuarios(portal);

			for (UsuarioDTO usuario : listaUsuarios) {
				modeloUsuarios.add(usuario);
			}

			ltbUsuarios.setModel(modeloUsuarios);
		} finally {
			Utils.closeConnection(portal);
		}

	}

	public void onHabilitarItem(final Event event) throws NamingException,
			SQLException {

		objUsuario = new PDUsuario();
		usuarioSeleccionado = (UsuarioDTO) event.getData();

		try {
			final Connection portal = Utils.getConnection();

			if (usuarioSeleccionado.getUser_estado().equalsIgnoreCase("ACTIVO")) {
				Messagebox.show("¿Desea Desactivar el usuario?",
						nombreOperacion, Messagebox.YES + Messagebox.NO,
						Messagebox.QUESTION, new EventListener<Event>() {
							public void onEvent(Event event) throws Exception {
								if (Messagebox.ON_YES.equals(event.getName())) {
									if (objUsuario.inhabilitarUsuario(portal,
											usuarioSeleccionado.getUser_name())) {
										showInformationMessage(nombreOperacion,
												"Usuario desactivado exitosamente.");
										cargarUsuarios();
									} else {
										showErrorMessage(nombreOperacion,
												"No fue posible desactivar el usuario, intente de nuevo.");
									}

								}
							}
						});
			} else {
				Messagebox.show("¿Desea activar el usuario?", nombreOperacion,
						Messagebox.YES + Messagebox.NO, Messagebox.QUESTION,
						new EventListener<Event>() {
							public void onEvent(Event event) throws Exception {
								if (Messagebox.ON_YES.equals(event.getName())) {
									if (objUsuario.habilitarUsuario(portal,
											usuarioSeleccionado.getUser_name())) {
										showInformationMessage(nombreOperacion,
												"Usuario activado exitosamente.");
										cargarUsuarios();
									} else {
										showErrorMessage(nombreOperacion,
												"No fue posible activar el usuario, intente de nuevo.");
									}
								}
							}
						});
			}
		} finally {
			Utils.closeConnection(portal);
		}

	}

	public void onResetPasswordItem(final Event event) throws NamingException,
			SQLException {
		objUsuario = new PDUsuario();
		usuarioSeleccionado = (UsuarioDTO) event.getData();

		try {
			final Connection portal = Utils.getConnection();
			final String password = SecurityPortal
					.getHmacSHA1(usuarioSeleccionado.getUser_name());

			Messagebox.show("¿Esta seguro de resetear el password?",
					nombreOperacion, Messagebox.YES + Messagebox.NO,
					Messagebox.QUESTION, new EventListener<Event>() {
						public void onEvent(Event event) throws Exception {
							if (Messagebox.ON_YES.equals(event.getName())) {
								if (objUsuario.resetearPassword(portal,
										usuarioSeleccionado.getUser_name(),
										password)) {
									showInformationMessage(nombreOperacion,
											"Password reseteado exitosamente!");
								} else {
									showErrorMessage(nombreOperacion,
											"No fue posible resetear el password, intente de nuevo.");
								}

							}
						}
					});

		} finally {
			Utils.closeConnection(portal);
		}

	}

	public void onUpdateItem(final Event event) {
		objUsuario = new PDUsuario();
		usuarioSeleccionado = (UsuarioDTO) event.getData();

		args.put(KEY_ARG_DTO, usuarioSeleccionado);
		args.put(KEY_ARG_OPERACION, OPERACION_EDITAR);

		Window wd = (Window) execution.createComponents(Pages.URL_USUARIO,
				wdwUsuario, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}
}
