package raptor.bot.main;

import java.io.File;
import java.util.Map;

import raptor.bot.command.BotCommandParser;
import raptor.bot.command.CommandWords;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.utils.SoundPlayer;

public class RaptorBot {
	private final Map<String, String> sounds;

	public RaptorBot(final Map<String, String> sounds) {
		this.sounds = sounds;
	}

	public String message(final String user, final String message) {
		final BotCommand command = BotCommandParser.parseBotCommand(message);

		if (command instanceof SoundCommand) {
			final SoundCommand soundCommand = (SoundCommand)command;
			return playSound(soundCommand.getSound());
		} else if (command instanceof HelpCommand) {
			final HelpCommand helpCommand = (HelpCommand)command;
			return helpCommand(helpCommand.getCommand());
		}

		return "Invalid command given. " + helpCommand();
	}

	private String playSound(final String sound) {
		final String soundPath = sounds.get(sound);
		if (soundPath != null) {
			try {
				SoundPlayer.playSound(new File(soundPath));
			} catch (Throwable t) {}
		} else {
			return helpCommand(SoundCommand.COMMAND_WORD);
		}
		return "";
	}

	private String helpCommand() {
		return helpCommand("");
	}

	private String helpCommand(final String command) {
		if (SoundCommand.COMMAND_WORD.equals(command)) {
			return "Usage '!sound <sound>'. " + buildSoundsList();
		} else if (HelpCommand.COMMAND_WORD.equals(command) || "".equals(command)) {
			return "Use '!help <command>' for help for a specific command. " + buildCommandList();
		} else {
			return "Unknown command given for help. " + helpCommand();
		}
	}

	private String buildSoundsList() {
		String soundList = "Sounds List: ";
		for (final Map.Entry<String, String> e : sounds.entrySet()) {
			soundList += e.getKey() + ", ";
		}
		return soundList.substring(0, soundList.length() - 2) + ".";
	}

	private String buildCommandList() {
		String commandList = "The following is a list of commands: ";
		for (final CommandWords c : CommandWords.values()) {
			commandList += c.getWord() + ", ";
		}
		return commandList.substring(0, commandList.length() - 2) + ".";
	}
}
