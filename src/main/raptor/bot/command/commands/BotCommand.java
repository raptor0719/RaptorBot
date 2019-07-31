package raptor.bot.command.commands;

public class BotCommand {
	public static final String SOUND_COMMAND_STRING = "sound";

	private final String command;

	public BotCommand(final String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}
}
