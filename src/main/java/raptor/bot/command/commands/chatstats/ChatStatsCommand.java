package raptor.bot.command.commands.chatstats;

import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;

public class ChatStatsCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.CHAT_STATS.getWord();

	public ChatStatsCommand() {
		super(COMMAND_WORD);
	}
}
