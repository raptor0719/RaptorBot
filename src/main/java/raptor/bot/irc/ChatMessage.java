package raptor.bot.irc;

public class ChatMessage {
	private final String channel;
	private final String user;
	private final String message;
	private final long timestamp;

	public ChatMessage(final String channel, final String user, final String message, final long timestamp) {
		this.channel = channel;
		this.user = user;
		this.message = message;
		this.timestamp = timestamp;
	}

	public String getChannel() {
		return channel;
	}

	public String getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}

	public long getTimestamp() {
		return timestamp;
	}
}
