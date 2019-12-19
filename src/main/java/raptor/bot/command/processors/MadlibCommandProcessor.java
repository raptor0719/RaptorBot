package raptor.bot.command.processors;

import raptor.bot.api.IMadlibManager;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.madlib.MadlibCommand;
import raptor.bot.command.commands.madlib.MadlibFillCommand;
import raptor.bot.command.commands.madlib.MadlibFormatCommand;
import raptor.bot.irc.ChatMessage;

public class MadlibCommandProcessor extends BotCommandProcessor {
	private final IMadlibManager madlibManager;

	public MadlibCommandProcessor(final IMadlibManager madlibManager) {
		super(BotMethod.MADLIB.getWord(), "Use '!madlib fill <phrase>' to fill the marked-up phrase with random words. Use '!madlib format' for info on formatting your phrase.");
		this.madlibManager = madlibManager;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.MADLIB.parse(input);

		if (!(command instanceof MadlibCommand)) {
			System.err.println("Expected instanceof MadlibCommand.");
			return false;
		}

		if (command instanceof MadlibFillCommand) {
			final MadlibFillCommand fill = (MadlibFillCommand)command;
			sender.sendMessage(madlibManager.fill(fill.getPhrase()));
		} else if (command instanceof MadlibFormatCommand) {
			sender.sendMessage("The following specifies a replacement string for the given word type. The format is '<keyword>=<part-of-speech>'. Use the <keyword> in your phrase. " + madlibManager.getFormat());
		}

		return true;
	}
}
