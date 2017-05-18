package com.portal.dto;


public class MarcaDTO {

	private String cod_marca;
	private String marca;
	private String estado;
	private String log_insertdate;
	private String log_insertuser;
	private String log_updatedate;
	private String log_updateuser;

	

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
		if (estado.equalsIgnoreCase("1")) {
			return "ACTIVO";
		} else {
			return "INACTIVO";
		}

	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getLog_insertdate() {
		return log_insertdate;
	}

	public void setLog_insertdate(String log_insertdate) {
		this.log_insertdate = log_insertdate;
	}

	public String getLog_insertuser() {
		return log_insertuser;
	}

	public void setLog_insertuser(String log_insertuser) {
		this.log_insertuser = log_insertuser;
	}

	public String getLog_updatedate() {
		return log_updatedate;
	}

	public void setLog_updatedate(String log_updatedate) {
		this.log_updatedate = log_updatedate;
	}

	public String getLog_updateuser() {
		return log_updateuser;
	}

	public void setLog_updateuser(String log_updateuser) {
		this.log_updateuser = log_updateuser;
	}

}
