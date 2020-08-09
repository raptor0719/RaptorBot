package raptor.bot.api;

import raptor.bot.api.message.IMessageSender;

public abstract class TimedBotProcessor implements IInherentBotProcessor {
	private long timerEnd;

	public TimedBotProcessor(final long initialTime) {
		setTimer(initialTime);
	}

	public void setTimer(final long millis) {
		timerEnd = System.currentTimeMillis() + millis;
	}

	public boolean hasGoneOff() {
		return System.currentTimeMillis() >= timerEnd;
	}

	@Override
	public boolean process(final IMessageSender<String> sender) {
		if (!hasGoneOff())
			return false;
		setTimer(getTime());
		return doProcess(sender);
	}

	protected abstract boolean doProcess(final IMessageSender<String> sender);
	protected abstract long getTime();
}
