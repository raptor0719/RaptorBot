package raptor.bot.command.processors;

import java.util.List;

import raptor.bot.api.IAliasManager;
import raptor.bot.api.IBotProcessor;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.irc.ChatMessage;

public class AliasedCommandProcessor implements IBotProcessor<ChatMessage> {
	private final IAliasManager aliasManager;
	private final List<IBotProcessor<ChatMessage>> processors;

	public AliasedCommandProcessor(final IAliasManager aliasManager, final List<IBotProcessor<ChatMessage>> processors) {
		this.aliasManager = aliasManager;
		this.processors = processors;
	}

	@Override
	public boolean process(final ChatMessage input, final IMessageSender<String> sender) {
		final String message = input.getMessage();
		final String commandWord = message.substring(1).split(" ")[0];

		if (!aliasManager.isAlias(commandWord))
			return false;

		final ChatMessage updatedMessage = input.setNewMessage(aliasManager.getAliasedPhrase(commandWord) + extractParams(commandWord, message));

		for (final IBotProcessor<ChatMessage> p : processors)
			if (p.process(updatedMessage, sender))
				break;

		return true;
	}

	private String extractParams(final String commandWord, final String message) {
		return (message.length() > commandWord.length() + 1) ? " " + message.substring(message.indexOf(" ") + 1) : "";
	}
}
