package raptor.bot.utils.chat;

import java.util.Iterator;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.irc.ChatMessage;

public class NoOpChatDatastore implements IChatDatastore {
	@Override
	public int getTotalMessageCount() {
		/* Return the signal that the datastore in unavailable */
		return -1;
	}

	@Override
	public void storeMessage(String channel, String user, String message, long timestamp) {
		/* No-op */
	}

	@Override
	public ChatMessage getMessage(final int index) {
		/* Return the signal that this message does not exist */
		return null;
	}

	@Override
	public int getMessageCountForUser(String user) {
		/* Return the signal that the datastore in unavailable */
		return -1;
	}

	@Override
	public Iterator<ChatMessage> getMessagesInRange(int start, int end) {
		/* Return the signal that these messages do not exist */
		return null;
	}

	@Override
	public Iterator<ChatMessage> getLastMessages(int length) {
		/* Return the signal that these messages do not exist */
		return null;
	}
}
