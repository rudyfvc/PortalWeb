package com.portal.control.inventario;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

import com.portal.base.ComposerBase;
import com.portal.bussines.PDKardex;
import com.portal.dto.InventarioDisponibleDTO;
import com.portal.dto.KardexDTO;
import com.portal.utils.Utils;

public class WdwKardex extends ComposerBase {
	private static final long serialVersionUID = 1L;

	private Long codEmpresa;
	private String codPeriodo;
	private Long codProducto;

	private Textbox txtEmpresa, txtPeriodo, txtCodProducto, txtProducto,
			txtTipoProducto, txtUnidadMedida;
	private Listbox ltbKardex;

	private PDKardex objKardex;

	public WdwKardex() {
		log = Logger.getLogger(getClass());
		nombreOperacion = "Movimiento de Producto";
	}

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		codPeriodo = (String) arg.get("COD_PERIODO");
		codEmpresa = Long.parseLong(arg.get("COD_EMPRESA").toString());
		codProducto = Long.parseLong(arg.get("COD_PRODUCTO").toString());
		cargarInfo();
	}

	private void cargarInfo() throws NamingException, SQLException {
		InventarioDisponibleDTO inv = (InventarioDisponibleDTO) arg
				.get(KEY_ARG_DTO);
		String empresa = (String) arg.get("EMPRESA");

		txtEmpresa.setText(empresa);
		txtPeriodo.setText(codPeriodo);

		txtCodProducto.setText(inv.getCod_producto().toString());
		txtProducto.setText(inv.getProducto() + " (" + inv.getMarca() + ")");
		txtTipoProducto.setText(inv.getTipo_producto());
		txtUnidadMedida.setText(inv.getUnidad_medida());
		cargarKardex();
	}

	private void cargarKardex() throws NamingException, SQLException {
		ltbKardex.getItems().clear();
		Connection conn = null;

		List<KardexDTO> kardex = new ArrayList<KardexDTO>();
		List<KardexDTO> depurado = new ArrayList<KardexDTO>();

		objKardex = new PDKardex();

		try {
			conn = Utils.getConnection();
			KardexDTO inicial = objKardex.getInvInicial(conn, codEmpresa,
					codPeriodo, codProducto);

			kardex.add(inicial);

			List<KardexDTO> entradas = objKardex.getEntradas(conn, codEmpresa,
					codPeriodo, codProducto);
			List<KardexDTO> salidas = objKardex.getSalidas(conn, codEmpresa,
					codPeriodo, codProducto);
			List<KardexDTO> anulacionesS = objKardex.getAnulacionesSalidas(
					conn, codEmpresa, codPeriodo, codProducto);
			List<KardexDTO> anulacionesE = objKardex.getAnulacionesEntradas(
					conn, codEmpresa, codPeriodo, codProducto);

			kardex.addAll(entradas);
			kardex.addAll(salidas);
			kardex.addAll(anulacionesS);
			kardex.addAll(anulacionesE);

			Collections.sort(kardex);

			if (kardex.size() > 0) {
				for (int i = 0; i < kardex.size(); i++) {
					KardexDTO item = kardex.get(i);
					if (i == 0) {
						depurado.add(item);
					} else {
						KardexDTO itemAnterior = kardex.get(i - 1);
						item.setInv_inicial(itemAnterior.getInv_final());
						item.setInv_final(item.getInv_inicial()
								+ item.getInv_entrada() - item.getInv_salida());
						depurado.add(item);
					}
				}
			}

			Collections.sort(depurado);

			ltbKardex.setModel(new ListModelList<KardexDTO>(depurado));

		} finally {
			Utils.closeConnection(conn);
		}

	}
}
