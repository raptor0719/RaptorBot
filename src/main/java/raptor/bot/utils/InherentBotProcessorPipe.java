package raptor.bot.utils;

import java.util.Collection;

import raptor.bot.api.IInherentBotProcessor;
import raptor.bot.api.message.IMessageSender;

public class InherentBotProcessorPipe implements IInherentBotProcessor {
	private final Collection<IInherentBotProcessor> processors;
	private final boolean stopAtFirstSuccess;

	public InherentBotProcessorPipe(final Collection<IInherentBotProcessor> processors, final boolean stopAtFirstSuccess) {
		this.processors = processors;
		this.stopAtFirstSuccess = stopAtFirstSuccess;
	}

	public InherentBotProcessorPipe(final Collection<IInherentBotProcessor> processors) {
		this(processors, false);
	}

	@Override
	public boolean process(final IMessageSender<String> sender) {
		for (final IInherentBotProcessor p : processors) {
			final boolean wasProcessed = p.process(sender);
			if (wasProcessed && stopAtFirstSuccess)
				break;
		}
		return true;
	}
}
