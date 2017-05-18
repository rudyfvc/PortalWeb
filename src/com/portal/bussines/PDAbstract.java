package com.portal.bussines;

import com.portal.utils.ObjectDataBase;

public abstract class PDAbstract extends ObjectDataBase {

	protected String usuario;
	protected static final String FORMATO_FECHA = "DD-MM-YYYY";

	public PDAbstract(String usuario) {
		this.usuario = usuario;
	}

	public PDAbstract() {
		
	}

	public static final int ESTADO_ACTIVO = 1;
	public static final int ESTADO_INACTIVO = 0;

}
