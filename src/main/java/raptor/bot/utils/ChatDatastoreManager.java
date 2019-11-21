package raptor.bot.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatDatastoreManager {
	private static final String connectionUrl = "jdbc:sqlserver://192.168.1.6:1280;databaseName=master;user=raptorbot;password=raptorbot";

	private static final String SCHEMA_NAME = "dbo";
	private static final String TABLE_NAME = "ChatLog";
	private static final String COLUMNS = "Channel, Chatter, ChatMessage, MessageTimestamp";

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	public void storeMessage(final String channel, final String user, final String message, final long timestamp) {
		try {
			final Connection connection = DriverManager.getConnection(connectionUrl);
			final Statement statement = connection.createStatement();
			final String query = getStoreMessageStatement(channel, user, message, timestamp);
			System.out.println(query);
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String getStoreMessageStatement(final String channel, final String user, final String message, final long timestamp) {
		return "INSERT INTO " + SCHEMA_NAME + "." + TABLE_NAME + " (" + COLUMNS + ") VALUES (" +
				quotes(channel) + ", " + quotes(user) + ", " + quotes(escapeSingleQuote(message)) + ", " + quotes(sdf.format(new Date(timestamp))) + ");";
	}

	private String quotes(final String s) {
		return "\'" + s + "\'";
	}

	private String escapeSingleQuote(final String s) {
		return s.replaceAll("'", "''");
	}
}
