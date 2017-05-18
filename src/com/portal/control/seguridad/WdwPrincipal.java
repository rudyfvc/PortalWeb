package com.portal.control.seguridad;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Menu;

import com.portal.base.ComposerBase;
import com.portal.utils.Utils;

public class WdwPrincipal extends ComposerBase {
	private static final long serialVersionUID = 1L;
	private Menu menuUsuario;
	private Label lblFecha;

	public WdwPrincipal() {
		log = Logger.getLogger(getClass());
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if (usuario != null) {
			menuUsuario.setLabel(usuario.getUser_name());
			lblFecha.setValue(Utils.getSystemDate());
		}

		log.debug("mostrando menu principal...");
	}

	public void onClick$lblLogout() {
		log.debug("saliendo de la aplicación...");
		session.setAttribute(ATT_KEY_USER, null);
		execution.sendRedirect(Utils.getUrlLogin());
	}

}
