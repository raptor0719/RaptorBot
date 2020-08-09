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
		this.message = message;
		this.interval = interval;
		this.isRandom = false;
		this.min = -1;
		this.range = -1;
		setTimer(getTimeUntilNextMessage());
	}

	public TimedMessageBotProcessor(final String message, final int min, final int range) {
		this.message = message;
		this.interval = -1;
		this.isRandom = true;
		this.min = min;
		this.range = range;
		setTimer(getTimeUntilNextMessage());
	}

	@Override
	protected boolean _process(final IMessageSender<String> sender) {
		if (!hasGoneOff())
			return false;
		sender.sendMessage(message);
		setTimer(getTimeUntilNextMessage());
		return true;
	}

	private long getTimeUntilNextMessage() {
		if (!isRandom)
			return interval*ONE_MINUTE;

		return (long) ((Math.random()*(range*ONE_MINUTE)) + (min*ONE_MINUTE));
	}
}
