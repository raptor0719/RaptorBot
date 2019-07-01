package raptor.bot.irc;

public class ChatMessage {
	private final String user;
	private final String message;

	public ChatMessage(final String user, final String message) {
		this.user = user;
		this.message = message;
	}

	public String getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}
}
