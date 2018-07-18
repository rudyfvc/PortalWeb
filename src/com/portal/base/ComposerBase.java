package com.portal.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Messagebox;

import com.portal.exception.PDException;
import com.portal.security.SecUsuario;
import com.portal.utils.Utils;

public abstract class ComposerBase extends GenericForwardComposer<Component> {

	private static final long serialVersionUID = 1L;

	protected static final String DEFAULT_VALUE_CMB = "-1";
	protected static final String DEFAULT_LABEL_CMB = "- Seleccione -";
	protected static final String ESTADO_ACTIVO = "ACTIVO";
	protected static final String ESTADO_INACTIVO = "INACTIVO";

	public static final String ATT_KEY_USER = "sessionUser";

	protected Logger log;
	protected String nombreOperacion;
	protected String userLoguiado;
	protected SecUsuario usuario;
	protected Map<String, Object> args;

	public static final String OPERACION_EDITAR = "editar";
	public static final String OPERACION_NUEVO = "nuevo";
	public static final String OPERACION_CONSULTA = "consulta";
	public static final String KEY_ARG_OPERACION = "operacion";
	public static final String KEY_ARG_DTO = "dto";

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		args = new HashMap<String, Object>();
		validarLogin();
	}

	protected void doExitApplication() throws Exception {
		session.setAttribute(ATT_KEY_USER, null);
		execution.sendRedirect(Utils.getUrlLogin());
	}

	protected void exitApplication() throws Exception {
		doExitApplication();
	}

	protected static final void showErrorMessage(String titulo,
			String errorMessage) {
		Messagebox.show(errorMessage, titulo, Messagebox.OK, Messagebox.ERROR);
	}

	protected static final void showInformationMessage(String titulo,
			String message) {
		Messagebox.show(message, titulo, Messagebox.OK, Messagebox.INFORMATION);
	}

	protected static final void showConfirmMessage(String titulo,
			String message, EventListener<Event> event) {
		Messagebox.show(message, titulo, Messagebox.YES + Messagebox.NO,
				Messagebox.QUESTION, event);
	}

	private void validarLogin() {
		usuario = (SecUsuario) session.getAttribute(ATT_KEY_USER);

		if (usuario == null) {
			execution.sendRedirect(Utils.getUrlLogin());
			userLoguiado = null;
		} else {
			userLoguiado = usuario.getUser_name();
		}

	}

	protected final String getOperacion() {
		if (arg.get(KEY_ARG_OPERACION) != null) {
			return arg.get(KEY_ARG_OPERACION).toString();
		}

		return "";
	}

	protected Object getValorOperacion() {
		return arg.get(KEY_ARG_DTO);
	}

	protected static final void lanzarError(String error) throws PDException {
		throw new PDException(error);
	}

}


