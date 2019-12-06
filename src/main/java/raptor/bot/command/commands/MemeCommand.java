package raptor.bot.command.commands;

import raptor.bot.command.BotMethod;

public class MemeCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.MEME.getWord();
	private final String meme;

	public MemeCommand() {
		this(null);
	}

	public MemeCommand(final String meme) {
		super(COMMAND_WORD);
		this.meme = meme;
	}

	public String getMeme() {
		return meme;
	}
}
