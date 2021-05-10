package raptor.bot.api;

import raptor.bot.api.message.IMessageSender;
import raptor.bot.irc.ChatMessage;

public interface IChatProcessor {
	boolean process(ChatMessage message, IMessageSender<String> sender);
}
