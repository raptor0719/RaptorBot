package raptor.bot.main;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Iterator;

import raptor.bot.irc.ChatMessage;
import raptor.bot.irc.IRCClient;

public class Main {
	public static void main(String[] args) {
		final IRCClient client = new IRCClient("192.168.1.6", 6667, "raptorbot", "RaptorBot");
		try {
			client.connect();
			client.joinChannel("#general");

			while (true) {
				client.process();

				final Iterator<ChatMessage> messages = client.getMessages("#general");
				while (messages.hasNext()) {
					final ChatMessage message = messages.next();
					System.out.println(message.getUser() + ": " + message.getMessage());
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
