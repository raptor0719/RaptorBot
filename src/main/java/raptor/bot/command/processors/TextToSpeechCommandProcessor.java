package raptor.bot.command.processors;

import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.TextToSpeechCommand;
import raptor.bot.irc.ChatMessage;
import raptor.bot.tts.TextToSpeech;

public class TextToSpeechCommandProcessor extends BotCommandProcessor {
	private final TextToSpeech tts;

	public TextToSpeechCommandProcessor(final TextToSpeech tts) {
		super(BotMethod.TEXT_TO_SPEECH.getWord(), "Say a phrase with audio using !tts <phrase>.");
		this.tts = tts;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.TEXT_TO_SPEECH.parse(input);

		if (!(command instanceof TextToSpeechCommand)) {
			System.err.println("Expected instanceof TextToSpeechCommand.");
			return false;
		}

		tts.speakPhrase(input);

		return true;
	}
}
