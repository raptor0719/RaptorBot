package raptor.bot.chatter.processors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.chatter.mimic.ChatMimic;
import raptor.bot.irc.ChatMessage;

public class ChatMimicTimedMessageBotProcessor extends TimedMessageBotProcessor {
	private final ChatMimic mimic;
	private final IChatDatastore datastore;

	public ChatMimicTimedMessageBotProcessor(final ChatMimic mimic, final IChatDatastore datastore, int interval) {
		super(interval);
		this.mimic = mimic;
		this.datastore = datastore;
	}

	public ChatMimicTimedMessageBotProcessor(final ChatMimic mimic, final IChatDatastore datastore, int min, int range) {
		super(min, range);
		this.mimic = mimic;
		this.datastore = datastore;
	}

	@Override
	public String getMessage() {
		final List<String> lines = new ArrayList<>();
		final Iterator<ChatMessage> iter = datastore.getLastMessages(30);
		while (iter.hasNext())
			lines.add(iter.next().getMessage());
		return mimic.mimic(lines);
	}
}