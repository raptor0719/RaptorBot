package raptor.bot.chatter.processors;

public class ConcreteTimedMessageBotProcessor extends TimedMessageBotProcessor {
	private final String message;

	public ConcreteTimedMessageBotProcessor(final String message, int interval) {
		super(interval);
		this.message = message;
	}

	public ConcreteTimedMessageBotProcessor(final String message, int min, int range) {
		super(min, range);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
