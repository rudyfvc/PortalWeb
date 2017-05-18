package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.portal.dto.TipoDocumentoDTO;

public class PDTipoDocumento extends PDAbstract {

	private static final String SQL_GET_TIPOS_DOCUMENTO = "select * from pd_tipo_documento where estado =  1";

	public List<TipoDocumentoDTO> getTiposDocumento(Connection conn) throws SQLException {

		return consultaLista(conn, SQL_GET_TIPOS_DOCUMENTO,
				TipoDocumentoDTO.class, null);
	}

}
