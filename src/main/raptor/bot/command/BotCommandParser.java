package raptor.bot.command;

import java.util.regex.Pattern;

import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.command.commands.alias.AliasCommand;
import raptor.bot.command.commands.alias.AliasCreateCommand;
import raptor.bot.command.commands.alias.AliasDeleteCommand;
import raptor.bot.command.commands.alias.AliasListCommand;

public class BotCommandParser {
	public static BotCommand parseBotCommand(final String message) {
		final CommandInfo info = parseCommand(message);

		switch (info.type) {
			case Sound:
				return new SoundCommand(info.sound);
			case Help:
				return new HelpCommand(info.commandHelp);
			case Alias:
				return new AliasCommand();
			case Alias_create:
				return new AliasCreateCommand(info.alias, info.aliasedPhrase);
			case Alias_delete:
				return new AliasDeleteCommand(info.alias);
			case Alias_list:
				return new AliasListCommand();
			case NotACommand:
				return null;
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
			} else if (CommandWords.HELP.getWord().equals(commandWord)) {
				parseHelpCommand(info);
			} else if (CommandWords.ALIAS.getWord().equals(commandWord)) {
				parseAliasCommand(info);
			} else {
				info.unknownCommandString = commandWord;
			}

			return info;
		} else {
			info.type = CommandType.NotACommand;
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

	private static CommandInfo parseAliasCommand(final CommandInfo info) {
		final String actionWord = info.remainingMessage.split(" ")[0];
		info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(" ") + 1);

		if (AliasCreateCommand.SUB_COMMAND_WORD.equals(actionWord)) {
			info.type = CommandType.Alias_create;
			info.alias = info.remainingMessage.split(" ")[0];
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(" ") + 1);
			info.aliasedPhrase = info.remainingMessage;
			info.remainingMessage = "";
		} else if (AliasDeleteCommand.SUB_COMMAND_WORD.equals(actionWord)) {
			info.type = CommandType.Alias_delete;
			info.alias = info.remainingMessage.split(" ")[0];
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(" ") + 1);
		} else if (AliasListCommand.SUB_COMMAND_WORD.equals(actionWord)) {
			info.type = CommandType.Alias_list;
		} else {
			info.type = CommandType.Alias;
		}

		return info;
	}

	private static class CommandInfo {
		public final String originalMessage;
		public String remainingMessage;

		public CommandType type = CommandType.Unknown;
		public String sound;
		public String commandHelp;

		public String alias;
		public String aliasedPhrase;

		public String unknownCommandString;

		public CommandInfo(final String message) {
			originalMessage = message;
			remainingMessage = message;
		}
	}

	private static enum CommandType {
		Alias,
		Alias_create,
		Alias_list,
		Alias_delete,
		Sound,
		Help,
		Unknown,
		NotACommand
	}
}
