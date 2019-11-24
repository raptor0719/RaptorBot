package raptor.bot.command.commands;

import raptor.bot.command.BotMethod;

public class ChatStatsCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.CHAT_STATS.getWord();

	public ChatStatsCommand() {
		super(COMMAND_WORD);
	}
}
