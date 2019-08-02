package raptor.bot.irc.message;

import java.util.regex.Pattern;

import raptor.bot.irc.message.messages.ClientMessage;
import raptor.bot.irc.message.messages.IrcMessage;
import raptor.bot.irc.message.messages.JoinDeclarationMessage;
import raptor.bot.irc.message.messages.ModeDeclarationMessage;
import raptor.bot.irc.message.messages.NoticeMessage;
import raptor.bot.irc.message.messages.NumericServerReplyMessage;
import raptor.bot.irc.message.messages.PingMessage;

public class IrcMessageParser {
	public static IrcMessage parseIrcMessage(final String message) {
		final MessageInfo info = parseMessage(message);

		if (info.remainingMessage.startsWith(":")) {
			info.remainingMessage = info.remainingMessage.substring(1);
			info.payload = info.remainingMessage;
		} else {
			info.payload = "";
		}

		switch (info.type) {
			case Ping:
				return new PingMessage(info.payload);
			case Notice:
				return new NoticeMessage(info.payload, info.source);
			case Join:
				return new JoinDeclarationMessage(info.payload, info.source);
			case Mode:
				return new ModeDeclarationMessage(info.payload, info.source, info.nick);
			case Client:
				return new ClientMessage(info.payload, info.source, info.channel);
			case NumericReply:
				return new NumericServerReplyMessage(info.payload, info.source, info.responseCode, info.nick);
			default:
				throw new RuntimeException("Unknown message type encountered. Message was: " + info.originalMessage);
		}
	}

	private static MessageInfo parseMessage(final String message) {
		final MessageInfo info = new MessageInfo(message);
		if (info.remainingMessage.startsWith("PING")) {
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(' ') + 1);
			info.type = MessageType.Ping;
			return info;
		}

		return parseSourcedMessage(info);
	}

	private static MessageInfo parseSourcedMessage(final MessageInfo info) {
		parseSource(info);
		parseMessageInfo(info);
		return info;
	}

	private static MessageInfo parseSource(final MessageInfo info) {
		if (!info.remainingMessage.startsWith(":"))
			throw new RuntimeException("Expected ':' character. Message was: " + info.originalMessage);
		info.remainingMessage = info.remainingMessage.substring(1);
		info.source = parseName(info);
		return info;
	}

	private static MessageInfo parseMessageInfo(final MessageInfo info) {
		if (info.remainingMessage.startsWith("NOTICE")) {
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(' ') + 1);
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(' ') + 1);
			info.type = MessageType.Notice;
			return info;
		}

		if (info.remainingMessage.startsWith("PRIVMSG")) {
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(' ') + 1);

			final String channel = parseChannel(info);
			info.channel = channel;

			info.type = MessageType.Client;
			return info;
		}

		if (info.remainingMessage.startsWith("MODE")) {
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(' ') + 1);

			final String name = parseName(info);
			info.nick = name;

			info.type = MessageType.Mode;
			return info;
		}

		if (info.remainingMessage.startsWith("JOIN")) {
			info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(' ') + 1);
			info.type = MessageType.Join;
			return info;
		}

		return parseNumericReply(info);
	}

	private static MessageInfo parseNumericReply(final MessageInfo info) {
		final String responseCode = info.remainingMessage.split(" ")[0];
		info.remainingMessage = info.remainingMessage.substring(responseCode.length() + 1);
		info.responseCode = responseCode;

		final String nick = parseName(info);
		info.nick = nick;

		switch (info.responseCode) {
			case "001":

		}

		info.remainingMessage = info.remainingMessage.substring(info.remainingMessage.indexOf(" :") + 1);

		info.type = MessageType.NumericReply;

		return info;
	}

	private static String parseName(final MessageInfo info) {
		final String name = info.remainingMessage.split(" ")[0];
		info.remainingMessage = info.remainingMessage.substring(name.length() + 1);
		if (!Pattern.matches("[a-zA-Z0-9!@._]*", name))
			throw new RuntimeException("Expected name. Message was: " + info.originalMessage);
		return name;
	}

	private static String parseChannel(final MessageInfo info) {
		final String channel = info.remainingMessage.split(" ")[0];
		info.remainingMessage = info.remainingMessage.substring(channel.length() + 1);
		if (!Pattern.matches("[&#!+][a-zA-Z0-9.]*", channel))
			throw new RuntimeException("Expected channel. Message was: " + info.originalMessage);
		return channel;
	}

	private static class MessageInfo {
		public String originalMessage;
		public String remainingMessage;
		public MessageType type = MessageType.UNKNOWN;
		public String payload;
		public String source;
		public String responseCode;
		public String nick;
		public String channel;

		public MessageInfo(final String message) {
			this.originalMessage = message;
			this.remainingMessage = message;
		}
	}

	private static enum MessageType {
		Ping,
		NumericReply,
		Client,
		Notice,
		Mode,
		Join,
		UNKNOWN
	}
}
