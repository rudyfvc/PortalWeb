package com.portal.dto;

public class AjusteDetalleDTO {

	private Long cod_movimiento;
	private Long cod_producto;
	private String producto;
	private Long cantidad;
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
	public String getProducto() {
		return producto;
	}
	public void setProducto(String producto) {
		this.producto = producto;
	}
	public Long getCantidad() {
		return cantidad;
	}
	public void setCantidad(Long cantidad) {
		this.cantidad = cantidad;
	}
	
	
}
