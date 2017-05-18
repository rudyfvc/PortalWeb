package com.portal.enums;

public enum Estados {

	ACTIVO(1), INACTIVO(0);

	public final int CODIGO;

	Estados(int estado) {
		this.CODIGO = estado;
	}

}
