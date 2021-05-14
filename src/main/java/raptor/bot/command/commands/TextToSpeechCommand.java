package raptor.bot.command.commands;

import raptor.bot.command.BotMethod;

public class TextToSpeechCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.TEXT_TO_SPEECH.getWord();
	private final String phrase;

	public TextToSpeechCommand(final String phrase) {
		super(COMMAND_WORD);
		this.phrase = phrase;
	}

	public String getPhrase() {
		return phrase;
	}
}
