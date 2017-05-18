package com.portal.enums;

public enum TiposMovimiento {

	COMPRA(1), VENTA(2), DEVOLUCION_PROVEEDOR(3), DEVOLUCION_BODEGA(4);

	public final int CODIGO;

	TiposMovimiento(int tipo) {
		this.CODIGO = tipo;
	}

}
