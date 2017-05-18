package com.portal.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class KardexDTO implements Comparable<KardexDTO> {

	private Long cod_movimiento;
	private String doc_tipo;
	private String doc_serie;
	private Long doc_numero;
	private String doc_fecha;
	private String tipo_movimiento;
	private String descripcion;
	private Long inv_inicial;
	private Long inv_final;
	private Long inv_entrada;
	private Long inv_salida;

	private String inicial;
	private String entrada;
	private String salida;
	private String existencia;

	public Long getCod_movimiento() {
		return cod_movimiento;
	}

	public void setCod_movimiento(Long cod_movimiento) {
		this.cod_movimiento = cod_movimiento;
	}

	public void setInicial(String inicial) {
		this.inicial = inicial;
	}

	public void setEntrada(String entrada) {
		this.entrada = entrada;
	}

	public void setSalida(String salida) {
		this.salida = salida;
	}

	public void setExistencia(String existencia) {
		this.existencia = existencia;
	}

	public String getDoc_fecha() {
		return doc_fecha;
	}

	public void setDoc_fecha(String doc_fecha) {
		this.doc_fecha = doc_fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getInv_inicial() {
		return inv_inicial;
	}

	public void setInv_inicial(Long inv_inicial) {
		this.inv_inicial = inv_inicial;
	}

	public Long getInv_final() {
		return inv_final;
	}

	public void setInv_final(Long inv_final) {
		this.inv_final = inv_final;
	}

	public Long getInv_entrada() {
		return inv_entrada;
	}

	public void setInv_entrada(Long inv_entrada) {
		this.inv_entrada = inv_entrada;
	}

	public Long getInv_salida() {
		return inv_salida;
	}

	public void setInv_salida(Long inv_salida) {
		this.inv_salida = inv_salida;
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

	public Long getDoc_numero() {
		return doc_numero;
	}

	public void setDoc_numero(Long doc_numero) {
		this.doc_numero = doc_numero;
	}

	public String getInicial() {
		inicial = " ";
		return inicial;
	}

	public String getEntrada() {
		if (inv_entrada == 0) {
			entrada = "";
		} else {
			entrada = inv_entrada.toString();
		}

		return entrada;
	}

	public String getSalida() {

		if (inv_salida == 0) {
			salida = "";
		} else {
			salida = inv_salida.toString();
		}

		return salida;
	}

	public String getExistencia() {

		existencia = inv_final.toString();

		return existencia;
	}
	
	

	public String getTipo_movimiento() {
		return tipo_movimiento;
	}

	public void setTipo_movimiento(String tipo_movimiento) {
		this.tipo_movimiento = tipo_movimiento;
	}

	@Override
	public int compareTo(KardexDTO o) {
		Date fecha1 = null;
		Date fecha2 = null;

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

		try {
			cal.setTime(formatter.parse(this.doc_fecha));
			fecha1 = cal.getTime();
			cal.setTime(formatter.parse(o.doc_fecha));
			fecha2 = cal.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return fecha1.compareTo(fecha2);
	}
}
