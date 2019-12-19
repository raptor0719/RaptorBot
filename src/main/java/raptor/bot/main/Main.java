package raptor.bot.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import raptor.bot.api.IAliasManager;
import raptor.bot.api.ITransformer;
import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageService;
import raptor.bot.command.BotCommandListProcessor;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.processors.AliasCommandProcessor;
import raptor.bot.command.processors.ChatStatsCommandProcessor;
import raptor.bot.command.processors.HelpCommandProcessor;
import raptor.bot.command.processors.MadlibCommandProcessor;
import raptor.bot.command.processors.MemeCommandProcessor;
import raptor.bot.command.processors.SoundCommandProcessor;
import raptor.bot.command.processors.WisdomCommandProcessor;
import raptor.bot.irc.ChatMessage;
import raptor.bot.irc.IRCClient;
import raptor.bot.test.utils.TestWindow;
import raptor.bot.utils.AliasManager;
import raptor.bot.utils.MadlibManager;
import raptor.bot.utils.MemeManager;
import raptor.bot.utils.MemePlayer;
import raptor.bot.utils.QueueBasedMessageService;
import raptor.bot.utils.SoundManager;
import raptor.bot.utils.TransformerPipe;
import raptor.bot.utils.chat.FileChatDatastore;
import raptor.bot.utils.chat.NoOpChatDatastore;
import raptor.bot.utils.chat.SQLChatDatastore;
import raptor.bot.utils.words.PartOfSpeech;
import raptor.bot.utils.words.WordBank;

