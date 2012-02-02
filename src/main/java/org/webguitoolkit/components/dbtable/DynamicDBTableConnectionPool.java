package org.webguitoolkit.components.dbtable;


import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface that provides a common method for the DynamicDBTableView to receive the database-connection.
 * An implementation could look like this:
 * 
 * <pre>
 * {@code
 * getConnection() {
 * try {
 * 			Context ctx = new InitialContext();
 *			dataSource = (DataSource)ctx.lookup("java:/comp/env/jdbc/wam"));
 *			return dataSource.getConnection();
 *		}
 *		catch (Exception e) {
 *			logger.error("cant load db datasource", e);
 *		}
 * }
 * }
 * </pre>
 * 
 * @author schweizerg
 */
public interface DynamicDBTableConnectionPool {

	public Connection getConnection() throws SQLException;
}
