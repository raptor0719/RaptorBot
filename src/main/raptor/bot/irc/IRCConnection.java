package raptor.bot.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import raptor.bot.irc.message.IrcMessageParser;
import raptor.bot.irc.message.messages.IrcMessage;

public class IRCConnection {
	private final Socket tcpConnection;
	private final BufferedReader serverMessageStream;
	private final BufferedWriter clientMessageStream;

	public IRCConnection(final String address, final int port) throws UnknownHostException, IOException {
		tcpConnection = new Socket(address, port);
		serverMessageStream = new BufferedReader(new InputStreamReader(tcpConnection.getInputStream()));
		clientMessageStream = new BufferedWriter(new OutputStreamWriter(tcpConnection.getOutputStream()));
	}

	public void user(final String username, final String realName) {
		sendMessage("USER %s * * :%s\n", username, realName);
	}

	public void nick(final String nickname) {
		sendMessage("NICK %s\n", nickname);
	}

	public void pong(final String pingVal) {
		sendMessage("PONG :%s\n", pingVal);
	}

	public void join(final String... channels) {
		sendMessage("JOIN %s\n", String.join(",", channels));
	}

	public void part(final String... channels) {
		sendMessage("PART %s\n", String.join(",", channels));
	}

	public void privmsg(final String target, final String message) {
		sendMessage("PRIVMSG", target, message);
	}

	public Iterator<IrcMessage> getServerMessages() throws IOException {
		final List<IrcMessage> serverMessages = new LinkedList<>();

		// readLine blocks, so make sure it won't block before reading data
		String message;
		while (serverMessageStream.ready()) {
			message = serverMessageStream.readLine();
			serverMessages.add(IrcMessageParser.parseIrcMessage(message));
		}

		return serverMessages.iterator();
	}

	public boolean isConnected() {
		return tcpConnection.isConnected();
	}

	private void sendMessage(final String commandFormat, final Object... args) {
		final String message = String.format(commandFormat, args);
		try {
			clientMessageStream.write(message);
			clientMessageStream.flush();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
