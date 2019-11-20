package raptor.bot.command.commands.alias;

import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;

public class AliasCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.ALIAS.getWord();
	private final String action;

	public AliasCommand() {
		this("");
	}

	public AliasCommand(final String action) {
		super(COMMAND_WORD);
		this.action = action;
	}

	public String getAction() {
		return action;
	}
}
