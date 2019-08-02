package raptor.bot.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import raptor.bot.irc.ChatMessage;
import raptor.bot.irc.IRCClient;

public class Main {
	private static final Map<String, String> sounds;

	static {
		sounds = new HashMap<String, String>();
		sounds.put("ynot", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\WC3-Peon-WhyNot.wav");
		sounds.put("imdead", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\WC3-Peasant-ImDead.wav");
		sounds.put("nice", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\WC3-Grunt-KindOfNice.wav");
		sounds.put("death", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\WC3-BloodMage-Dead.wav");
		sounds.put("engineer", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\WC3-BelfEngineer-ImAnEngineer.wav");
		sounds.put("wanderer", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\WC3-Bandit-RoamerWanderer.wav");
		sounds.put("moredots", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-MoreDotsMoreDots.wav");
		sounds.put("dotsnow", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-MoreDotsNow.wav");
		sounds.put("stopdots", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-StopDots.wav");
		sounds.put("whodafuq", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-WhoTheWasThat.wav");
		sounds.put("wtf", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-WTF.wav");
		sounds.put("wtfshit", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-WTFShit.wav");
		sounds.put("cmere", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-ComeHere.wav");
		sounds.put("eff", "C:\\Users\\short\\Documents\\RaptorBot\\sounds\\OWA-Fuck.wav");
	}

	public static void main(String[] args) {
//		final String ip = "192.168.1.6";
		final String ip = "irc.chat.twitch.tv";
		final int port = 6667;
//		final int port = 80;
		final String user = "thequarterbot";
		final String nick = user;
//		final String channel = "#general";
//		final String channel = "#tluu";
//		final String channel = "#thequarterbot";
		final String channel = "#raptor0719";

		final String password = getOathToken();
//		final IRCClient client = new IRCClient(ip, port, user, nick);
		final IRCClient client = new IRCClient(ip, port, user, nick, password);
		final RaptorBot bot = new RaptorBot(sounds);

		final long messageDelay = 1000L;
		long lastMessageTime = 0L;

		try {
			client.connect();
			client.joinChannel(channel);

			while (true) {
				client.process();

				final Iterator<ChatMessage> messages = client.getMessages(channel);
				while (messages.hasNext()) {
					final ChatMessage message = messages.next();
					System.out.println(message.getUser() + ": " + message.getMessage());
					final String botResponse = bot.message(message.getUser(), message.getMessage());
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
