package raptor.bot.command;

import raptor.bot.api.IBotProcessor;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.irc.ChatMessage;

public abstract class BotCommandProcessor implements IBotProcessor<ChatMessage> {
	private final String commandWord;
	private final String helpMessage;

	public BotCommandProcessor(final String commandWord, final String helpMessage) {
		this.commandWord = commandWord;
		this.helpMessage = helpMessage;
	}

	@Override
	public boolean process(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final String command = input.split(" ")[0].toLowerCase();

		if (!command.equals(commandWord.toLowerCase()))
			return false;

		final String params = (commandWord.equals(input)) ? "" : input.substring(input.indexOf(" ") + 1);

		if ("help".equals(params.trim().toLowerCase())) {
			sender.sendMessage(helpMessage);
			return true;
		}

		return doProcess(message.setNewMessage(params), sender);
	}

	public String getCommandWord() {
		return commandWord;
	}

	protected abstract boolean doProcess(final ChatMessage input, final IMessageSender<String> sender);
}
