package raptor.bot.utils.chat;

import raptor.bot.api.chat.IChatDatastore;

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
}
