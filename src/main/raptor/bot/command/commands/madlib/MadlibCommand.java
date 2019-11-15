package raptor.bot.command.commands.madlib;

import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;

public class MadlibCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.MADLIB.getWord();

	private final String action;

	public MadlibCommand(final String action) {
		super(COMMAND_WORD);
		this.action = action;
	}

	public String getAction() {
		return action;
	}
}
