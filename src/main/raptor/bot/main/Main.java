package raptor.bot.main;

import java.io.IOException;
import java.net.UnknownHostException;

import raptor.bot.irc.IRCClient;

public class Main {
	public static void main(String[] args) {
		final IRCClient client = new IRCClient("192.168.1.6", 6667);
		try {
			client.connect();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
