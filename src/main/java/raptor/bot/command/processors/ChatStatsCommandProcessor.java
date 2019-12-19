package raptor.bot.command.processors;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.ChatStatsCommand;
import raptor.bot.irc.ChatMessage;

public class ChatStatsCommandProcessor extends BotCommandProcessor {
	private final IChatDatastore chatDatastore;

	public ChatStatsCommandProcessor(final IChatDatastore chatDatastore) {
		super(BotMethod.CHAT_STATS.getWord(), "Use !chatstats to get statistics on chat!");
		this.chatDatastore = chatDatastore;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.CHAT_STATS.parse(input);

		if (!(command instanceof ChatStatsCommand)) {
			System.err.println("Expected instanceof ChatStatsCommand.");
			return false;
		}

		final int totalMessages = chatDatastore.getTotalMessageCount();
		sender.sendMessage((totalMessages < 0) ? "Statistics unavailable." : "Total messages sent: " + totalMessages);
		return true;
	}
}
