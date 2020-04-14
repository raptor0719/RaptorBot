package raptor.bot.command.processors;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.chatstats.ChatStatsCommand;
import raptor.bot.command.commands.chatstats.ChatStatsUserMessageCountCommand;
import raptor.bot.irc.ChatMessage;

public class ChatStatsCommandProcessor extends BotCommandProcessor {
	private final IChatDatastore chatDatastore;

	public ChatStatsCommandProcessor(final IChatDatastore chatDatastore) {
		super(BotMethod.CHAT_STATS.getWord(), "Use !chatstats to get statistics on chat or !chatstats <user> for stats a specific user!");
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

		if (command instanceof ChatStatsUserMessageCountCommand) {
			final ChatStatsUserMessageCountCommand userCount = (ChatStatsUserMessageCountCommand)command;
			final int userMessageCount = chatDatastore.getMessageCountForUser(userCount.getUser());
			sender.sendMessage((userMessageCount < 0) ? "Statistics unavailable." : String.format("Message count for %s: %s", userCount.getUser(), userMessageCount));
		} else {
			final int totalMessages = chatDatastore.getTotalMessageCount();
			sender.sendMessage((totalMessages < 0) ? "Statistics unavailable." : "Total messages sent: " + totalMessages);
		}
		return true;
	}
}
