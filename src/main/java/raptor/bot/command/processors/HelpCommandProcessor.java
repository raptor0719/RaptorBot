package raptor.bot.command.processors;

import java.util.List;

import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.irc.ChatMessage;

public class HelpCommandProcessor extends BotCommandProcessor {
	final List<BotCommandProcessor> processors;

	public HelpCommandProcessor(final List<BotCommandProcessor> processors) {
		super(BotMethod.HELP.getWord(), "Use '!help <command>' for help for a specific command. " + buildCommandList(processors));
		this.processors = processors;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.HELP.parse(input);

		if (!(command instanceof HelpCommand)) {
			System.err.println("Expected instanceof HelpCommand.");
			return false;
		}

		final HelpCommand helpCommand = (HelpCommand)command;
		final String helpQuery = ((helpCommand.getCommand() == null || helpCommand.getCommand().isEmpty()) ? "help" : helpCommand.getCommand()) + " help";

		boolean wasProcessed = false;
		for (final BotCommandProcessor p : processors)
			if (p.process(message.setNewMessage(helpQuery), sender)) {
				wasProcessed = true;
				break;
			}

		if (!wasProcessed)
			sender.sendMessage("Unknown command given for help.");

		return wasProcessed;
	}

	private static String buildCommandList(final List<BotCommandProcessor> processors) {
		String commandList = "The following is a list of commands: ";
		for (final BotCommandProcessor p : processors)
			commandList += p.getCommandWord() + ", ";
		return commandList.substring(0, commandList.length() - 2) + ".";
	}
}
