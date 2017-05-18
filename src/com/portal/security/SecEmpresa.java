package com.portal.security;

import com.portal.dto.PeriodoDTO;

public class SecEmpresa {

	private String cod_empresa;
	private String emp_nombre;
	private String cod_estado;
	private String emp_nit;
	private String emp_direccion;

	private PeriodoDTO periodo;
	

	public String getCod_empresa() {
		return cod_empresa;
	}

	public void setCod_empresa(String cod_empresa) {
		this.cod_empresa = cod_empresa;
	}

	public String getEmp_nombre() {
		return emp_nombre;
	}

	public void setEmp_nombre(String emp_nombre) {
		this.emp_nombre = emp_nombre;
	}

	public String getCod_estado() {
		return cod_estado;
	}

	public void setCod_estado(String cod_estado) {
		this.cod_estado = cod_estado;
	}

	public String getEmp_nit() {
		return emp_nit;
	}

	public void setEmp_nit(String emp_nit) {
		this.emp_nit = emp_nit;
	}

	public String getEmp_direccion() {
		return emp_direccion;
	}

	public void setEmp_direccion(String emp_direccion) {
		this.emp_direccion = emp_direccion;
	}


	public PeriodoDTO getPeriodo() {
		return periodo;
	}

	public void setPeriodo(PeriodoDTO periodo) {
		this.periodo = periodo;
	}
	
	

}
