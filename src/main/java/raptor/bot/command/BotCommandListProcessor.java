package raptor.bot.command;

import java.util.List;

import raptor.bot.api.IBotProcessor;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.irc.ChatMessage;

public class BotCommandListProcessor implements IBotProcessor<ChatMessage> {
	private final List<IBotProcessor<ChatMessage>> processors;

	public BotCommandListProcessor(final List<IBotProcessor<ChatMessage>> processors) {
		this.processors = processors;
	}

	@Override
	public boolean process(final ChatMessage message, final IMessageSender<String> sender) {
		if (message == null)
			return false;

		final String input = message.getMessage();
		if ("".equals(input.trim()) || !input.startsWith("!") || input.length() < 2)
			return false;

		boolean wasProcessed = false;
		for (final IBotProcessor<ChatMessage> command : processors) {
			wasProcessed = command.process(message, sender);
			if (wasProcessed)
				break;
		}

		return wasProcessed;
	}

}
