package raptor.bot.main;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import raptor.bot.api.IAliasManager;
import raptor.bot.api.IMadlibManager;
import raptor.bot.api.IMemeManager;
import raptor.bot.api.ISoundManager;
import raptor.bot.api.ITransformer;
import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageService;
import raptor.bot.command.BotCommandParser;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.ChatStatsCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.MemeCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.command.commands.WisdomCommand;
import raptor.bot.command.commands.alias.AliasCommand;
import raptor.bot.command.commands.alias.AliasCreateCommand;
import raptor.bot.command.commands.alias.AliasDeleteCommand;
import raptor.bot.command.commands.alias.AliasListCommand;
import raptor.bot.command.commands.madlib.MadlibCommand;
import raptor.bot.command.commands.madlib.MadlibFillCommand;
import raptor.bot.command.commands.madlib.MadlibFormatCommand;
import raptor.bot.irc.ChatMessage;
import raptor.bot.utils.MemePlayer;
import raptor.bot.utils.SoundPlayer;

public class RaptorBot {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private final IMessageService<ChatMessage, String> messageService;
	private final ISoundManager<String> soundManager;
	private final IAliasManager aliasManager;
	private final ITransformer<ChatMessage, String> chatProcessor;
	private final IMadlibManager madlibManager;
	private final IMemeManager memeManager;
	private final IChatDatastore chatDatastore;

	public RaptorBot(final IMessageService<ChatMessage, String> messageService, final ISoundManager<String> soundManager, final IAliasManager aliasManager, final ITransformer<ChatMessage, String> chatProcessor, final IMadlibManager madlibManager, final IMemeManager memeManager, final IChatDatastore chatDatastore) {
		this.messageService = messageService;
		this.soundManager = soundManager;
		this.aliasManager = aliasManager;
		this.chatProcessor = chatProcessor;
		this.madlibManager = madlibManager;
		this.memeManager = memeManager;
		this.chatDatastore = chatDatastore;
	}

	public void process() {
		final Iterator<ChatMessage> messages = messageService.receiveMessages();
		while (messages.hasNext()) {
			final ChatMessage message = messages.next();
			final String botResponse = message(message);
			storeMessageToChatLog(message);
			if (botResponse != null && !"".equals(botResponse.trim()))
				messageService.sendMessage(botResponse);
		}
	}

	private String message(final ChatMessage message) {
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
			return message(new ChatMessage(message.getChannel(), message.getUser(), aliasedCommand, message.getTimestamp()));
		} else if (command instanceof MadlibCommand) {
			return madlibCommand((MadlibCommand) command);
		} else if (command instanceof MemeCommand) {
			final MemeCommand memeCommand = (MemeCommand)command;
			return memeCommand(memeCommand);
		} else if (command instanceof ChatStatsCommand) {
			final int totalMessages = chatDatastore.getTotalMessageCount();
			return (totalMessages < 0) ? "Statistics unavailable." : "Total messages sent: " + totalMessages;
		} else if (command instanceof WisdomCommand) {
			final WisdomCommand wisdomCommand = (WisdomCommand)command;
			return wisdomCommand(wisdomCommand.getIndex());
		}

		return "";
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

		if (audio != null)
			SoundPlayer.playSound(audio);

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
		} else if (MemeCommand.COMMAND_WORD.equals(command)) {
			return "Use '!<name>' or '!meme <name>' to display the meme on screen! VisLaud " + buildMemeList();
		} else if (WisdomCommand.COMMAND_WORD.equals(command)) {
			return "Use '!wisdom' or '!wisdom <index>' to get some past wisdom from chat.";
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

	private String memeCommand(final MemeCommand command) {
		if (command.getMeme() == null)
			return helpCommand(MemeCommand.COMMAND_WORD);
		MemePlayer.playMeme(memeManager.getMemeFile(command.getMeme()), memeManager.getMemeLength(command.getMeme()));
		return "";
	}

	private String wisdomCommand(final int index) {
		final int max = chatDatastore.getTotalMessageCount();
		final int rowNumber = (index < 0) ? (int)(Math.random() * max) + 1 : index;
		final ChatMessage msg = chatDatastore.getMessage(rowNumber);
		if (msg == null)
			return "There was no wisdom found. FeelsBadMan";
		return "Wisdom #" + rowNumber + ": " + msg.getMessage() + " - " + msg.getUser() + " [" + sdf.format(new Date(msg.getTimestamp())) + "]";
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

	private String buildMemeList() {
		String memeList = "Memes List: ";
		for (final String s : memeManager.getMemes()) {
			memeList += s + ", ";
		}
		return memeList.substring(0, memeList.length() - 2) + ".";
	}
}
