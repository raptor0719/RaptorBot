package raptor.bot.command.processors;

import raptor.bot.api.IMemeManager;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.MemeCommand;
import raptor.bot.irc.ChatMessage;
import raptor.bot.utils.MemePlayer;

public class MemeCommandProcessor extends BotCommandProcessor {
	private final IMemeManager memeManager;

	public MemeCommandProcessor(final IMemeManager memeManager) {
		super(BotMethod.MEME.getWord(), "Use '!<name>' or '!meme <name>' to display the meme on screen! VisLaud " + buildMemeList(memeManager));
		this.memeManager = memeManager;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.MEME.parse(input);

		if (!(command instanceof MemeCommand)) {
			System.err.println("Expected instanceof MemeCommand.");
			return false;
		}

		final MemeCommand memeCommand = (MemeCommand)command;
		final String meme = memeCommand.getMeme();

		if (meme == null || "".equals(meme.trim()))
			return true;

		MemePlayer.playMeme(memeManager.getMemeFile(meme), memeManager.getMemeLength(meme));

		return true;
	}

	private static String buildMemeList(final IMemeManager memeManager) {
		String memeList = "Memes List: ";
		for (final String s : memeManager.getMemes()) {
			memeList += s + ", ";
		}
		return memeList.substring(0, memeList.length() - 2) + ".";
	}
}