public class Main {
	public static void main(String[] args) {
		BotConfig config = null;
		try {
			if (args.length >= 2)
				config = new BotConfig(new FileInputStream(new File(args[1])));
			else
				config = new BotConfig(new FileInputStream(new File("raptorbot.properties")));
		} catch (final IOException e) {
			throw new RuntimeException("An error occured while building the configuration.", e);
		}
		MemePlayer.currentFileName = config.getMemeActiveFilePath();

		final Queue<ChatMessage> botInputQueue = new ConcurrentLinkedQueue<ChatMessage>();
		final Queue<String> botOutputQueue = new ConcurrentLinkedQueue<String>();
		final IMessageService<ChatMessage, String> botMessageService = new QueueBasedMessageService<ChatMessage, String>(botInputQueue, botOutputQueue);
		final IMessageService<String, ChatMessage> botInputOutput = new QueueBasedMessageService<String, ChatMessage>(botOutputQueue, botInputQueue);

		final IChatDatastore chatDatastore = getConfiguredChatDataManager(config);
		final IAliasManager aliasManager = new AliasManager(config.getAliasFilePath());

		final List<BotCommandProcessor> processors = new ArrayList<BotCommandProcessor>();
		processors.add(new AliasCommandProcessor(aliasManager));
		processors.add(new ChatStatsCommandProcessor(chatDatastore));
		processors.add(new MadlibCommandProcessor(new MadlibManager(getWordBank(config.getDictionaryFilePath()))));
		processors.add(new WisdomCommandProcessor(chatDatastore));
		processors.add(new SoundCommandProcessor(new SoundManager(config.getSoundsFilePath())));
		processors.add(new MemeCommandProcessor(new MemeManager(config.getMemeFilePath())));
		processors.add(new HelpCommandProcessor(processors));

		final RaptorBot bot = new RaptorBot(botMessageService, getChatProcessor(config.getIrcChannel(), config.getIrcUser()), chatDatastore, new BotCommandListProcessor(processors));

		if (args.length >= 1 && Boolean.parseBoolean(args[0])) {
			new TestWindow(bot, botInputOutput, chatDatastore);
			return;
		}

		final IRCClient client = new IRCClient(config.getIrcIp(), config.getIrcPort(), config.getIrcUser(), config.getIrcUser(), config.getIrcPassword());

		final long messageDelay = config.getBotMessageCooldown();
		long lastMessageTime = 0L;

		try {
			final String channel = config.getIrcChannel();
			System.out.println("Attempting connection...");
			client.connect();
			System.out.println("Connection success!");
			client.joinChannel(channel);

			while (true) {
				client.process();

				final Iterator<ChatMessage> messages = client.getMessages(channel);
				while (messages.hasNext()) {
					final ChatMessage message = messages.next();
					System.out.println(message.getUser() + ": " + message.getMessage());
					botInputOutput.sendMessage(message);
				}

				bot.process();

				final Iterator<String> botResponses = botInputOutput.receiveMessages();
				while (botResponses.hasNext() && (System.currentTimeMillis() - lastMessageTime) >= messageDelay) {
					final String botResponse = botResponses.next();
					if (botResponse != null && !botResponse.isEmpty()) {
						client.sendMessage(botResponse, channel);
						lastMessageTime = System.currentTimeMillis();
					}
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static IChatDatastore getConfiguredChatDataManager(final BotConfig config) {
		if (config.isEnableChatDatastoreFile())
			return new FileChatDatastore(config.getChatDatastoreFileDirectoryPath());
		else if (config.isEnableChatDatastoreSql())
			return new SQLChatDatastore(config.getChatDatastoreSqlConnectionURL(), config.getChatDatastoreSqlSchema(), config.getChatDatastoreSqlTable());
		else
			return new NoOpChatDatastore();
	}

	private static ITransformer<ChatMessage, String> getChatProcessor(final String channel, final String botName) {
		final ITransformer<ChatMessage, ChatMessage> wombatGreeter = new ITransformer<ChatMessage, ChatMessage>() {
			final long timeBetweenGreetings = 3600000L;
			long lastGreeting = -1;
			@Override
			public ChatMessage transform(final ChatMessage in) {
				if (in == null)
					return null;

				if (in.getUser().toLowerCase().contains("wombat")) {
					if ((System.currentTimeMillis() - lastGreeting) >= timeBetweenGreetings || lastGreeting < 0) {
						lastGreeting = System.currentTimeMillis();
						return new ChatMessage(channel, botName, "Welcome to the chat room!", lastGreeting);
					} else {
						// Increase the time to next greeting by 1 minute every time wombat says something in chat
						lastGreeting += 60000;
					}
				}

				return null;
			}
		};

		final List<ITransformer<ChatMessage, ChatMessage>> processors = new LinkedList<>();
		processors.add(wombatGreeter);

		final ITransformer<ChatMessage, ChatMessage> pipe = new TransformerPipe<ChatMessage>(processors);

		return new ITransformer<ChatMessage, String>() {
			@Override
			public String transform(final ChatMessage in) {
				final ChatMessage message = pipe.transform(in);
				return (message == null) ? "" : message.getMessage();
			}
		};
	}

	private static WordBank getWordBank(final String wordFilePath) {
		return new WordBank(extractWords(wordFilePath));
	}

	private static Map<PartOfSpeech, List<String>> extractWords(final String wordFilePath) {
		final File wordFile = new File(wordFilePath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(wordFile);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

			final Map<PartOfSpeech, List<String>> wordBank = new HashMap<PartOfSpeech, List<String>>();
			final PartOfSpeech[] partsOfSpeech = PartOfSpeech.values();

			for (final PartOfSpeech pos : partsOfSpeech)
				wordBank.put(pos, new ArrayList<String>());

			String line = reader.readLine();
			while (line != null) {
				final String[] data = line.split("	");
				line = reader.readLine();

				if (data.length < 2 || data[0].startsWith("#"))
					continue;

				final String word = data[0];
				final boolean[] used = new boolean[partsOfSpeech.length];
				Arrays.fill(used, false);

				for (final String c : data[1].split(",")) {
					switch (c) {
						case "n":
							wordBank.get(PartOfSpeech.Noun).add(word);
							used[PartOfSpeech.Noun.ordinal()] = true;
							break;
						case "N":
							wordBank.get(PartOfSpeech.PeopleNoun).add(word);
							used[PartOfSpeech.PeopleNoun.ordinal()] = true;
							break;
						case "v":
							wordBank.get(PartOfSpeech.PresentVerb).add(word);
							used[PartOfSpeech.PresentVerb.ordinal()] = true;
							break;
						case "pv":
							wordBank.get(PartOfSpeech.PastVerb).add(word);
							used[PartOfSpeech.PastVerb.ordinal()] = true;
							break;
						case "a":
							wordBank.get(PartOfSpeech.Adjective).add(word);
							used[PartOfSpeech.Adjective.ordinal()] = true;
							break;
						case "adv":
							wordBank.get(PartOfSpeech.Adverb).add(word);
							used[PartOfSpeech.Adverb.ordinal()] = true;
							break;
						case "!":
							wordBank.get(PartOfSpeech.Interjection).add(word);
							used[PartOfSpeech.Interjection.ordinal()] = true;
							break;
						default:
					}
				}
			}
			reader.close();
			return wordBank;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				fis.close();
			} catch (Throwable t) {}
		}
	}
}
