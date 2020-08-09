package raptor.bot.api;

import raptor.bot.api.message.IMessageSender;

public abstract class TimedBotProcessor implements IInherentBotProcessor {
	private long timerEnd;

	public void setTimer(final long millis) {
		timerEnd = System.currentTimeMillis() + millis;
	}

	public boolean hasGoneOff() {
		return System.currentTimeMillis() >= timerEnd;
	}

	@Override
	public boolean process(final IMessageSender<String> sender) {
		if (hasGoneOff())
			return _process(sender);
		return false;
	}

	protected abstract boolean _process(final IMessageSender<String> sender);
}
