package raptor.bot.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import raptor.bot.api.ITransformer;
import raptor.bot.irc.ChatMessage;
import raptor.bot.irc.IRCClient;
import raptor.bot.utils.AliasManager;
import raptor.bot.utils.SoundManager;
import raptor.bot.utils.TransformerPipe;

public class Main {
	private static final Map<String, String> sounds;
	private static final String aliasFile = "C:\\Users\\short\\Documents\\RaptorBot\\aliases.txt";

	static {
		final String soundDir = "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\";
		sounds = new HashMap<String, String>();

		sounds.put("ynot", soundDir + "WC3-Peon-WhyNot.wav");
		sounds.put("imdead", soundDir + "WC3-Peasant-ImDead.wav");
		sounds.put("nice", soundDir + "WC3-Grunt-KindOfNice.wav");
		sounds.put("death", soundDir + "WC3-BloodMage-Dead.wav");
		sounds.put("engineer", soundDir + "WC3-BelfEngineer-ImAnEngineer.wav");
		sounds.put("wanderer", soundDir + "WC3-Bandit-RoamerWanderer.wav");
		sounds.put("jobsdone", soundDir + "WC3-Peasant-JobsDone.wav");
		sounds.put("taunt", soundDir + "WC3-Muradin-BestYouCanDo.wav");

		sounds.put("moredots", soundDir + "OWA-MoreDotsMoreDots.wav");
		sounds.put("dotsnow", soundDir + "OWA-MoreDotsNow.wav");
		sounds.put("stopdots", soundDir + "OWA-StopDots.wav");
		sounds.put("whodafuq", soundDir + "OWA-WhoTheWasThat.wav");
		sounds.put("wtf", soundDir + "OWA-WTF.wav");
		sounds.put("wtfshit", soundDir + "OWA-WTFShit.wav");
		sounds.put("cmere", soundDir + "OWA-ComeHere.wav");
		sounds.put("eff", soundDir + "OWA-Fuck.wav");

		sounds.put("godlike", soundDir + "DOTA-Godlike.wav");
		sounds.put("holyshit", soundDir + "DOTA-HolyShit.wav");
		sounds.put("monsterkill", soundDir + "DOTA-MonsterKill.wav");
		sounds.put("ultrakill", soundDir + "DOTA-UltraKill.wav");

		sounds.put("chickens", soundDir + "MYTH2-ChickensAttack.wav");
		sounds.put("doing", soundDir + "MYTH2-Dwarf-DOOOIIINNNGGG.wav");
		sounds.put("weedoggie", soundDir + "MYTH2-Dwarf-WeeDoggie.wav");
		sounds.put("casualty", soundDir + "MYTH2-Casualty.wav");

		sounds.put("321", soundDir + "TS3-321GO.wav");
		sounds.put("yousuck", soundDir + "TS3-YouSuck.wav");

		sounds.put("casual", soundDir + "Casual.wav");
		sounds.put("freshmeat", soundDir + "DIABLO1-FreshMeat.wav");
		sounds.put("hax", soundDir + "IdiotBox-Hax.wav");
		sounds.put("victory", soundDir + "FF-Victory.wav");
		sounds.put("fatality", soundDir + "MK-Fatality.wav");
		sounds.put("herb", soundDir + "AOE2-HerbLaugh.wav");
	}

	public static void main(String[] args) {
		final String ip = "irc.chat.twitch.tv";
		final int port = 6667;
		final String user = "thequarterbot";
		final String nick = user;
//		final String channel = "#thequarterbot";
		final String channel = "#raptor0719";

		final String password = getOathToken();
//		final IRCClient client = new IRCClient(ip, port, user, nick);
		final IRCClient client = new IRCClient(ip, port, user, nick, password);
		final RaptorBot bot = new RaptorBot(new SoundManager(sounds), new AliasManager(aliasFile), getChatProcessor());

		final long messageDelay = 1000L;
		long lastMessageTime = 0L;

		try {
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
					final String botResponse = bot.message(message);
					if (botResponse != null && !botResponse.isEmpty() && (System.currentTimeMillis() - lastMessageTime) >= messageDelay) {
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

	private static ITransformer<ChatMessage, String> getChatProcessor() {
		final ITransformer<ChatMessage, ChatMessage> wombatGreeter = new ITransformer<ChatMessage, ChatMessage>() {
			final long timeBetweenGreetings = 3600000L;
			long lastGreeting = -1;
			@Override
			public ChatMessage transform(final ChatMessage in) {
				if (in == null)
					return null;

				if (in.getUser().toLowerCase().contains("wombat") && ((System.currentTimeMillis() - lastGreeting) >= timeBetweenGreetings || lastGreeting < 0)) {
					lastGreeting = System.currentTimeMillis();
					return new ChatMessage(null, "Welcome to the chat room!");
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

	private static String getOathToken() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("C:\\Users\\short\\Documents\\RaptorBot\\oauthtoken.txt"));
			return reader.readLine();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {}
		}
	}
}
