package raptor.bot.chatter.mimic.chat.processor;

import java.util.ArrayList;
import java.util.List;

import raptor.bot.api.IChatProcessor;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.chatter.mimic.ChatMimic;
import raptor.bot.irc.ChatMessage;

public class ChatMimicChatProcessor implements IChatProcessor {
	private final ChatMimic mimic;
	private final String botName;

	public ChatMimicChatProcessor(final ChatMimic mimic, final String botName) {
		this.mimic = mimic;
		this.botName = botName;
	}

	@Override
	public boolean process(final ChatMessage message, final IMessageSender<String> sender) {
		if (!message.getMessage().contains("@" + botName))
			return false;

		final List<String> lines = new ArrayList<>();
		lines.add(message.getMessage());
		final String response = mimic.mimic(lines);

		sender.sendMessage(response);

		return true;
	}
}
