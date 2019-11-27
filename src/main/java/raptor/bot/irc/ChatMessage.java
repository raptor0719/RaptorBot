package raptor.bot.irc;

public class ChatMessage {
	private final String channel;
	private final String user;
	private final String message;

	public ChatMessage(final String channel, final String user, final String message) {
		this.channel = channel;
		this.user = user;
		this.message = message;
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
}
