package raptor.bot.command.processors;

import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.MagicBallCommand;
import raptor.bot.irc.ChatMessage;
import raptor.bot.utils.MagicBall;

public class MagicBallCommandProcessor extends BotCommandProcessor {
	private final MagicBall magicBall;

	public MagicBallCommandProcessor() {
		super(BotMethod.MAGIC_BALL.getWord(), "Ask the magic 2 Ball a question with !2ball <question>.");
		magicBall = new MagicBall();
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.MAGIC_BALL.parse(input);

		if (!(command instanceof MagicBallCommand)) {
			System.err.println("Expected instanceof MagicBallCommand.");
			return false;
		}

		final MagicBallCommand magicBallCommand = (MagicBallCommand)command;
		magicBall.shake(magicBallCommand.getQuestion(), message.getUser(), sender);

		return true;
	}


}
