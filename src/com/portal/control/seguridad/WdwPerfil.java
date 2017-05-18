package com.portal.control.seguridad;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDPerfil;
import com.portal.bussines.Security;
import com.portal.dto.PerfilDTO;
import com.portal.security.Node;
import com.portal.utils.Utils;

public class WdwPerfil extends ComposerBase {
	private static final long serialVersionUID = 1L;

	public WdwPerfil() {
		super.log = Logger.getLogger(getClass());
		nombreOperacion = "Perfil";
	}

	/**************** WIRE ****************/
	private Tree treeOpciones;
	private Treeitem treeitem;
	private Treechildren treeChildren;
	private Textbox txtNombrePerfil;
	private Window wdwPerfil;

	/**************** VARIABLES ****************/
	private String tipoOperacion;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		tipoOperacion = (String) arg.get(WdwPerfiles.KEY_ARG_OPERACION);
		cargarOpciones();
	}

	private void cargarOpciones() throws SQLException, NamingException {
		treeChildren = new Treechildren();
		treeChildren.setParent(treeOpciones);
		Security objSec = new Security();
		Connection conn = null;
		PerfilDTO perfil = (PerfilDTO) arg.get(KEY_ARG_DTO);
		try {

			conn = Utils.getConnection();
			if (tipoOperacion.equalsIgnoreCase(WdwPerfiles.OPERACION_NUEVO)) {
				log.debug("cargando opciones para nuevo perfil...");
				armarArbol(treeChildren, objSec.getAllOpciones(conn));
			} else {
				log.debug("cargando opciones para edición de perfil...");
				txtNombrePerfil.setText(perfil.getPer_nombre());
				armarArbol(treeChildren,
						objSec.getOpciones(conn, perfil.getCod_perfil()));
			}
		} finally {
			Utils.closeConnection(conn);
		}

	}

	public void onClick$btnGuardar() throws WrongValueException, SQLException,
			NamingException {
		if (txtNombrePerfil.getText() == null
				|| txtNombrePerfil.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese un nombre de perfil");
			return;
		}

		PDPerfil objPerfil = new PDPerfil();
		Connection portal = null;

		List<String> seleccionadas = new ArrayList<String>();
		Iterator<Treeitem> it = treeOpciones.getSelectedItems().iterator();

		while (it.hasNext()) {
			seleccionadas.add(it.next().getId());
		}

		try {
			portal = Utils.getConnection();
			portal.setAutoCommit(false);
			boolean res = false;

			if (tipoOperacion.equalsIgnoreCase(WdwPerfiles.OPERACION_NUEVO)) {
				res = objPerfil.addPerfil(portal, "1", txtNombrePerfil
						.getText().trim(), usuario.getUser_name(),
						seleccionadas);
			} else {
				PerfilDTO perfil = (PerfilDTO) arg.get(KEY_ARG_DTO);
				res = objPerfil.updatePerfil(portal, "1", Long
						.valueOf(perfil.getCod_perfil()), txtNombrePerfil
						.getText().trim(), usuario.getUser_name(),
						seleccionadas);
			}

			if (res) {
				portal.commit();
				showInformationMessage(nombreOperacion,
						"Perfil guardado exitosamente!");
				Events.echoEvent("onClose", wdwPerfil, null);
			} else {
				portal.rollback();
				showErrorMessage(nombreOperacion,
						"No fue posible guardar el perfil, intente de nuevo.");
			}

		} finally {
			Utils.closeConnection(portal);
		}

	}

	private void armarArbol(Component cmp, Node nodo) {
		for (Node each : nodo.getChildren()) {
			if (each.getChildren().size() > 0) {
				treeitem = new Treeitem(each.getNombre());
				treeitem.setId(each.getId());
				treeitem.setParent(cmp);
				treeitem.setSelected(each.isSelected());
				treeitem.addEventListener("onClick", ev);
				treeChildren = new Treechildren();
				treeChildren.setParent(treeitem);
				armarArbol(treeChildren, each);
			} else {
				treeitem = new Treeitem(each.getNombre(), each);
				treeitem.setId(each.getId());
				treeitem.setSelected(each.isSelected());
				treeitem.setParent(cmp);
				treeitem.addEventListener("onClick", ev);
			}
		}
	}

	private EventListener<Event> ev = new EventListener<Event>() {
		@Override
		public void onEvent(Event ev) throws Exception {
			Treeitem item = (Treeitem) ev.getTarget();
			seleccionar(item);
		}
	};

	private void seleccionar(Treeitem item) {
		if (item.getTreechildren() != null) {
			List<Treeitem> treeitems = item.getTreechildren().getChildren();
			for (Treeitem i : treeitems) {
				i.setSelected(item.isSelected());
				seleccionar(i);
			}
		}
	}

}
