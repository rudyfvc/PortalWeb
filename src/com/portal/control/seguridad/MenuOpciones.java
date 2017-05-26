package com.portal.control.seguridad;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zul.Include;
import org.zkoss.zul.Menu;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.portal.base.ComposerBase;
import com.portal.security.Node;
import com.portal.security.SecPerfilOpcion;
import com.portal.security.SecUsuario;

public class MenuOpciones extends ComposerBase {

	private static final long serialVersionUID = 1L;

	private Menubar menubar;
	private Menu menu;
	private Menupopup menuPopUp;
	private Menuitem menuItem;

	// private String itemWidth = "200px";
	private String classIconosMenu = "bigicons";

	public MenuOpciones() {
		log = Logger.getLogger(getClass());
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		createMenu();
	}

	Map<String, Node> hm = new HashMap<String, Node>();

	private void createMenu() {
		SecUsuario user = (SecUsuario) session.getAttribute(ATT_KEY_USER);

		Node treeRootNode = new Node(null);
		treeRootNode.setId("root");

		if (user != null) {
			List<SecPerfilOpcion> opciones = user.getPerfil()
					.getOpcionesPerfil();

			Iterator<SecPerfilOpcion> it = opciones.iterator();

			while (it.hasNext()) {
				SecPerfilOpcion opc = it.next();
				if (opc.getCod_aplicacion_opcion_padre().equalsIgnoreCase("0")) {
					Node childNode = addChild(treeRootNode,
							opc.getCod_aplicacion_opcion(),
							opc.getOpc_nombre(), opc.getOpc_url(),
							opc.getOpc_image());
					hm.put(opc.getCod_aplicacion_opcion(), childNode);
				} else {
					Node parentNode = hm.get(opc
							.getCod_aplicacion_opcion_padre());
					Node ch = addChild(parentNode,
							opc.getCod_aplicacion_opcion(),
							opc.getOpc_nombre(), opc.getOpc_url(),
							opc.getOpc_image());
					hm.put(opc.getCod_aplicacion_opcion(), ch);
				}
			}

			armarMenu(menubar, treeRootNode);
		}

	}

	private void armarMenu(Component cmp, Node nodo) {

		for (Node each : nodo.getChildren()) {
			if (each.getChildren().size() > 0) {
				menu = new Menu();
				menu.setLabel(each.getNombre());
				menu.setImage(each.getImage());
				menu.setSclass(classIconosMenu);
				// menu.setWidth(itemWidth);
				menuPopUp = new Menupopup();
				menuPopUp.setSclass(classIconosMenu);
				menuPopUp.setParent(menu);
				// menuPopUp.setWidth(itemWidth);
				menu.setParent(cmp);
				armarMenu(menuPopUp, each);
			} else {
				menuItem = new Menuitem(each.getNombre());
				menuItem.setParent(cmp);
				menuItem.setId(each.getId());
				// menuItem.setWidth(itemWidth);
				menuItem.setImage(each.getImage());
				menuItem.setValue(each.getUrl());
				menuItem.setSclass(classIconosMenu);
				menuItem.addEventListener("onClick",
						new EventListener<Event>() {
							@Override
							public void onEvent(Event ev) throws Exception {
								Menuitem item = (Menuitem) ev.getTarget();
								log.debug("mostrando page: " + item.getValue());
								Include include = (Include) Selectors
										.iterable(getPage(), "#mainInclude")
										.iterator().next();
								include.setSrc(item.getValue());
							}
						});
			}
		}
	}

	private Node addChild(Node parent, String id, String nombre, String url,
			String image) {
		Node node = new Node(parent);
		node.setId(id);
		node.setNombre(nombre);
		node.setUrl(url);
		node.setImage(image);
		parent.getChildren().add(node);

		return node;
	}

}
