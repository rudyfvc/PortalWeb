package com.portal.dto;

public class DocumentoEntDetalleDTO {

	private Long cod_movimiento;
	private Long cod_producto;
	private String producto;
	private Long cantidad;
	private Double doc_monto_gravado;
	private Double doc_monto_descuento;
	private Double doc_monto_iva;
	private Double doc_monto_total;
	private Double doc_monto_unitario;
	private Double doc_monto_descuento_unitario;

	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}


	public Long getCod_movimiento() {
		return cod_movimiento;
	}

	public void setCod_movimiento(Long cod_movimiento) {
		this.cod_movimiento = cod_movimiento;
	}

	public Long getCod_producto() {
		return cod_producto;
	}

	public void setCod_producto(Long cod_producto) {
		this.cod_producto = cod_producto;
	}

	public Long getCantidad() {
		return cantidad;
	}

	public void setCantidad(Long cantidad) {
		this.cantidad = cantidad;
	}

	public Double getDoc_monto_gravado() {
		return doc_monto_gravado;
	}

	public void setDoc_monto_gravado(Double doc_monto_gravado) {
		this.doc_monto_gravado = doc_monto_gravado;
	}

	public Double getDoc_monto_descuento() {
		return doc_monto_descuento;
	}

	public void setDoc_monto_descuento(Double doc_monto_descuento) {
		this.doc_monto_descuento = doc_monto_descuento;
	}

	public Double getDoc_monto_iva() {
		return doc_monto_iva;
	}

	public void setDoc_monto_iva(Double doc_monto_iva) {
		this.doc_monto_iva = doc_monto_iva;
	}

	public Double getDoc_monto_total() {
		return doc_monto_total;
	}

	public void setDoc_monto_total(Double doc_monto_total) {
		this.doc_monto_total = doc_monto_total;
	}

	public Double getDoc_monto_unitario() {
		return doc_monto_unitario;
	}

	public void setDoc_monto_unitario(Double doc_monto_unitario) {
		this.doc_monto_unitario = doc_monto_unitario;
	}

	public Double getDoc_monto_descuento_unitario() {
		return doc_monto_descuento_unitario;
	}

	public void setDoc_monto_descuento_unitario(Double doc_monto_descuento_unitario) {
		this.doc_monto_descuento_unitario = doc_monto_descuento_unitario;
	}
	
	

}
