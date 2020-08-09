package raptor.bot.chatter.processors;

import raptor.bot.api.TimedBotProcessor;
import raptor.bot.api.message.IMessageSender;

public class TimedMessageBotProcessor extends TimedBotProcessor {
	private static final int ONE_MINUTE = 60000;

	private final String message;

	private final long interval;

	private final boolean isRandom;
	private final int min;
	private final int range;

	public TimedMessageBotProcessor(final String message, final int interval) {
		super(getTimeUntilNextMessage(interval));
		this.message = message;
		this.interval = interval;
		this.isRandom = false;
		this.min = -1;
		this.range = -1;
	}

	public TimedMessageBotProcessor(final String message, final int min, final int range) {
		super(getTimeUntilNextMessage(min, range));
		this.message = message;
		this.interval = -1;
		this.isRandom = true;
		this.min = min;
		this.range = range;
	}

	@Override
	protected boolean doProcess(final IMessageSender<String> sender) {
		sender.sendMessage(message);
		return true;
	}

	@Override
	protected long getTime() {
		return (isRandom) ? getTimeUntilNextMessage(min, range) : getTimeUntilNextMessage(interval);
	}

	private static long getTimeUntilNextMessage(final int min, final int range) {
		return (long) ((Math.random()*(range*ONE_MINUTE)) + (min*ONE_MINUTE));
	}

	private static long getTimeUntilNextMessage(final long interval) {
		return interval*ONE_MINUTE;
	}
}
