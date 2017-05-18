package com.portal.dto;

public class ProductoDTO {

	private String cod_producto;
	private String producto;
	private String cod_tipo_producto;
	private String tipo_producto;
	private String cod_unidad_medida;
	private String unidad_medida;
	private String cod_marca;
	private String marca;
	private String estado;
	private Double precio;
	private Double desc_porcentaje;

	public String getCod_producto() {
		return cod_producto;
	}

	public void setCod_producto(String cod_producto) {
		this.cod_producto = cod_producto;
	}

	public String getProducto() {
		return producto;
	}

	public void setProducto(String producto) {
		this.producto = producto;
	}

	public String getCod_tipo_producto() {
		return cod_tipo_producto;
	}

	public void setCod_tipo_producto(String cod_tipo_producto) {
		this.cod_tipo_producto = cod_tipo_producto;
	}

	public String getTipo_producto() {
		return tipo_producto;
	}

	public void setTipo_producto(String tipo_producto) {
		this.tipo_producto = tipo_producto;
	}

	public String getCod_unidad_medida() {
		return cod_unidad_medida;
	}

	public void setCod_unidad_medida(String cod_unidad_medida) {
		this.cod_unidad_medida = cod_unidad_medida;
	}

	public String getUnidad_medida() {
		return unidad_medida;
	}

	public void setUnidad_medida(String unidad_medida) {
		this.unidad_medida = unidad_medida;
	}

	public String getCod_marca() {
		return cod_marca;
	}

	public void setCod_marca(String cod_marca) {
		this.cod_marca = cod_marca;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getEstado() {
		if (estado != null) {
			if (estado.equalsIgnoreCase("1")) {
				return "ACTIVO";
			} else {
				return "INACTIVO";
			}
		} else {
			return "INACTIVO";
		}

	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}

	public Double getDesc_porcentaje() {
		return desc_porcentaje;
	}

	public void setDesc_porcentaje(Double desc_porcentaje) {
		this.desc_porcentaje = desc_porcentaje;
	}
	
	

}
