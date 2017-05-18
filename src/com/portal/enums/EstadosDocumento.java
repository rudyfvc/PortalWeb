package com.portal.enums;

public enum EstadosDocumento {

	VIGENTE("V"), ANULADO("A");

	public final String ESTADO;

	EstadosDocumento(String estado) {
		this.ESTADO = estado;
	}

}
