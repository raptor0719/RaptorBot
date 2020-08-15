package raptor.bot.utils.chat;

import java.util.Iterator;

import org.junit.Test;

import raptor.bot.irc.ChatMessage;

public class SQLChatDatastoreTest {
	@Test
	public void testChatDatastore() {
		final String connectionUrl = "jdbc:sqlserver://192.168.1.6:1280;databaseName=master;user=raptorbot;password=raptorbot";
		final String schema = "dbo";
		final String table = "ChatLog";

		final SQLChatDatastore datastore = new SQLChatDatastore(connectionUrl, schema, table);

		final Iterator<ChatMessage> lastMessages = datastore.getLastMessages(30);

		while (lastMessages.hasNext())
			System.out.println(lastMessages.next().getMessage());
	}
}
