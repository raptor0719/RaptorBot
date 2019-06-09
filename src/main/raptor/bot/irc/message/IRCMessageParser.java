package raptor.bot.irc.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class IRCMessageParser {
	public static IRCServerMessage parseMessage(final String message) {
		final Iterator<String> tokens = tokenize(message);
		return null;
	}

	private static Iterator<String> tokenize(final String message) {
		return new ArrayList<String>(Arrays.asList(message.split(" "))).iterator();
	}
}
