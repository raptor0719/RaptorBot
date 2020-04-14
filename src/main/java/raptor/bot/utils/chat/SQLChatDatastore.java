package raptor.bot.utils.chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.irc.ChatMessage;

public class SQLChatDatastore implements IChatDatastore {
	private static final String CHANNEL_COLUMN = "Channel";
	private static final String USER_COLUMN = "Chatter";
	private static final String MESSAGE_COLUMN = "ChatMessage";
	private static final String TIMESTAMP_COLUMN = "MessageTimestamp";
	private static final String COLUMNS = CHANNEL_COLUMN + ", " + USER_COLUMN + ", " + MESSAGE_COLUMN + ", " + TIMESTAMP_COLUMN;

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

	@Override
	public ChatMessage getMessage(final int index) {
		try {
			final Connection connection = DriverManager.getConnection(connectionUrl);
			final Statement statement = connection.createStatement();
			final String query = getMessageByIndexStatement(index);
			final ResultSet result = statement.executeQuery(query);
			result.next();
			return new ChatMessage(result.getString(CHANNEL_COLUMN), result.getString(USER_COLUMN), result.getString(MESSAGE_COLUMN), result.getTimestamp(TIMESTAMP_COLUMN).getTime());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int getMessageCountForUser(final String user) {
		try {
			final Connection connection = DriverManager.getConnection(connectionUrl);
			final Statement statement = connection.createStatement();
			final String query = getUserMessageCountStatement(user);
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

	private String getMessageByIndexStatement(final int index) {
		return "SELECT " + COLUMNS + " FROM(" +
				"SELECT ROW_NUMBER() OVER(ORDER BY " + TIMESTAMP_COLUMN + ") AS RowNum, " + COLUMNS + " FROM " + schemaName + "." + tableName +
				") AS ORDERED WHERE RowNum = " + index;
	}

	private String getUserMessageCountStatement(final String user) {
		return "SELECT COUNT(*) FROM " + schemaName + "." + tableName + " WHERE " + USER_COLUMN + "=" + quotes(user) + ";";
	}

	private String quotes(final String s) {
		return "\'" + s + "\'";
	}

	private String escapeSingleQuote(final String s) {
		return s.replaceAll("'", "''");
	}
}
