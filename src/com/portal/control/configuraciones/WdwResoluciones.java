package com.portal.control.configuraciones;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDConfiguracion;
import com.portal.bussines.PDResolucion;
import com.portal.bussines.PDTipoDocumento;
import com.portal.dto.ResolucionDTO;
import com.portal.dto.TipoDocumentoDTO;
import com.portal.enums.Estados;
import com.portal.exception.PDException;
import com.portal.security.SecEmpresa;
import com.portal.utils.Utils;

public class WdwResoluciones extends ComposerBase {
	private static final long serialVersionUID = 1L;
	private static boolean edicion = false;
	private static ResolucionDTO resolucion = null;

	private Listbox ltbResoluciones;
	private Combobox cmbEmpresa, cmbEstado, cmbTipoDocumento;
	private Textbox txtResolucion, txtSerie, txtNumeroInicial, txtNumeroFinal;
	private Datebox txtFechaResolucion;

	private PDTipoDocumento objTipoDocumento;
	private PDResolucion objResolucion;
	private PDConfiguracion objConf;

	public WdwResoluciones() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Resoluciones";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarCatalogos();
	}

	public void onChange$cmbEmpresa() throws NamingException, SQLException {
		ltbResoluciones.getItems().clear();
		limpiarCampos();
		cargarResoluciones();
	}

	public void onClick$btnAceptar() throws NamingException, SQLException {
		Connection conn = null;
		TipoDocumentoDTO tipoDocto = null;
		SecEmpresa empr = null;

		objResolucion = new PDResolucion(userLoguiado);

		if (cmbEmpresa.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione una empresa.");
			return;
		} else {
			empr = cmbEmpresa.getSelectedItem().getValue();
		}

		if (txtResolucion.getText() == null
				|| txtResolucion.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion,
					"Ingrese una descripción de resolución");
			return;
		}

		if (txtFechaResolucion.getValue() == null) {
			showErrorMessage(nombreOperacion,
					"Ingrese una fecha de resolución.");
			return;
		}

		if (cmbTipoDocumento.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion,
					"Seleccione un tipo de documento.");
			return;
		} else {
			tipoDocto = cmbTipoDocumento.getSelectedItem().getValue();
		}

		if (txtSerie.getText() == null
				|| txtSerie.getText().trim().equalsIgnoreCase("")) {
			showErrorMessage(nombreOperacion, "Ingrese la serie.");
			return;
		}

		if (txtNumeroInicial.getText() == null
				|| txtNumeroInicial.getText().trim().equalsIgnoreCase("")
				|| !Utils.isNumeric(txtNumeroInicial.getText())) {
			showErrorMessage(nombreOperacion, "Número inicial inválido.");
			return;
		}

		if (txtNumeroFinal.getText() == null
				|| txtNumeroFinal.getText().trim().equalsIgnoreCase("")
				|| !Utils.isNumeric(txtNumeroFinal.getText())) {
			showErrorMessage(nombreOperacion, "Número final inválido.");
			return;
		}

		if (cmbEstado.getSelectedIndex() < 0) {
			showErrorMessage(nombreOperacion, "Seleccione el estado.");
			return;
		}

		ResolucionDTO res = new ResolucionDTO();
		res.setCod_empresa(Long.parseLong(empr.getCod_empresa()));
		res.setResolucion(txtResolucion.getText().trim());
		res.setDoc_numero_actual(Long.parseLong(txtNumeroInicial.getText()
				.trim()));
		res.setDoc_numero_final(Long.parseLong(txtNumeroFinal.getText().trim()));
		res.setDoc_numero_inicial(Long.parseLong(txtNumeroInicial.getText()
				.trim()));
		res.setFecha_resolucion(txtFechaResolucion.getText().trim());
		res.setDoc_tipo(tipoDocto.getDoc_tipo());
		res.setDoc_serie(txtSerie.getText().trim());

		boolean resultado = false;

		try {
			conn = Utils.getConnection();
			conn.setAutoCommit(false);

			if (!edicion) {
				ResolucionDTO resolucionValidar = objResolucion
						.existeResolucion(conn, res.getCod_empresa(),
								res.getDoc_tipo(), res.getDoc_serie());

				if (resolucionValidar == null) {
					if (!objResolucion.existeResolucionActiva(conn,
							res.getCod_empresa(), res.getDoc_tipo())) {
						res.setCod_resolucion(objResolucion.getSecuencia(conn));

						if (objResolucion.addResolucion(conn, res)) {
							if (objResolucion.addDetalleRes(conn,
									res.getCod_resolucion(), res.getDoc_tipo(),
									res.getDoc_serie(),
									res.getDoc_numero_actual(),
									res.getCod_empresa())) {
								resultado = true;
								showInformationMessage(nombreOperacion,
										"Resolución agregada exitosamente.");
								limpiarCampos();
								cargarResoluciones();
							} else {
								showErrorMessage(nombreOperacion,
										"No fue posible agregar el primer correlativo.");
							}

						} else {
							showErrorMessage(nombreOperacion,
									"No fue posible agregar la resolución.");
						}
					} else {
						showErrorMessage(
								nombreOperacion,
								"Existe una resolución de facturacióna activa, para agregar otra debe desactivar primero la anterior.");
					}

				} else {
					showErrorMessage(nombreOperacion,
							"La resolución con serie y tipo ya se encuentra ingresada.");
				}

			} else {
				Long numeroFinal = Long.parseLong(txtNumeroFinal.getText());
				log.debug("valor de estado: "
						+ cmbEstado.getSelectedItem().getValue());
				Long estado = Long.valueOf(cmbEstado.getSelectedItem()
						.getValue().toString());
				Long codResolucion = resolucion.getCod_resolucion();

				if (objResolucion.editarResolucion(conn, numeroFinal, estado,
						codResolucion)) {
					resultado = true;
					showInformationMessage(nombreOperacion,
							"Resolución editada exitosamente.");
					limpiarCampos();
					cargarResoluciones();
				} else {
					showErrorMessage(nombreOperacion,
							"No fue posible editar la resolución.");
				}
			}
		} finally {
			if (resultado) {
				Utils.commit(conn);
			} else {
				Utils.rollback(conn);
			}
			Utils.closeConnection(conn);
		}

	}

	public void onDoubleClick$ltbResoluciones() throws WrongValueException,
			ParseException {
		if (ltbResoluciones.getItems() != null
				&& ltbResoluciones.getItems().size() > 0
				&& ltbResoluciones.getSelectedIndex() > -1) {
			edicion = true;
			resolucion = ltbResoluciones.getSelectedItem().getValue();

			int estado = 0;
			if (resolucion.getEstado().equalsIgnoreCase("ACTIVO")) {
				estado = 1;
			}
			cmbEstado.setSelectedIndex(indexEstado(estado));

			cmbTipoDocumento.setSelectedIndex(indexTipoDocto(resolucion
					.getDoc_tipo()));

			txtResolucion.setText(resolucion.getResolucion());
			txtSerie.setText(resolucion.getDoc_serie());
			txtNumeroInicial.setText(resolucion.getDoc_numero_inicial()
					.toString());
			txtNumeroFinal.setText(resolucion.getDoc_numero_final().toString());
			txtFechaResolucion.setValue(Utils.getDate(
					resolucion.getFecha_resolucion(), "YYYY-MM-DD"));
			deshabilitarEdicion(true);

		}
	}

	public void onClick$btnGenerarCorrelativos() throws NamingException,
			SQLException, PDException {
		Connection conn = null;
		boolean resultado = false;
		if (ltbResoluciones.getItems() != null
				&& ltbResoluciones.getItems().size() > 0
				&& ltbResoluciones.getSelectedIndex() > -1) {
			resolucion = ltbResoluciones.getSelectedItem().getValue();

			log.debug("número actual: " + resolucion.getDoc_numero_actual());

			if (resolucion.getEstado().equalsIgnoreCase(Estados.ACTIVO.name())) {
				try {
					conn = Utils.getConnection();
					conn.setAutoCommit(false);
					objConf = new PDConfiguracion();
					objResolucion = new PDResolucion(userLoguiado);

					int cantidadGenerar = Integer.valueOf(objConf.getValorConf(
							conn, PDConfiguracion.CONF_CANTIDAD_CORRELATIVOS));

					int restantes = resolucion.getDoc_numero_final().intValue()
							- resolucion.getDoc_numero_actual().intValue();

					log.debug("cantidad restantes de la resolución: "
							+ restantes);

					if (restantes <= 0) {
						showErrorMessage(nombreOperacion,
								"Se alcanzó el limite de la resolución para generar correlativos.");
						return;
					}

					if (cantidadGenerar > restantes) {
						cantidadGenerar = restantes;
					}

					log.debug("cantidad de correlativos a generar: "
							+ cantidadGenerar);
					log.debug("se generaran los correlativos del: "
							+ resolucion.getDoc_numero_actual()
							+ " al: "
							+ (cantidadGenerar + resolucion
									.getDoc_numero_actual()));

					for (int i = 0; i < cantidadGenerar; i++) {
						resolucion.setDoc_numero_actual(resolucion
								.getDoc_numero_actual() + 1);
						if (!objResolucion.addDetalleRes(conn,
								resolucion.getCod_resolucion(),
								resolucion.getDoc_tipo(),
								resolucion.getDoc_serie(),
								resolucion.getDoc_numero_actual(),
								resolucion.getCod_empresa())) {
							throw new PDException(
									"No fue posible ingresar el correlativo: "
											+ resolucion.getDoc_numero_actual());
						}
					}

					if (objResolucion.setNumeroActual(conn,
							resolucion.getCod_resolucion(),
							resolucion.getDoc_numero_actual())) {
						cargarResoluciones();
						resultado = true;
						showInformationMessage(nombreOperacion,
								"Se han generado " + cantidadGenerar
										+ " correlativos nuevos.");

					} else {
						showErrorMessage(nombreOperacion,
								"No fue posible generar los correlativos.");
					}

				} finally {
					if (resultado) {
						conn.commit();
					} else {
						conn.rollback();
					}

					Utils.closeConnection(conn);
				}

			} else {
				showErrorMessage(nombreOperacion,
						"La resolución debe estar activa para generar correlativos.");
			}
		} else {
			showErrorMessage(nombreOperacion, "Seleccione una resolución.");
			resolucion = null;
		}
	}

	private int indexEstado(int value) {
		log.debug("buscando item: " + value);
		for (int i = 0; i < cmbEstado.getItems().size(); i++) {
			int estado = cmbEstado.getItems().get(i).getValue();
			if (value == estado) {
				return i;
			}
		}

		return -1;
	}

	private int indexTipoDocto(String value) {
		for (int i = 0; i < cmbTipoDocumento.getItems().size(); i++) {
			TipoDocumentoDTO tipo = cmbTipoDocumento.getItems().get(i)
					.getValue();
			if (value.equalsIgnoreCase(tipo.getDoc_tipo())) {
				return i;
			}
		}
		return -1;
	}

	public void onClick$btnCancelar() {
		limpiarCampos();
	}

	private void limpiarCampos() {
		edicion = false;
		resolucion = null;
		cmbEstado.setValue(DEFAULT_LABEL_CMB);
		cmbTipoDocumento.setValue(DEFAULT_LABEL_CMB);
		txtResolucion.setText(null);
		txtSerie.setText(null);
		txtNumeroInicial.setText(null);
		txtNumeroFinal.setText(null);
		txtFechaResolucion.setText(null);
		deshabilitarEdicion(false);
	}

	private void cargarEmpresas() {
		if (usuario.getEmpresas() != null) {
			for (SecEmpresa emp : usuario.getEmpresas()) {
				Comboitem item = new Comboitem();
				item.setLabel(emp.getEmp_nombre());
				item.setValue(emp);
				item.setParent(cmbEmpresa);
			}
		}
	}

	private void cargarCatalogos() throws NamingException, SQLException {
		cargarEmpresas();
		cargarEstados();
		cargarTipoDocumento();
	}

	private void cargarTipoDocumento() throws NamingException, SQLException {
		Connection conn = null;
		objTipoDocumento = new PDTipoDocumento();
		try {
			conn = Utils.getConnection();

			for (TipoDocumentoDTO tipo : objTipoDocumento
					.getTiposDocumento(conn)) {
				Comboitem item = new Comboitem();
				item.setValue(tipo);
				item.setLabel(tipo.getDescripcion());
				item.setParent(cmbTipoDocumento);
			}

		} finally {
			Utils.closeConnection(conn);
		}

	}

	private void cargarEstados() {
		for (Estados estado : Estados.values()) {
			Comboitem item = new Comboitem();
			item.setValue(estado.CODIGO);
			item.setLabel(estado.name());
			item.setParent(cmbEstado);
		}
	}

	private void cargarResoluciones() throws NamingException, SQLException {
		log.debug("cargando resoluciones...");
		Connection conn = null;
		objResolucion = new PDResolucion(userLoguiado);

		if (cmbEmpresa.getSelectedIndex() > -1) {
			SecEmpresa emp = cmbEmpresa.getSelectedItem().getValue();
			try {
				conn = Utils.getConnection();

				List<ResolucionDTO> resoluciones = objResolucion
						.getResoluciones(conn,
								Long.parseLong(emp.getCod_empresa()));

				log.debug("cantidad de resoluciones: " + resoluciones.size());

				ltbResoluciones.setModel(new ListModelList<ResolucionDTO>(
						resoluciones));
				ltbResoluciones.renderAll();

			} finally {
				Utils.closeConnection(conn);
			}
		} else {
			ltbResoluciones.getItems().clear();
			log.debug("no hay empresa seleccionada.");
		}

	}

	private void deshabilitarEdicion(boolean enabled) {
		cmbTipoDocumento.setDisabled(enabled);
		txtResolucion.setDisabled(enabled);
		txtSerie.setDisabled(enabled);
		txtNumeroInicial.setDisabled(enabled);
		txtFechaResolucion.setDisabled(enabled);
	}

}
