package com.portal.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuDTO {

	private String cod_aplicacion_opcion;
	private String cod_aplicacion_opcion_padre;
	private String opc_nombre;
	private List<MenuDTO> hijos;

	public MenuDTO(String id, String padre, String nombre) {
		this.cod_aplicacion_opcion = id;
		this.cod_aplicacion_opcion_padre = padre;
		this.opc_nombre = nombre;
		this.hijos = new ArrayList<MenuDTO>();
	}

	public String getCod_aplicacion_opcion() {
		return cod_aplicacion_opcion;
	}

	public void setCod_aplicacion_opcion(String cod_aplicacion_opcion) {
		this.cod_aplicacion_opcion = cod_aplicacion_opcion;
	}

	public String getCod_aplicacion_opcion_padre() {
		return cod_aplicacion_opcion_padre;
	}

	public void setCod_aplicacion_opcion_padre(
			String cod_aplicacion_opcion_padre) {
		this.cod_aplicacion_opcion_padre = cod_aplicacion_opcion_padre;
	}

	public String getOpc_nombre() {
		return opc_nombre;
	}

	public void setOpc_nombre(String opc_nombre) {
		this.opc_nombre = opc_nombre;
	}

	public List<MenuDTO> getHijos() {
		return hijos;
	}

	public void setHijos(List<MenuDTO> hijos) {
		this.hijos = hijos;
	}

	public void addChildrenItem(MenuDTO childrenItem) {
		if (!this.hijos.contains(childrenItem))
			this.hijos.add(childrenItem);
	}

	public String toString() {

		StringBuilder sb = new StringBuilder(" Item: ");
		sb.append(" id_padre: " + this.getCod_aplicacion_opcion_padre());
		sb.append(" id: " + this.getCod_aplicacion_opcion());
		sb.append(" nombre: " + this.getOpc_nombre());
		sb.append(" hijos: " + hijos);

		return sb.toString();
	}

}
