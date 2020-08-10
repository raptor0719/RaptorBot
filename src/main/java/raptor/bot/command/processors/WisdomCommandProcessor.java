package raptor.bot.command.processors;

import java.text.SimpleDateFormat;
import java.util.Date;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.WisdomCommand;
import raptor.bot.irc.ChatMessage;

public class WisdomCommandProcessor extends BotCommandProcessor {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private final IChatDatastore chatDatastore;

	public WisdomCommandProcessor(final IChatDatastore chatDatastore) {
		super(BotMethod.WISDOM.getWord(), "Use '!wisdom' or '!wisdom <index>' to get some past wisdom from chat.");
		this.chatDatastore = chatDatastore;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.WISDOM.parse(input);

		if (!(command instanceof WisdomCommand)) {
			System.err.println("Expected instanceof WisdomCommand.");
			return false;
		}

		final WisdomCommand wisdomCommand = (WisdomCommand)command;
		final int index = wisdomCommand.getIndex();
		final int max = chatDatastore.getTotalMessageCount();
		final int rowNumber = (index < 0) ? (int)(Math.random() * max) : index;
		final ChatMessage msg = chatDatastore.getMessage(rowNumber);

		if (msg == null)
			sender.sendMessage("There was no wisdom found. FeelsBadMan");
		else
			sender.sendMessage("Wisdom #" + rowNumber + ": " + msg.getMessage() + " - " + msg.getUser() + " [" + sdf.format(new Date(msg.getTimestamp())) + "]");

		return true;
	}
}
