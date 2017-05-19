package com.portal.enums;

public enum TiposMovimiento {

	COMPRA(1), VENTA(2), AJUSTE(3);

	public final int CODIGO;

	TiposMovimiento(int tipo) {
		this.CODIGO = tipo;
	}

}
