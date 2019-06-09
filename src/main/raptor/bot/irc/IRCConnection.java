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

import raptor.bot.irc.message.IRCServerMessage;

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

	public Iterator<IRCServerMessage> getServerMessages() throws IOException {
		final List<IRCServerMessage> serverMessages = new LinkedList<>();

		String message = serverMessageStream.readLine();
		while (message != null) {
			serverMessages.add(new IRCServerMessage(message));
		}

		return serverMessages.iterator();
	}

	private void sendMessage(final String commandFormat, final Object... args) {
		try {
			clientMessageStream.write(String.format(commandFormat, args));
			clientMessageStream.flush();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
