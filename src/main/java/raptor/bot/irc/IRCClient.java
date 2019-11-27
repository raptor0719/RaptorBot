package raptor.bot.irc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import raptor.bot.irc.message.messages.ClientMessage;
import raptor.bot.irc.message.messages.IrcMessage;
import raptor.bot.irc.message.messages.NumericServerReplyMessage;
import raptor.bot.irc.message.messages.PingMessage;

public class IRCClient {
	private final String address;
	private final int port;

	private final String username;
	private final String nickname;
	private final String password;

	private Map<String, List<ChatMessage>> chatMessages;

	private IRCConnection connection;

	public IRCClient(final String address, final int port, final String username, final String nickname) {
		this(address, port, username, nickname, null);
	}

	public IRCClient(final String address, final int port, final String username, final String nickname, final String password) {
		this.address = address;
		this.port = port;
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.chatMessages = new HashMap<String, List<ChatMessage>>();
	}

	public void connect() throws UnknownHostException, IOException {
		// Default wait time is 30 seconds
		connect(30000L);
	}

	public void connect(final long timeout) throws UnknownHostException, IOException {
		connection = new IRCConnection(address, port);

		if (connection.isConnected()) {
			if (password != null)
				connection.pass(password);

			connection.user(username, "RaptorBot.IrcClient");
			connection.nick(nickname);

			final long currentTime = System.currentTimeMillis();
			long passedTime = System.currentTimeMillis() - currentTime;

			while (passedTime < timeout) {
				final Iterator<IrcMessage> messages = connection.getServerMessages();
				while (messages.hasNext()) {
					final IrcMessage message = messages.next();
					System.out.println("IRCClient - connect - " + message);
					if (message instanceof PingMessage) {
						connection.pong(((PingMessage)message).getPayload());
						return;
					} else if (message instanceof NumericServerReplyMessage) {
						final NumericServerReplyMessage reply = (NumericServerReplyMessage)message;
						if ("001".equals(reply.getResponseCode())) {
							return;
						}
					}
				}
				passedTime = System.currentTimeMillis() - currentTime;
			}
			throw new RuntimeException("Timeout occured while waiting for server response.");
		}
		throw new RuntimeException("Connection was not able to successfully connect.");
	}

	public void process() {
		try {
			final Iterator<IrcMessage> messages = connection.getServerMessages();

			while (messages.hasNext()) {
				final IrcMessage message = messages.next();

				if (message instanceof ClientMessage) {
					final ClientMessage clientMessage = (ClientMessage)message;
					if (chatMessages.containsKey(clientMessage.getChannel())) {
						chatMessages.get(clientMessage.getChannel()).add(new ChatMessage(clientMessage.getChannel(), clientMessage.getSource(), clientMessage.getPayload()));
					}
				} else if (message instanceof PingMessage) {
					final PingMessage ping = (PingMessage)message;
					connection.pong(ping.getPayload());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Iterator<ChatMessage> getMessages(final String channel) {
		final List<ChatMessage> messages = chatMessages.get(channel);
		chatMessages.put(channel, new ArrayList<ChatMessage>());
		return messages.iterator();
	}

	public void sendMessage(final String message, final String channel) {
		connection.privmsg(channel, message);
	}

	public void joinChannel(final String channel) {
		connection.join(channel);
		chatMessages.put(channel, new ArrayList<ChatMessage>());
	}

	public void leaveChannel(final String channel) {
		connection.part(channel);
		chatMessages.remove(channel);
	}
}
