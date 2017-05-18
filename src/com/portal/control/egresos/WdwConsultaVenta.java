package com.portal.control.egresos;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDDocumentoSal;
import com.portal.bussines.PDPeriodo;
import com.portal.dto.DocumentoSalDetalleDTO;
import com.portal.dto.DocumentoSalEncabezadoDTO;
import com.portal.dto.PeriodoDTO;
import com.portal.enums.EstadosDocumento;
import com.portal.utils.Utils;

public class WdwConsultaVenta extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Window wdwConsultaVenta;
	private Textbox txtEmpresa, txtPeriodo, txtTipo, txtNumero, txtCliente,
			txtEstado, txtFecha, txtSerie;
	private Listbox ltbDetalle;
	private Label lblSubTotal, lblDescuento, lblTotal;
	private Button btnAnular;

	private List<DocumentoSalDetalleDTO> items;
	private DocumentoSalEncabezadoDTO enc;

	private PDDocumentoSal objDocumento;
	private PDPeriodo objPeriodo;
	private Long codMovimiento;

	public WdwConsultaVenta() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Venta";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		cargarInfo();
	}

	private void cargarInfo() throws WrongValueException, ParseException,
			NamingException, SQLException {
		Connection conn = null;
		enc = (DocumentoSalEncabezadoDTO) arg.get(KEY_ARG_DTO);
		objPeriodo = new PDPeriodo(userLoguiado);

		objDocumento = new PDDocumentoSal(userLoguiado);
		codMovimiento = enc.getCod_movimiento();
		txtEmpresa.setText(enc.getNombre_empresa());
		txtPeriodo.setText(enc.getCod_periodo());
		txtTipo.setText(enc.getDoc_tipo());
		txtFecha.setText(enc.getDoc_fecha());
		txtNumero.setText(enc.getDoc_numero().toString());
		txtCliente.setText(enc.getCliente());
		txtSerie.setText(enc.getDoc_serie());

		String estado = EstadosDocumento.VIGENTE.name();

		if (enc.getDoc_estado().equalsIgnoreCase(
				EstadosDocumento.ANULADO.ESTADO)) {
			estado = EstadosDocumento.ANULADO.name();
			showBotonAnular(false);
		}

		txtEstado.setText(estado);

		try {
			conn = Utils.getConnection();
			items = objDocumento.getDetalle(conn, enc.getCod_movimiento());
			ltbDetalle
					.setModel(new ListModelList<DocumentoSalDetalleDTO>(items));

			PeriodoDTO periodo = objPeriodo.getPeriodoAbierto(conn,
					enc.getCod_empresa());

			if (periodo == null
					|| !periodo.getCod_periodo().equalsIgnoreCase(
							txtPeriodo.getText())) {
				showBotonAnular(false);
			}

			cargarMontos();
		} finally {
			Utils.closeConnection(conn);
		}

	}

	private EventListener<Event> event = new EventListener<Event>() {
		public void onEvent(Event event) throws Exception {
			if (!Messagebox.ON_NO.equals(event.getName())) {
				Connection conn = null;
				if (txtEstado.getText() != null) {
					if (txtEstado.getText().trim()
							.equalsIgnoreCase(EstadosDocumento.VIGENTE.name())) {

						try {
							conn = Utils.getConnection();

							String estadoPeriodo = objPeriodo.getEstadoPeriodo(
									conn, enc.getCod_empresa(),
									enc.getCod_periodo());

							if (estadoPeriodo
									.equalsIgnoreCase(PDPeriodo.ESTADO_ABIERTO)) {
								if (objDocumento.anularDocumento(conn,
										codMovimiento)) {
									showInformationMessage(nombreOperacion,
											"Documento anulado exitosamente.");
									Events.echoEvent("onClose",
											wdwConsultaVenta, null);
								} else {
									showErrorMessage(nombreOperacion,
											"No fue posible anular el documento.");
								}
							} else {
								showErrorMessage(nombreOperacion,
										"El documento que desea anular se encuentra con periodo cerrado.");
							}

						} finally {
							Utils.closeConnection(conn);
						}

					} else if (txtEstado.getText().trim()
							.equalsIgnoreCase(EstadosDocumento.ANULADO.name())) {
						showErrorMessage(nombreOperacion,
								"El documento ya se encuentra anulado.");
					} else {
						showErrorMessage(nombreOperacion,
								"Estado inválido para anulación.");
					}
				} else {
					showErrorMessage(nombreOperacion,
							"Estado inválido para anulación.");
				}
			}
		}
	};

	public void onClick$btnAnular() throws NamingException, SQLException {
		showConfirmMessage(nombreOperacion, "¿Seguro de anular el documento?",
				event);
	}

	private void showBotonAnular(boolean hide) {
		btnAnular.setVisible(hide);
	}

	private void cargarMontos() {
		double subtotal = 0;
		double descuento = 0;
		double total = 0;
		log.debug("cargando montos...");
		NumberFormat formatter = new DecimalFormat("#,##0.00");

		for (DocumentoSalDetalleDTO det : items) {
			subtotal += det.getDoc_monto_gravado();
			descuento += det.getDoc_monto_descuento();
			total += det.getDoc_monto_total();
		}

		lblSubTotal.setValue(formatter.format(subtotal));
		lblDescuento.setValue(formatter.format(descuento));
		lblTotal.setValue(formatter.format(total));

	}

}
