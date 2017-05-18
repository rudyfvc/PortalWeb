package com.portal.bussines;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.portal.dto.EmpresaDTO;

public class PDEmpresa extends PDAbstract {

	private static final String SQL_GET_EMPRESAS = " select * from pfd_empresa "
			+ " where cod_estado = ?  ";

	private static final String SQL_GET_ALL_EMPRESAS = " select * from pfd_empresa ";

	private static final String SQL_INSERT_EMPRESA = " insert into pfd_empresa (cod_empresa,emp_nombre,"
			+ " emp_nit, emp_direccion, emp_telefono, log_insertdate, log_insertuser,cod_estado )"
			+ " values(?,?,?,?,?,current_timestamp,?,1)";

	private static final String SQL_UPDATE_EMPRESA = " update pfd_empresa "
			+ " set emp_nombre = ?, "
			+ " emp_nit = ?, "
			+ " emp_direccion = ?, "
			+ " emp_telefono = ?, log_updatedate = current_timestamp, log_updateuser = ?  "
			+ "  where cod_empresa = ? ";

	private static final String SQL_GET_SECUENCIA = " select nextval('pfd_empresa_sq') ";

	public PDEmpresa() {
		log = Logger.getLogger(getClass());
	}

	public List<EmpresaDTO> getEmpresasActivas(Connection conn)
			throws SQLException {

		return consultaLista(conn, SQL_GET_EMPRESAS, EmpresaDTO.class,
				new Object[] { ESTADO_ACTIVO });
	}

	public List<EmpresaDTO> getAllEmpresas(Connection conn) throws SQLException {

		return consultaLista(conn, SQL_GET_ALL_EMPRESAS, EmpresaDTO.class, null);
	}

	public Long getSecuencia(Connection conn) throws SQLException {

		Long b = consultaEscalar(conn, SQL_GET_SECUENCIA, Long.class, null);

		return b;
	}

	public boolean addEmpresa(Connection conn, Long codEmpresa,
			String nombreEmpresa, String nit, String telefono,
			String direccion, String usuario) throws SQLException {

		return dml(conn, SQL_INSERT_EMPRESA, new Object[] { codEmpresa,
				nombreEmpresa, nit, direccion, telefono, usuario }) > 0;

	}

	public boolean updateEmpresa(Connection conn, String nombreEmpresa,
			String nit, String telefono, String direccion, String usuario,
			String codEmpresa) throws SQLException {

		return dml(conn, SQL_UPDATE_EMPRESA, new Object[] { nombreEmpresa, nit,
				direccion, telefono, usuario, Long.parseLong(codEmpresa) }) > 0;

	}

}
