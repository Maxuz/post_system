package tk.maxuz.blog.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCConnectionHelper {
	private static final String GET_DATABASE_DRIVER_ERROR_MSG = "Ошибка получения драйвера базы данных";
	private static final String CONNECT_TO_DATABASE_ERROR_MSG = "Ошибка соединения с базой данных";
	private static final String CONNECTION_IS_NOT_INITIALIZED_ERROR_MSG = "Connection is not initialized.";
	private static final String CLOSE_CONNECTION_ERROR_MSG = "Ошибка закрытия соединения с базой данных";

	private Connection connection;

	private final Logger LOGGER = LoggerFactory.getLogger(JDBCConnectionHelper.class);
	
	public void initialize() {
		try {
			if (connection == null) {
				Class.forName("org.postgresql.Driver");
				connection = DriverManager.getConnection("jdbc:postgresql:post_system_test", "testuser", "user");
			}
		} catch (ClassNotFoundException e) {
			LOGGER.error(GET_DATABASE_DRIVER_ERROR_MSG, e);
		} catch (SQLException e) {
			LOGGER.error(CONNECT_TO_DATABASE_ERROR_MSG, e);
		}
	}

	public Connection getConnection() {
		if (connection == null) {
			throw new RuntimeException(CONNECTION_IS_NOT_INITIALIZED_ERROR_MSG);
		}
		return connection;
	}

	public void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			LOGGER.error(CLOSE_CONNECTION_ERROR_MSG, e);
		}
	}
}
