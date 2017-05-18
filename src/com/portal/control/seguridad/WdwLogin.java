package com.portal.control.seguridad;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import com.portal.base.ComposerBase;
import com.portal.bussines.Security;
import com.portal.security.SecUsuario;
import com.portal.utils.Utils;

public class WdwLogin extends GenericForwardComposer<Component> {
	private static final long serialVersionUID = 1L;

	private static final String ATT_KEY_USER = ComposerBase.ATT_KEY_USER;

	private Textbox txtUsuario;
	private Textbox txtPassword;
	private String nombreOperacion = "Login";
	private Logger log = Logger.getLogger(getClass());

	public WdwLogin() {

	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		log.debug("Login.doAfterCompose()");
	}

	public void onClick$btnLogin() throws NamingException, SQLException {
		log.debug("loguiando...");

		if (txtUsuario.getText() == null
				|| txtUsuario.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese un usuario");
			return;
		}

		if (txtPassword.getText() == null
				|| txtPassword.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese la contraseña");
			return;
		}

		Security objSecurity = new Security();
		Connection portal = null;
		SecUsuario usuario = null;

		try {
			portal = Utils.getConnection();
			usuario = objSecurity.validarLogin(portal, txtUsuario.getText()
					.trim(), txtPassword.getText().trim());

			if (usuario == null) {
				showErrorMessage(nombreOperacion,
						"El usuario no fue encontrado.");
				return;
			}

			if (!"ACTIVO".equalsIgnoreCase(usuario.getUser_estado())) {
				showErrorMessage(nombreOperacion,
						"El usuario no se encuentra activo.");
				return;
			}

			if (usuario.getPerfil() == null) {
				showErrorMessage(nombreOperacion,
						"El usuario no posee perfil asociado.");
				return;
			}

			if (usuario.getPerfil().getOpcionesPerfil().size() == 0) {
				showErrorMessage(nombreOperacion,
						"El perfil del usuario no tiene opciones de aplicación.");
				return;
			}

			log.debug("usuario: " + usuario.toString());
			session.setAttribute(ATT_KEY_USER, usuario);
			Locale preferredLocale = Locale.getDefault();

			execution.sendRedirect("principal/menu-principal.zul");
			session.setAttribute(Attributes.PREFERRED_LOCALE, preferredLocale);

		} finally {
			Utils.closeConnection(portal);
		}

	}

	protected static void showErrorMessage(String titulo, String errorMessage) {
		Messagebox.show(errorMessage, titulo, Messagebox.OK, Messagebox.ERROR);
	}

}
