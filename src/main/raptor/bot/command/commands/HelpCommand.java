package raptor.bot.command.commands;

public class HelpCommand extends BotCommand {
	private final String command;

	public HelpCommand(final String command) {
		super(BotCommand.HELP_COMMAND_STRING);
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}
}
