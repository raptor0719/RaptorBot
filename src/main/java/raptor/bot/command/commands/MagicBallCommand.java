package raptor.bot.command.commands;

import raptor.bot.command.BotMethod;

public class MagicBallCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.MAGIC_BALL.getWord();
	private final String question;

	public MagicBallCommand(final String question) {
		super(COMMAND_WORD);
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}
}
