package raptor.bot.command;

import java.util.regex.Pattern;

import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.SoundCommand;

public class BotCommandParser {
	public static BotCommand parseBotCommand(final String message) {
		final CommandInfo info = parseCommand(message);

		switch (info.type) {
			case Sound:
				return new SoundCommand(info.sound);
			case Help:
				return new HelpCommand(info.commandHelp);
			default:
				return new BotCommand(info.unknownCommandString);
		}
	}

	private static CommandInfo parseCommand(final String message) {
		final CommandInfo info = new CommandInfo(message);

		if (message.startsWith("!")) {
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf("!") + 1);
			final String commandWord = info.remainingMessage.split(" ")[0];
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(" ") + 1);

			if (CommandWords.SOUND.getWord().equals(commandWord)) {
				parseSoundCommand(info);
			} else if (CommandWords.SOUND.getWord().equals(commandWord)) {
				parseHelpCommand(info);
			} else {
				info.unknownCommandString = commandWord;
			}

			return info;
		}

		return info;
	}

	private static CommandInfo parseSoundCommand(final CommandInfo info) {
		info.type = CommandType.Sound;
		final String sound = info.remainingMessage.split(" ")[0];

		if (!Pattern.matches("[a-zA-Z0-9_-]*", sound))
			throw new RuntimeException("Malformed argument for sound command was given.");

		info.sound = sound;
		return info;
	}

	private static CommandInfo parseHelpCommand(final CommandInfo info) {
		info.type = CommandType.Help;
		final String commandHelp = info.remainingMessage.split(" ")[0];

		if (!Pattern.matches("[a-zA-Z0-9_-]*", commandHelp))
			throw new RuntimeException("Malformed argument for help command was given.");

		info.commandHelp = commandHelp;
		return info;
	}

	private static class CommandInfo {
		public final String originalMessage;
		public String remainingMessage;

		public CommandType type = CommandType.Unknown;
		public String sound;
		public String commandHelp;

		public String unknownCommandString;

		public CommandInfo(final String message) {
			originalMessage = message;
			remainingMessage = message;
		}
	}

	private static enum CommandType {
		Sound,
		Help,
		Unknown
	}
}
