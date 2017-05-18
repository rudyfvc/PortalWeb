package com.portal.security;

public class SecPerfilOpcion {

	private String cod_aplicacion_opcion;
	private String cod_aplicacion_opcion_padre;
	private String opc_nombre;
	private String opc_nombre_desplegar;
	private String opc_url;
	private String opc_image;

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

	public String getOpc_nombre_desplegar() {
		return opc_nombre_desplegar;
	}

	public void setOpc_nombre_desplegar(String opc_nombre_desplegar) {
		this.opc_nombre_desplegar = opc_nombre_desplegar;
	}

	public String getOpc_url() {
		return opc_url;
	}

	public void setOpc_url(String opc_url) {
		this.opc_url = opc_url;
	}

	
	
	public String getOpc_image() {
		return opc_image;
	}

	public void setOpc_image(String opc_image) {
		this.opc_image = opc_image;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" cod_aplicacion_opcion: " + cod_aplicacion_opcion);
		sb.append(" cod_aplicacion_opcion_padre: "
				+ cod_aplicacion_opcion_padre);
		sb.append(" opc_nombre: " + opc_nombre);
		sb.append(" opc_nombre_desplegar: " + opc_nombre_desplegar);
		sb.append(" opc_url: " + opc_url);

		return sb.toString();
	}
	
	

}
