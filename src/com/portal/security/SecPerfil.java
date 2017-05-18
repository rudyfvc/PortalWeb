package com.portal.security;

import java.util.Arrays;
import java.util.List;

public class SecPerfil {

	private String cod_perfil;
	private String per_nombre;
	private List<SecPerfilOpcion> opcionesPerfil;

	public String getCod_perfil() {
		return cod_perfil;
	}

	public void setCod_perfil(String cod_perfil) {
		this.cod_perfil = cod_perfil;
	}

	public String getPer_nombre() {
		return per_nombre;
	}

	public void setPer_nombre(String per_nombre) {
		this.per_nombre = per_nombre;
	}

	public List<SecPerfilOpcion> getOpcionesPerfil() {
		return opcionesPerfil;
	}

	public void setOpcionesPerfil(List<SecPerfilOpcion> opcionesPerfil) {
		this.opcionesPerfil = opcionesPerfil;
	}

	public String toString() {

		return " cod_perfil: " + this.cod_perfil + "	per_nombre: "
				+ this.per_nombre + " opcionesPefil: "
				+ Arrays.toString(this.opcionesPerfil.toArray());

	}

}
