package com.portal.base;

public final class Pages {

	private static final String URL_VIEW_CONFIGURACIONES = "/view-configuraciones";
	private static final String URL_VIEW_SEGURIDAD = "/view-seguridad";
	private static final String URL_VIEW_INVENTARIO = "/view-inventario";
	private static final String URL_VIEW_CATALOGOS = "/view-catalogos";
	private static final String URL_VIEW_INGRESOS = "/view-ingresos";
	private static final String URL_VIEW_EGRESOS = "/view-egresos";

	public static final String URL_EMPRESA = URL_VIEW_CONFIGURACIONES
			+ "/empresas/empresa.zul";
	public static final String URL_PERFIL = URL_VIEW_SEGURIDAD + "/perfil.zul";
	public static final String URL_USUARIO = URL_VIEW_SEGURIDAD
			+ "/usuario.zul";

	public static final String URL_PRODUCTO = URL_VIEW_INVENTARIO
			+ "/producto.zul";
	public static final String URL_PRODUCTO_PRECIO = URL_VIEW_INVENTARIO
			+ "/precios/precio.zul";

	public static final String URL_PROVEEDOR = URL_VIEW_CATALOGOS
			+ "/proveedores/proveedor.zul";
	public static final String URL_CLIENTE = URL_VIEW_CATALOGOS
			+ "/clientes/cliente.zul";

	public static final String URL_COMPRA = URL_VIEW_INGRESOS + "/compra.zul";
	public static final String URL_CONSULTA_COMPRA = URL_VIEW_INGRESOS
			+ "/consulta-compra.zul";

	public static final String URL_KARDEX = URL_VIEW_INVENTARIO + "/kardex.zul";

	public static final String URL_VENTA = URL_VIEW_EGRESOS
			+ "/ventas/venta.zul";
	public static final String URL_CONSULTA_VENTA = URL_VIEW_EGRESOS
			+ "/ventas/consulta-venta.zul";
}
