package raptor.bot.api.chat;

import java.util.Iterator;

import raptor.bot.irc.ChatMessage;

public interface IChatDatastore {
	void storeMessage(String channel, String user, String message, long timestamp);
	int getTotalMessageCount();
	ChatMessage getMessage(int index);
	int getMessageCountForUser(String user);
	Iterator<ChatMessage> getMessagesInRange(int start, int end);
}
