package raptor.bot.chatter.processors;

import java.util.Iterator;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.irc.ChatMessage;

public class CaveBobberTimedMessageBotProcessor extends TimedMessageBotProcessor {
	private final IChatDatastore datastore;

	public CaveBobberTimedMessageBotProcessor(final IChatDatastore datastore, final int interval) {
		super(interval);
		this.datastore = datastore;
	}

	public CaveBobberTimedMessageBotProcessor(final IChatDatastore datastore, final int min, final int range) {
		super(min, range);
		this.datastore = datastore;
	}

	@Override
	public String getMessage() {
		final Iterator<ChatMessage> lastMessage = datastore.getLastMessages(1);

		if (!lastMessage.hasNext())
			return null;

		return String.format("\"%s\" caveBob", lastMessage.next().getMessage());
	}
}
