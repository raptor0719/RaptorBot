package raptor.bot.command.commands;

import raptor.bot.command.CommandWords;

public class SoundCommand extends BotCommand {
	public static final String COMMAND_WORD = CommandWords.SOUND.getWord();
	private final String sound;

	public SoundCommand(final String sound) {
		super(COMMAND_WORD);
		this.sound = sound;
	}

	public String getSound() {
		return sound;
	}
}
