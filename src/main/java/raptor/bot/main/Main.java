package raptor.bot.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import raptor.bot.api.IBotProcessor;
import raptor.bot.api.IInherentBotProcessor;
import raptor.bot.api.ITransformer;
import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageService;
import raptor.bot.chatter.mimic.ChatMimic;
import raptor.bot.chatter.mimic.ChatMimicDictionary;
import raptor.bot.chatter.processors.CaveBobberTimedMessageBotProcessor;
import raptor.bot.chatter.processors.ChatMimicTimedMessageBotProcessor;
import raptor.bot.chatter.processors.ConcreteTimedMessageBotProcessor;
import raptor.bot.command.BotCommandListProcessor;
import raptor.bot.command.processors.AliasCommandProcessor;
import raptor.bot.command.processors.AliasedCommandProcessor;
import raptor.bot.command.processors.ChatStatsCommandProcessor;
import raptor.bot.command.processors.HelpCommandProcessor;
import raptor.bot.command.processors.MadlibCommandProcessor;
import raptor.bot.command.processors.MagicBallCommandProcessor;
import raptor.bot.command.processors.MemeCommandProcessor;
import raptor.bot.command.processors.SoundCommandProcessor;
import raptor.bot.command.processors.WisdomCommandProcessor;
import raptor.bot.irc.ChatMessage;
import raptor.bot.irc.IRCClient;
import raptor.bot.test.utils.TestWindow;
import raptor.bot.utils.AliasManager;
import raptor.bot.utils.InherentBotProcessorPipe;
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

		final List<IBotProcessor<ChatMessage>> processors = new ArrayList<IBotProcessor<ChatMessage>>();
		processors.add(new AliasCommandProcessor(aliasManager));
		processors.add(new ChatStatsCommandProcessor(chatDatastore));
		processors.add(new MadlibCommandProcessor(new MadlibManager(getWordBank(config.getDictionaryFilePath()))));
		processors.add(new WisdomCommandProcessor(chatDatastore));
		processors.add(new SoundCommandProcessor(new SoundManager(config.getSoundsFilePath())));
		processors.add(new MemeCommandProcessor(new MemeManager(config.getMemeFilePath())));
		processors.add(new MagicBallCommandProcessor());
		processors.add(new HelpCommandProcessor(processors));
		processors.add(new AliasedCommandProcessor(aliasManager, processors));

		final ChatMimicDictionary chatMimicDictionary = (config.isCompileChatMimicDictionary()) ? ChatMimicDictionary.compile(chatDatastore) : readChatMimicDictionaryFromDisk();
		if (config.isCompileChatMimicDictionary())
			saveChatMimicDictionaryToDisk(chatMimicDictionary);

		final ChatMimic chatMimic = new ChatMimic(chatMimicDictionary);

		final RaptorBot bot = new RaptorBot(botMessageService, getChatProcessor(config.getIrcChannel(), config.getIrcUser(), chatMimic, chatDatastore), chatDatastore, new BotCommandListProcessor(processors), getInherentProcessor(chatMimic, chatDatastore));

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

	private static ChatMimicDictionary readChatMimicDictionaryFromDisk() {
		final File mimdicFile = new File("bot.mimdic");
		FileInputStream is = null;
		try {
			if (!mimdicFile.exists())
				throw new RuntimeException("bot.mimdic was not found in the bot directory.");

			is = new FileInputStream(mimdicFile);

			return ChatMimicDictionary.marshal(new BufferedInputStream(is, 2400));
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	private static void saveChatMimicDictionaryToDisk(final ChatMimicDictionary dic) {
		final File mimdicFile = new File("bot.mimdic");
		FileOutputStream os = null;
		try {
			if (mimdicFile.exists())
				mimdicFile.delete();

			os = new FileOutputStream(mimdicFile);

			ChatMimicDictionary.serialize(dic, os);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				os.close();
			} catch (Throwable t) {};
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

	private static ITransformer<ChatMessage, String> getChatProcessor(final String channel, final String botName, final ChatMimic mimic, final IChatDatastore datastore) {
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

		final ITransformer<ChatMessage, ChatMessage> chatMimic = new ITransformer<ChatMessage, ChatMessage>() {
			@Override
			public ChatMessage transform(final ChatMessage in) {
				final List<String> lines = new ArrayList<>();
				lines.add(in.getMessage());
				final String response = mimic.mimic(lines);

				return in.getMessage().contains("@" + botName) ? new ChatMessage(channel, botName, response, System.currentTimeMillis()) : null;
			}
		};

		final ITransformer<ChatMessage, ChatMessage> TEMP_END = new ITransformer<ChatMessage, ChatMessage>() {
			@Override
			public ChatMessage transform(final ChatMessage in) {
				return null;
			}
		};

		final List<ITransformer<ChatMessage, ChatMessage>> processors = new LinkedList<>();
		//processors.add(wombatGreeter);
		//processors.add(TEMP_END);
		processors.add(chatMimic);

		final ITransformer<ChatMessage, ChatMessage> pipe = new TransformerPipe<ChatMessage>(processors);

		return new ITransformer<ChatMessage, String>() {
			@Override
			public String transform(final ChatMessage in) {
				final ChatMessage message = pipe.transform(in);
				return (message == null) ? "" : message.getMessage();
			}
		};
	}

	private static IInherentBotProcessor getInherentProcessor(final ChatMimic mimic, final IChatDatastore datastore) {
		final List<IInherentBotProcessor> processors = new ArrayList<>();
		processors.add(new ConcreteTimedMessageBotProcessor("Wait", 70, 30));
		processors.add(new ChatMimicTimedMessageBotProcessor(mimic, datastore, 15, 15));
		processors.add(new CaveBobberTimedMessageBotProcessor(datastore, 20, 30));

		return new InherentBotProcessorPipe(processors);
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
