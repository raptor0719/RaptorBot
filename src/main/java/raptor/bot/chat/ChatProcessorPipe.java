package raptor.bot.chat;

import java.util.Collection;

import raptor.bot.api.IChatProcessor;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.irc.ChatMessage;

public class ChatProcessorPipe implements IChatProcessor {
	private final Collection<IChatProcessor> processors;
	private final boolean stopAfterFirstSuccess;

	public ChatProcessorPipe(final Collection<IChatProcessor> processors, final boolean stopAfterFirstSuccess) {
		this.processors = processors;
		this.stopAfterFirstSuccess = stopAfterFirstSuccess;
	}

	public ChatProcessorPipe(final Collection<IChatProcessor> processors) {
		this(processors, false);
	}

	@Override
	public boolean process(final ChatMessage message, final IMessageSender<String> sender) {
		boolean wasProcessed = false;

		for (final IChatProcessor processor : processors) {
			wasProcessed = processor.process(message, sender);
			if (wasProcessed && stopAfterFirstSuccess)
				break;
		}

		return wasProcessed;
	}
}
