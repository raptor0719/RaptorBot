package raptor.bot.main;

import java.io.InputStream;

import raptor.bot.api.IAliasManager;
import raptor.bot.api.IMadlibManager;
import raptor.bot.api.ISoundManager;
import raptor.bot.api.ITransformer;
import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.command.BotCommandParser;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.ChatStatsCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.command.commands.alias.AliasCommand;
import raptor.bot.command.commands.alias.AliasCreateCommand;
import raptor.bot.command.commands.alias.AliasDeleteCommand;
import raptor.bot.command.commands.alias.AliasListCommand;
import raptor.bot.command.commands.madlib.MadlibCommand;
import raptor.bot.command.commands.madlib.MadlibFillCommand;
import raptor.bot.command.commands.madlib.MadlibFormatCommand;
import raptor.bot.irc.ChatMessage;
import raptor.bot.utils.SoundPlayer;

public class RaptorBot {
	private final ISoundManager<String> soundManager;
	private final IAliasManager aliasManager;
	private final ITransformer<ChatMessage, String> chatProcessor;
	private final IMadlibManager madlibManager;
	private final IChatDatastore chatDatastore;

	public RaptorBot(final ISoundManager<String> soundManager, final IAliasManager aliasManager, final ITransformer<ChatMessage, String> chatProcessor, final IMadlibManager madlibManager, final IChatDatastore chatDatastore) {
		this.soundManager = soundManager;
		this.aliasManager = aliasManager;
		this.chatProcessor = chatProcessor;
		this.madlibManager = madlibManager;
		this.chatDatastore = chatDatastore;
	}

	public String message(final ChatMessage message) {
		storeMessageToChatLog(message);
		final BotCommand command = BotCommandParser.parseBotCommand(message.getMessage());

		if (command == null) {
			return chatProcessor.transform(message);
		} else if (command instanceof SoundCommand) {
			final SoundCommand soundCommand = (SoundCommand)command;
			return playSound(soundCommand.getSound());
		} else if (command instanceof HelpCommand) {
			final HelpCommand helpCommand = (HelpCommand)command;
			return helpCommand(helpCommand.getCommand());
		} else if (command instanceof AliasCommand) {
			return aliasCommand((AliasCommand)command);
		} else if (aliasManager.isAlias(command.getCommand())) {
			final String aliasedCommand = aliasManager.getAliasedPhrase(command.getCommand());
			return message(new ChatMessage(message.getChannel(), message.getUser(), aliasedCommand));
		} else if (command instanceof MadlibCommand) {
			return madlibCommand((MadlibCommand) command);
		} else if (command instanceof ChatStatsCommand) {
			final int totalMessages = chatDatastore.getTotalMessageCount();
			return (totalMessages < 0) ? "Statistics unavailable." : "Total messages sent: " + totalMessages;
		}

		return "Invalid command '" + command.getCommand() + "' given. " + helpCommand();
	}

	private void storeMessageToChatLog(final ChatMessage message) {
		try {
			chatDatastore.storeMessage(message.getChannel().substring(1), message.getUser().split("!")[0], message.getMessage(), System.currentTimeMillis());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private String playSound(final String sound) {
		final InputStream audio = soundManager.getSound(sound);
		if (audio != null) {
			try {
				System.out.println("Playing sound: " + sound);
				SoundPlayer.playSound(audio);
			} catch (Throwable t) {
				System.out.println("The SoundPlayer threw an error with the following message: " + t.getMessage());
			}
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
			return "Usage '!<sound>' or '!sound <sound>'. " + buildSoundsList();
		} else if (HelpCommand.COMMAND_WORD.equals(command) || "".equals(command)) {
			return "Use '!help <command>' for help for a specific command. " + buildCommandList();
		} else if (AliasCommand.COMMAND_WORD.equals(command)) {
			return "Use '!alias list' to list all aliases. Use '!alias create <alias> <command>' to create a new alias or replace an existing alias. Use '!alias delete <alias>' to delete an alias";
		} else if (MadlibCommand.COMMAND_WORD.equals(command)) {
			return "Use '!madlib fill <phrase>' to fill the marked-up phrase with random words. Use '!madlib format' for info on formatting your phrase.";
		} else {
			return "Unknown command given for help. " + helpCommand();
		}
	}

	private String aliasCommand(final AliasCommand command) {
		if (command instanceof AliasCreateCommand) {
			final AliasCreateCommand create = (AliasCreateCommand)command;
			aliasManager.create(create.getAlias(), create.getAliasedCommand());
			return "Created new alias.";
		} else if (command instanceof AliasDeleteCommand) {
			final AliasDeleteCommand delete = (AliasDeleteCommand)command;
			aliasManager.delete(delete.getAlias());
			return "Deleted alias.";
		} else if (command instanceof AliasListCommand) {
			return buildAliasList();
		}

		return helpCommand(AliasCommand.COMMAND_WORD);
	}

	private String madlibCommand(final MadlibCommand command) {
		if (command instanceof MadlibFillCommand) {
			final MadlibFillCommand fill = (MadlibFillCommand)command;
			return madlibManager.fill(fill.getPhrase());
		} else if (command instanceof MadlibFormatCommand) {
			return "The following specifies a replacement string for the given word type. The format is '<keyword>=<part-of-speech>'. Use the <keyword> in your phrase. " + madlibManager.getFormat();
		} else {
			return helpCommand(command.getCommand());
		}
	}

	private String buildSoundsList() {
		String soundList = "Sounds List: ";
		for (final String s : soundManager.getKeys()) {
			soundList += s + ", ";
		}
		return soundList.substring(0, soundList.length() - 2) + ".";
	}

	private String buildCommandList() {
		String commandList = "The following is a list of commands: ";
		for (final BotMethod c : BotMethod.values()) {
			commandList += c.getWord() + ", ";
		}
		return commandList.substring(0, commandList.length() - 2) + ".";
	}

	private String buildAliasList() {
		final Iterable<String> aliases = aliasManager.getAliases();

		if (!aliases.iterator().hasNext())
			return "";

		String aliasList = "The following is a list of aliases: ";
		for (final String s : aliases)
			aliasList += s + ", ";
		return aliasList.substring(0, aliasList.length() - 2) + ".";
	}
}
