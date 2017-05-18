package com.portal.control.seguridad;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDEmpresa;
import com.portal.bussines.PDPerfil;
import com.portal.bussines.PDUsuario;
import com.portal.dto.EmpresaDTO;
import com.portal.dto.PerfilDTO;
import com.portal.dto.UsuarioDTO;
import com.portal.utils.SecurityPortal;
import com.portal.utils.Utils;

public class WdwUsuario extends ComposerBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(WdwUsuario.class);

	/************ WIRE *************/

	private Window wdwUsuario;
	private Textbox txtUsuario, txtNombres, txtApellido, txtEmail;
	private Combobox cmbEmpresa, cmbPerfil;
	private Listbox ltbEmpresasAsignadas;

	/************ VARIABLES *************/
	private PDUsuario objUsuario;
	private List<EmpresaDTO> empresasAsignadas = new ArrayList<EmpresaDTO>();

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		nombreOperacion = "Usuario";
		cargarInfo();
		cargarUsuarioEmpresa();
	}

	public void onClick$btnCancelar() {
		wdwUsuario.detach();
	}

	public void onClick$btnGuardar() throws NamingException, SQLException {
		log.debug("Guardando usuario en base de datos");

		if (txtUsuario.getText() == null
				|| txtUsuario.getText().trim().equalsIgnoreCase("")) {
			return;
		}

		if (txtNombres.getText() == null
				|| txtNombres.getText().trim().equalsIgnoreCase("")) {
			return;
		}

		if (txtApellido.getText() == null
				|| txtApellido.getText().trim().equalsIgnoreCase("")) {
			return;
		}

		if (cmbPerfil.getSelectedItem() == null) {
			showErrorMessage(nombreOperacion,
					"Seleccione un perfil para asignar al usuario.");
			return;
		}

		if (ltbEmpresasAsignadas.getItemCount() == 0) {
			showErrorMessage(nombreOperacion,
					"No ha asignado empresas al usuario.");
			return;
		}

		Connection portal = null;
		PerfilDTO perfil = cmbPerfil.getSelectedItem().getValue();
		try {
			portal = Utils.getConnection();

			objUsuario = new PDUsuario();

			if (arg.get(KEY_ARG_OPERACION) != null
					&& arg.get(KEY_ARG_OPERACION).toString()
							.equals(OPERACION_EDITAR)) {

				portal.setAutoCommit(false);
				if (objUsuario
						.editarUsuario(portal, txtUsuario.getText().trim(),
								txtNombres.getText().trim(), txtApellido
										.getText().trim(), txtEmail.getText()
										.trim(), userLoguiado, perfil
										.getCod_perfil(), empresasAsignadas)) {
					portal.commit();
					showInformationMessage(nombreOperacion,
							"Usuario actualizado exitosamente");
					Events.echoEvent("onClose", wdwUsuario, null);
				} else {
					portal.rollback();
					showErrorMessage(nombreOperacion,
							"No fue posible actualizar el usuario, intente de nuevo");
				}

			} else {
				if (objUsuario.buscarUsuario(portal, txtUsuario.getText()
						.trim())) {
					showErrorMessage(nombreOperacion,
							"El usuario ya existe, intente con otro.");
					return;
				}

				String password = SecurityPortal.getHmacSHA1(txtUsuario
						.getText().trim());

				portal.setAutoCommit(false);
				if (objUsuario
						.addUsuario(portal, txtUsuario.getText().trim(),
								password, txtNombres.getText().trim(),
								txtApellido.getText().trim(), txtEmail
										.getText().trim(), userLoguiado, perfil
										.getCod_perfil(), empresasAsignadas)) {
					portal.commit();
					showInformationMessage(nombreOperacion,
							"Usuario ingresado exitosamente");
					Events.echoEvent("onClose", wdwUsuario, null);
				} else {
					portal.rollback();
					showErrorMessage(nombreOperacion,
							"No fue posible ingresar el usuario, intente de nuevo");
				}
			}

		} finally {
			Utils.closeConnection(portal);
		}

	}

	public void onClick$btnAgregarEmpresa() {
		if (cmbEmpresa.getSelectedItem() == null) {
			showErrorMessage(nombreOperacion, "Seleccione una empresa.");
			return;
		}

		EmpresaDTO empresa = (EmpresaDTO) cmbEmpresa.getSelectedItem()
				.getValue();
		int index = buscarElemento(empresa);

		if (index > -1) {
			showErrorMessage(nombreOperacion,
					"Ya se encuentra agregada en la lista.");
			return;
		} else {
			empresasAsignadas.add(empresa);
		}
		ltbEmpresasAsignadas.setModel(new ListModelList<EmpresaDTO>(
				empresasAsignadas));

	}

	public void onClick$btnRemoverEmpresa() {
		log.debug("empresas seleccionadas: "
				+ ltbEmpresasAsignadas.getSelectedCount());
		if (ltbEmpresasAsignadas.getSelectedCount() == 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione una empresa para removerla.");
			return;
		}

		int index = buscarElemento((EmpresaDTO) ltbEmpresasAsignadas
				.getSelectedItem().getValue());

		empresasAsignadas.remove(index);

		ltbEmpresasAsignadas.setModel(new ListModelList<EmpresaDTO>(
				empresasAsignadas));

	}

	private final int buscarElemento(EmpresaDTO empresa) {

		for (int i = 0; i < empresasAsignadas.size(); i++) {
			if (empresa.getCod_empresa().equalsIgnoreCase(
					empresasAsignadas.get(i).getCod_empresa()))
				return i;
		}

		return -1;
	}

	private final int buscarElemento(PerfilDTO perfil) {
		for (int i = 0; i < cmbPerfil.getItems().size(); i++) {
			PerfilDTO each = cmbPerfil.getItems().get(i).getValue();

			if (perfil.getCod_perfil().equalsIgnoreCase(each.getCod_perfil()))
				return i;
		}

		return -1;
	}

	private void cargarInfo() throws NamingException, SQLException {
		Connection portal = null;
		try {
			portal = Utils.getConnection();

			PDPerfil objPerfil = new PDPerfil();
			cmbPerfil.setModel(new ListModelList<PerfilDTO>(objPerfil
					.getPerfiles(portal)));

			PDEmpresa objEmpresa = new PDEmpresa();
			cmbEmpresa.setModel(new ListModelList<EmpresaDTO>(objEmpresa
					.getEmpresasActivas(portal)));

		} finally {
			Utils.closeConnection(portal);
		}
	}

	public void onAfterRender$cmbPerfil() throws NamingException, SQLException {
		if (arg.get(KEY_ARG_OPERACION) != null
				&& arg.get(KEY_ARG_OPERACION).toString()
						.equals(OPERACION_EDITAR))
			cargarPerfilUsuario();
	}

	private void cargarPerfilUsuario() throws NamingException, SQLException {
		UsuarioDTO user = (UsuarioDTO) arg.get(KEY_ARG_DTO);

		txtUsuario.setText(user.getUser_name());
		txtUsuario.setDisabled(true);
		txtNombres.setText(user.getUser_nombres());
		txtApellido.setText(user.getUser_apellidos());
		txtEmail.setText(user.getUser_email());

		Connection portal = null;
		try {
			portal = Utils.getConnection();
			PDUsuario objUsuario = new PDUsuario();

			PerfilDTO perfil = objUsuario.getUsuarioPerfil(portal,
					user.getUser_name());

			int index = buscarElemento(perfil);

			if (index > -1)
				cmbPerfil.setSelectedIndex(index);

		} finally {
			Utils.closeConnection(portal);
		}
	}

	private void cargarUsuarioEmpresa() throws SQLException, NamingException {

		if (arg.get(KEY_ARG_OPERACION) != null
				&& arg.get(KEY_ARG_OPERACION).toString()
						.equals(OPERACION_EDITAR)) {
			log.debug("cargando información de empresa del usuario...");
			UsuarioDTO user = (UsuarioDTO) arg.get(KEY_ARG_DTO);

			objUsuario = new PDUsuario();
			Connection portal = null;

			try {
				portal = Utils.getConnection();
				empresasAsignadas.clear();
				for (EmpresaDTO empresa : objUsuario.getUsuarioEmpresas(portal,
						user.getUser_name())) {
					empresasAsignadas.add(empresa);
				}
			} finally {
				Utils.closeConnection(portal);
			}

			ltbEmpresasAsignadas.setModel(new ListModelList<EmpresaDTO>(
					empresasAsignadas));

		}

	}

}
