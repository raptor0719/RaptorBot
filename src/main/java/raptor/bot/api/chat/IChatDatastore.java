package raptor.bot.api.chat;

public interface IChatDatastore {
	void storeMessage(String channel, String user, String message, long timestamp);
}
