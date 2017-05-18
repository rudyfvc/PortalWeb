package com.portal.dto;

public class ResolucionDTO {

	private Long cod_resolucion;
	private Long cod_empresa;
	private String doc_tipo;
	private String doc_serie;
	private Long doc_numero_inicial;
	private Long doc_numero_final;
	private Long doc_numero_actual;
	private String fecha_resolucion;
	private String resolucion;
	private String estado;

	public Long getCod_resolucion() {
		return cod_resolucion;
	}

	public void setCod_resolucion(Long cod_resolucion) {
		this.cod_resolucion = cod_resolucion;
	}

	public Long getCod_empresa() {
		return cod_empresa;
	}

	public void setCod_empresa(Long cod_empresa) {
		this.cod_empresa = cod_empresa;
	}

	public String getDoc_tipo() {
		return doc_tipo;
	}

	public void setDoc_tipo(String doc_tipo) {
		this.doc_tipo = doc_tipo;
	}

	public String getDoc_serie() {
		return doc_serie;
	}

	public void setDoc_serie(String doc_serie) {
		this.doc_serie = doc_serie;
	}

	public Long getDoc_numero_inicial() {
		return doc_numero_inicial;
	}

	public void setDoc_numero_inicial(Long doc_numero_inicial) {
		this.doc_numero_inicial = doc_numero_inicial;
	}

	public Long getDoc_numero_final() {
		return doc_numero_final;
	}

	public void setDoc_numero_final(Long doc_numero_final) {
		this.doc_numero_final = doc_numero_final;
	}

	public Long getDoc_numero_actual() {
		return doc_numero_actual;
	}

	public void setDoc_numero_actual(Long doc_numero_actual) {
		this.doc_numero_actual = doc_numero_actual;
	}

	public String getFecha_resolucion() {
		return fecha_resolucion;
	}

	public void setFecha_resolucion(String fecha_resolucion) {
		this.fecha_resolucion = fecha_resolucion;
	}

	public String getResolucion() {
		return resolucion;
	}

	public void setResolucion(String resolucion) {
		this.resolucion = resolucion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
