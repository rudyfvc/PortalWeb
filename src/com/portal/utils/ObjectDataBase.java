package com.portal.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

/**
 * 
 * @author
 * 
 *         Esta clase debe heredarse por las clases que requieran ejecutar dml´s
 *         en la base de datos.
 *
 */
public abstract class ObjectDataBase {

	protected Logger log;

	protected QueryRunner qr;

	public ObjectDataBase() {
		this.log = Logger.getLogger(getClass());
		this.qr = new QueryRunner();
	}

	/**
	 * 
	 * <p>
	 * Este método permite ejecutar dml´s en la base de datos.
	 * </p>
	 * 
	 * @param conn
	 *            <p>
	 *            Instancia de la coneccion de la base de datos en la cual se
	 *            ejecutara el dml
	 *            </p>
	 * @param sql
	 *            <p>
	 *            Cadena de caracteres que contiene el dml a ejecutar en la base
	 *            de datos.
	 *            </p>
	 * @param params
	 *            <p>
	 *            Arreglo de objetos en el cual se agregan los parametros que
	 *            haran match con cada query parameter.
	 *            </p>
	 * @return int
	 *         <p>
	 *         Retorna un valor int, el cual contiene la cantidad de filas
	 *         afectadas por el dml ejecutado.
	 *         </p>
	 * @throws SQLException
	 */
	protected int dml(Connection conn, String sql, Object[] params)
			throws SQLException {
		log.debug("ejecución dml sobre database.");
		log.debug("SQL " + sql + ", params " + Arrays.toString(params));
		return qr.update(conn, sql, params);
	}

	protected long count(Connection conn, String sql, Object[] params)
			throws SQLException {

		log.debug("ejecución count sobre database.");
		long count = 0;

		count = (long) qr.query(conn, sql, new ScalarHandler<Object>(), params);

		return count;

	}

	/**
	 * 
	 * <p>
	 * Este método permite crear la instancia de un DTO
	 * </p>
	 * 
	 * @param conn
	 *            <p>
	 *            </p>
	 * @param sql
	 *            <p>
	 *            </p>
	 * @param cl
	 *            <p>
	 *            </p>
	 * @param params
	 *            <p>
	 *            </p>
	 * @return <p>
	 *         </p>
	 * @throws SQLException
	 */
	protected <T> T consultaDTO(Connection conn, String sql, Class<T> cl,
			Object[] params) throws SQLException {
		log.debug("SQL " + sql + ", params " + Arrays.toString(params));
		return qr.query(conn, sql, new BeanHandler<T>(cl), params);

	}

	/**
	 * 
	 * <p>
	 * Este metodo crea la instancia de una lista a partir de los los registos
	 * obtenidos por medio de la consulta ejecutada en la base de datos.
	 * </p>
	 * 
	 * @param conn
	 *            <p>
	 *            Instancia de la conexion de la base de datos hacia donde se
	 *            ejecutara la consulta.
	 *            </p>
	 * @param sql
	 *            <p>
	 *            Instancia de la cadena de caracteres que contienen la
	 *            instrucción para la obtención de la data requerida.
	 *            </p>
	 * @param cl
	 *            <p>
	 *            Tipo de clase de la cual se creara la instancia de la lista
	 *            que se retorna como parametro.
	 *            </p>
	 * @param params
	 *            <p>
	 *            Instancia del arreglo de objetos que haran match con los query
	 *            parameters de la consulta.
	 *            </p>
	 * @return {@link List}
	 *         <p>
	 *         Retorna la lista de objetos a partir del tipo de clase enviada
	 *         como parametro.
	 *         </p>
	 * @throws SQLException
	 */
	protected <T> List<T> consultaLista(Connection conn, String sql,
			Class<T> cl, Object[] params) throws SQLException {
		return this
				.consultaLista(conn, sql, new BeanListHandler<T>(cl), params);
	}

	private <T> T consulta(Connection conn, String sql,
			ResultSetHandler<T> rsh, Object[] params) throws SQLException {
		log.debug("SQL " + sql + ", params " + Arrays.toString(params));
		return qr.query(conn, sql, rsh, params);
	}

	private <T> List<T> consultaLista(Connection conn, String sql,
			BeanListHandler<T> blh, Object[] params) throws SQLException {

		List<T> lista = this.consulta(conn, sql, blh, params);

		if (lista == null)
			lista = new ArrayList<T>();

		return lista;
	}

	/**
	 * 
	 * <p>
	 * Metodo utilizado para obtener valores escalares desde la base de datos.
	 * </p>
	 * 
	 * @param conn
	 * @param sql
	 * @param cl
	 * @return
	 * @throws SQLException
	 */
	protected <T> T consultaEscalar(Connection conn, String sql, Class<T> cl,
			Object[] params) throws SQLException {
		return this.consulta(conn, sql, new ScalarHandler<T>(), params);
	}

	/**
	 * 
	 * <p>
	 * Este metodo crea la instancia de una lista de valores escalares.
	 * </p>
	 * 
	 * @param conn
	 *            <p>
	 *            Instancia de la conexion de la base de datos hacia donde se
	 *            ejecutara la consulta.
	 *            </p>
	 * @param sql
	 *            <p>
	 *            Instancia de la cadena de caracteres que contienen la
	 *            instrucción para la obtención de la data requerida.
	 *            </p>
	 * @param cl
	 *            <p>
	 *            Tipo de clase de la cual se creara la instancia de la lista
	 *            que se retorna como parametro.
	 *            </p>
	 * @param params
	 *            <p>
	 *            Instancia del arreglo de objetos que haran match con los query
	 *            parameters de la consulta.
	 *            </p>
	 * @return
	 * @throws SQLException
	 */
	protected <T> List<T> consultaListaEscalar(Connection conn, String sql,
			Class<T> cl, Object[] params) throws SQLException {
		return this.consulta(conn, sql, new ColumnListHandler<T>(), params);
	}

}
