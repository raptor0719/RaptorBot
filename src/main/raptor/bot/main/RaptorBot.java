package raptor.bot.main;

import java.io.File;
import java.util.Map;
import java.util.Set;

import raptor.bot.command.BotCommandParser;
import raptor.bot.command.CommandWords;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.command.commands.alias.AliasCommand;
import raptor.bot.command.commands.alias.AliasCreateCommand;
import raptor.bot.command.commands.alias.AliasDeleteCommand;
import raptor.bot.command.commands.alias.AliasListCommand;
import raptor.bot.irc.ChatMessage;
import raptor.bot.utils.AliasManager;
import raptor.bot.utils.SoundPlayer;

public class RaptorBot {
	private final Map<String, String> sounds;
	private final AliasManager aliasManager;

	public RaptorBot(final Map<String, String> sounds, final String aliasFilePath) {
		this.sounds = sounds;
		aliasManager = new AliasManager(aliasFilePath);
	}

	public String message(final ChatMessage message) {
		final BotCommand command = BotCommandParser.parseBotCommand(message.getMessage());

		if (command == null) {
			return "";
		} else if (command instanceof SoundCommand) {
			final SoundCommand soundCommand = (SoundCommand)command;
			return playSound(soundCommand.getSound());
		} else if (command instanceof HelpCommand) {
			final HelpCommand helpCommand = (HelpCommand)command;
			return helpCommand(helpCommand.getCommand());
		} else if (command instanceof AliasCommand) {
			return aliasCommand((AliasCommand)command);
		} else if (isAlias(command.getCommand())) {
			final String aliasedCommand = aliasManager.getAliases().get(command.getCommand());
			return message(new ChatMessage(message.getUser(), aliasedCommand));
		}

		return "Invalid command '" + command.getCommand() + "' given. " + helpCommand();
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
		} else if (AliasCommand.COMMAND_WORD.equals(command)) {
			return "Use '!alias list' to list all aliases. Use '!alias create <alias> <command>' to create a new alias or replace an existing alias. Use '!alias delete <alias>' to delete an alias";
		} else {
			return "Unknown command given for help. " + helpCommand();
		}
	}

	private String aliasCommand(final AliasCommand command) {
		if (command instanceof AliasCreateCommand) {
			final AliasCreateCommand create = (AliasCreateCommand)command;
			aliasManager.create(create.getAlias(), create.getAliasedCommand());
		} else if (command instanceof AliasDeleteCommand) {
			final AliasDeleteCommand delete = (AliasDeleteCommand)command;
			aliasManager.delete(delete.getAlias());
		} else if (command instanceof AliasListCommand) {
			return buildAliasList(aliasManager);
		}

		return helpCommand(AliasCommand.COMMAND_WORD);
	}

	private boolean isAlias(final String command) {
		return aliasManager.getAliases().containsKey(command);
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
		return commandList.substring(0, commandList.length() - 2) + ". " + buildAliasList(aliasManager);
	}

	private String buildAliasList(final AliasManager manager) {
		final Set<String> keySet = manager.getAliases().keySet();
		if (keySet.size() < 1)
			return "";
		String aliasList = "The following is a list of aliases: ";
		for (final String s : keySet) {
			aliasList += s + ", ";
		}
		return aliasList.substring(0, aliasList.length() - 2) + ".";
	}
}
