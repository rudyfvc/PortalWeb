package com.portal.control.seguridad;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.base.Pages;
import com.portal.bussines.PDPerfil;
import com.portal.dto.PerfilDTO;
import com.portal.utils.Utils;

public class WdwPerfiles extends ComposerBase {

	private static final long serialVersionUID = 1L;
	private Listbox ltbPerfiles;
	private Window wdwPerfil;

	/************ VARIABLES *************/
	private ListModelList<PerfilDTO> modelo = new ListModelList<PerfilDTO>();
	private PDPerfil objPerfil;
	private List<PerfilDTO> listaPerfiles;
	private Map<String, Object> args = new HashMap<String, Object>();
	private Connection portal = null;
	private PerfilDTO seleccion;

	public WdwPerfiles() {
		super.log = Logger.getLogger(getClass());
		nombreOperacion = "Perfiles";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarPerfiles();
	}

	EventListener<Event> onClose = new EventListener<Event>() {
		@Override
		public void onEvent(Event arg0) throws Exception {
			cargarPerfiles();
		}
	};

	@Command
	public void onClick$btnAgregar() {
		args.put(KEY_ARG_OPERACION, OPERACION_NUEVO);
		Window wd = (Window) execution.createComponents(Pages.URL_PERFIL,
				wdwPerfil, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

	private void cargarPerfiles() throws SQLException, NamingException {
		log.debug("cargando perfiles disponibles...");
		modelo.clear();
		objPerfil = new PDPerfil();

		try {
			portal = Utils.getConnection();
			listaPerfiles = objPerfil.getPerfiles(portal);

			for (PerfilDTO perfil : listaPerfiles) {
				modelo.add(perfil);
			}

			ltbPerfiles.setModel(modelo);
		} finally {
			Utils.closeConnection(portal);
		}

	}

	public void onHabilitarItem(final Event event) throws NamingException,
			SQLException {

		objPerfil = new PDPerfil();
		seleccion = (PerfilDTO) event.getData();

		try {
			portal = Utils.getConnection();

			if (seleccion.getPer_estado().equalsIgnoreCase(ESTADO_ACTIVO)) {
				if (objPerfil.cambiarEstado(portal, "0",
						seleccion.getCod_perfil())) {
					showInformationMessage(nombreOperacion,
							"Perfil desactivado exitosamente.");
					cargarPerfiles();
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible desactivar el perfil, intente de nuevo.");
				}
			} else {
				if (objPerfil.cambiarEstado(portal, "1",
						seleccion.getCod_perfil())) {
					showInformationMessage(nombreOperacion,
							"Perfil activado exitosamente.");
					cargarPerfiles();
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible activar el perfil, intente de nuevo.");
				}
			}
		} finally {
			Utils.closeConnection(portal);
		}
	}

	public void onUpdateItem(final Event event) {
		objPerfil = new PDPerfil();
		seleccion = (PerfilDTO) event.getData();

		args.put(KEY_ARG_DTO, seleccion);
		args.put(KEY_ARG_OPERACION, OPERACION_EDITAR);

		Window wd = (Window) execution.createComponents(Pages.URL_PERFIL,
				wdwPerfil, args);
		wd.addEventListener("onClose", onClose);
		wd.doModal();
	}

}
