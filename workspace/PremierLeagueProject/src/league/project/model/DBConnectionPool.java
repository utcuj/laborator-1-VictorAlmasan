package league.project.model;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public final class DBConnectionPool {

	private static DBConnectionPool datasource;
	private ComboPooledDataSource cpds;
	private static final String driver = "com.mysql.jdbc.Driver";
	private static final String jdbcUrl = "jdbc:mysql://localhost/league?useSSL=false&noAccessToProcedureBodies=true";

	private static String username;
	private static String password;

	private DBConnectionPool(String username, String password) throws IOException, SQLException, PropertyVetoException {
		cpds = new ComboPooledDataSource();
		setComboPooledDataSource(cpds);
		cpds.setUser(username);
		cpds.setPassword(password);
		setUsername(username);
		setPassword(password);

	}

	private DBConnectionPool() throws IOException, SQLException, PropertyVetoException {
		cpds = new ComboPooledDataSource();
		setComboPooledDataSource(cpds);
		cpds.setUser(username);
		cpds.setPassword(password);
	}

	public static DBConnectionPool getInstance() throws IOException, SQLException, PropertyVetoException {
		if (datasource == null) {
			datasource = new DBConnectionPool();
			return datasource;
		} else {

			return datasource;
		}
	}

	public static DBConnectionPool getInstance(String username, String password)
			throws IOException, SQLException, PropertyVetoException {
		datasource = null;
		datasource = new DBConnectionPool(username, password);
		return datasource;
	}

	public Connection getConnection() throws SQLException {
		return this.cpds.getConnection();
	}

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String username) {
		DBConnectionPool.username = username;
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		DBConnectionPool.password = password;
	}

	public static void main(String[] args) throws Exception {
		getInstance("admin", "admin").getConnection();
	}

	private static void setComboPooledDataSource(ComboPooledDataSource cpds) throws PropertyVetoException {
		cpds.setDriverClass(driver); // loads the jdbc driver
		cpds.setJdbcUrl(jdbcUrl);
		cpds.setMinPoolSize(5);
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);
		cpds.setMaxStatements(200);
		cpds.setAcquireRetryAttempts(5);
		cpds.setAcquireRetryDelay(500);
	}
}
