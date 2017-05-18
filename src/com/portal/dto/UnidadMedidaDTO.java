package com.portal.dto;


public class UnidadMedidaDTO {

	private String cod_unidad_medida;
	private String unidad_medida;
	private String estado;
	private String log_insertdate;
	private String log_insertuser;
	private String log_updatedate;
	private String log_updateuser;

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
