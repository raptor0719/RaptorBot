package raptor.bot.chatter.mimic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.irc.ChatMessage;
import raptor.bot.utils.chat.SQLChatDatastore;

public class ChatMimicTest {

	@Test
	public void testChatMimic() {
		final IChatDatastore datastore = new InMemoryChatDatastore();
		storeTestMessage("I love the outdoors", datastore);
		storeTestMessage("outdoors LUL", datastore);
		storeTestMessage("LUL", datastore);
		storeTestMessage("I hate outdoors", datastore);
		storeTestMessage("I didnt ask you", datastore);
		storeTestMessage("love is a bit silly", datastore);
		storeTestMessage("outdoors is silly LUL", datastore);
		storeTestMessage("silly is a strong word", datastore);

		final ChatMimic mimic = new ChatMimic(ChatMimicDictionary.compile(datastore));

		final List<String> proximityLines = new ArrayList<String>();
		proximityLines.add("LUL");

		System.out.println(mimic.mimic(proximityLines));
	}

	@Test
	public void megatestChatMimic() {
		final IChatDatastore datastore = new SQLChatDatastore("jdbc:sqlserver://192.168.1.6:1280;databaseName=master;user=raptorbot;password=raptorbot", "dbo", "ChatLog");
		final ChatMimicDictionary dictionary = ChatMimicDictionary.compile(datastore);
		System.out.println("Finished compiling...");
		final ChatMimic mimic = new ChatMimic(dictionary);

		System.out.println("Getting proximity lines...");
		final List<String> proximityLines = new ArrayList<>();
		final int totalCount = datastore.getTotalMessageCount();

		for (int i = 0; i < ChatMimicDictionary.PROXIMITY_LINE_COUNT; i++) {
			final String msg = datastore.getMessage(totalCount - 1 - i).getMessage();
			System.out.println(msg);
			proximityLines.add(msg);
		}

		System.out.println("Mimicing...");
		String result = "";
		for (int i = 0; i < 50; i++)
			result = result + mimic.mimic(proximityLines) + "\n";
		System.out.println(result);
	}

	private void storeTestMessage(final String message, final IChatDatastore datastore) {
		datastore.storeMessage("test", "testUser", message, 0L);
	}

	private static class InMemoryChatDatastore implements IChatDatastore {
		final List<ChatMessage> storedMessages;

		public InMemoryChatDatastore(final List<ChatMessage> storedMessages) {
			this.storedMessages = storedMessages;
		}

		public InMemoryChatDatastore() {
			this(new ArrayList<>());
		}

		@Override
		public void storeMessage(String channel, String user, String message, long timestamp) {
			storedMessages.add(new ChatMessage(channel, user, message, timestamp));
		}

		@Override
		public int getTotalMessageCount() {
			return storedMessages.size();
		}

		@Override
		public ChatMessage getMessage(int index) {
			return storedMessages.get(index);
		}

		@Override
		public int getMessageCountForUser(final String user) {
			int count = 0;
			for (final ChatMessage c : storedMessages)
				if (c.getUser().equals(user))
					count++;
			return count;
		}

		@Override
		public Iterator<ChatMessage> getMessagesInRange(int start, int end) {
			return storedMessages.subList(start, end+1).iterator();
		}

		@Override
		public Iterator<ChatMessage> getLastMessages(int length) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}
