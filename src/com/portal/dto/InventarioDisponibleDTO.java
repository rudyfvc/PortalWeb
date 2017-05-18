package com.portal.dto;

public class InventarioDisponibleDTO {

	private Long cod_producto;
	private String producto;
	private String tipo_producto;
	private String unidad_medida;
	private String marca;
	private Long inv_existencia;
	
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
	public String getTipo_producto() {
		return tipo_producto;
	}
	public void setTipo_producto(String tipo_producto) {
		this.tipo_producto = tipo_producto;
	}
	public String getUnidad_medida() {
		return unidad_medida;
	}
	public void setUnidad_medida(String unidad_medida) {
		this.unidad_medida = unidad_medida;
	}
	public String getMarca() {
		return marca;
	}
	public void setMarca(String marca) {
		this.marca = marca;
	}
	public Long getInv_existencia() {
		return inv_existencia;
	}
	public void setInv_existencia(Long inv_existencia) {
		this.inv_existencia = inv_existencia;
	}
	
	
	
	
	
}
