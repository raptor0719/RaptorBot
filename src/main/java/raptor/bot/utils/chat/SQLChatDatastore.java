package raptor.bot.utils.chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import raptor.bot.api.chat.IChatDatastore;

public class SQLChatDatastore implements IChatDatastore {
	private static final String COLUMNS = "Channel, Chatter, ChatMessage, MessageTimestamp";

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	private final String connectionUrl;
	private final String schemaName;
	private final String tableName;

	public SQLChatDatastore(final String connectionUrl, final String schemaName, final String tableName) {
		this.connectionUrl = connectionUrl;
		this.schemaName = schemaName;
		this.tableName = tableName;
	}

	@Override
	public void storeMessage(final String channel, final String user, final String message, final long timestamp) {
		try {
			final Connection connection = DriverManager.getConnection(connectionUrl);
			final Statement statement = connection.createStatement();
			final String query = getStoreMessageStatement(channel, user, message, timestamp);
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getTotalMessageCount() {
		try {
			final Connection connection = DriverManager.getConnection(connectionUrl);
			final Statement statement = connection.createStatement();
			final String query = getTotalMessageCountStatement();
			final ResultSet result = statement.executeQuery(query);
			result.next();
			return result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	private String getStoreMessageStatement(final String channel, final String user, final String message, final long timestamp) {
		return "INSERT INTO " + schemaName + "." + tableName + " (" + COLUMNS + ") VALUES (" +
				quotes(channel) + ", " + quotes(user) + ", " + quotes(escapeSingleQuote(message)) + ", " + quotes(sdf.format(new Date(timestamp))) + ");";
	}

	private String getTotalMessageCountStatement() {
		return "SELECT COUNT(*) FROM " + schemaName + "." + tableName + ";";
	}

	private String quotes(final String s) {
		return "\'" + s + "\'";
	}

	private String escapeSingleQuote(final String s) {
		return s.replaceAll("'", "''");
	}
}
