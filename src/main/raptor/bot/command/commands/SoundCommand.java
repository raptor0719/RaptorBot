package raptor.bot.command.commands;

public class SoundCommand extends BotCommand {
	private final String sound;

	public SoundCommand(final String sound) {
		super(BotCommand.SOUND_COMMAND_STRING);
		this.sound = sound;
	}

	public String getSound() {
		return sound;
	}
}
