package raptor.bot.command.processors;

import java.io.InputStream;

import raptor.bot.api.ISoundManager;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.irc.ChatMessage;
import raptor.bot.utils.SoundPlayer;

public class SoundCommandProcessor extends BotCommandProcessor {
	private final ISoundManager<String> soundManager;

	public SoundCommandProcessor(final ISoundManager<String> soundManager) {
		super(BotMethod.SOUND.getWord(), "Usage '!<sound>' or '!sound <sound>'. " + buildSoundsList(soundManager));
		this.soundManager = soundManager;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.SOUND.parse(input);

		if (!(command instanceof SoundCommand)) {
			System.err.println("Expected instanceof SoundCommand.");
			return false;
		}

		final SoundCommand soundCommand = (SoundCommand)command;
		if (soundCommand.getSound() == null || soundCommand.getSound().isEmpty())
			sender.sendMessage(getHelpMessage());

		final InputStream audio = soundManager.getSound(soundCommand.getSound());

		if (audio != null)
			SoundPlayer.playSound(audio);

		return true;
	}

	private static String buildSoundsList(final ISoundManager<String> soundManager) {
		String soundList = "Sounds List: ";
		for (final String s : soundManager.getKeys()) {
			soundList += s + ", ";
		}
		return soundList.substring(0, soundList.length() - 2) + ".";
	}
}
