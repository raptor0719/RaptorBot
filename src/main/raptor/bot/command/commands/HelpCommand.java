package raptor.bot.command.commands;

import raptor.bot.command.BotMethod;

public class HelpCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.HELP.getWord();
	private final String command;

	public HelpCommand(final String command) {
		super(COMMAND_WORD);
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}
}
