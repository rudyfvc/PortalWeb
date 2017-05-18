package com.portal.security;

import java.util.List;

public class SecUsuario {

	

	private String user_name;
	private String user_email;
	private String user_nombres;
	private String user_apellidos;
	private String user_estado;
	private SecPerfil perfil;
	private List<SecEmpresa> empresas;

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public SecPerfil getPerfil() {
		return perfil;
	}

	public void setPerfil(SecPerfil perfil) {
		this.perfil = perfil;
	}

	public String getUser_nombres() {
		return user_nombres;
	}

	public void setUser_nombres(String user_nombres) {
		this.user_nombres = user_nombres;
	}

	public String getUser_apellidos() {
		return user_apellidos;
	}

	public void setUser_apellidos(String user_apellidos) {
		this.user_apellidos = user_apellidos;
	}

	public String getUser_estado() {
		return user_estado;
	}

	public void setUser_estado(String user_estado) {
		this.user_estado = user_estado;
	}
	
	
	public List<SecEmpresa> getEmpresas() {
		return empresas;
	}

	public void setEmpresas(List<SecEmpresa> empresas) {
		this.empresas = empresas;
	}

	public String toString(){
		return "user_name: "+this.user_name+
				"user_nombres: "+this.user_nombres+
				"user_apellidos: "+this.user_apellidos+
				"user_estado: "+this.user_estado+
				" perfil: "+this.perfil;
	}

}
