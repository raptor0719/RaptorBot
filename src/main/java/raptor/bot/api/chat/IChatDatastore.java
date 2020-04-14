package raptor.bot.api.chat;

import raptor.bot.irc.ChatMessage;

public interface IChatDatastore {
	void storeMessage(String channel, String user, String message, long timestamp);
	int getTotalMessageCount();
	ChatMessage getMessage(int index);
	int getMessageCountForUser(String user);
}
