package raptor.bot.api;

import raptor.bot.api.message.IMessageSender;

public interface IBotProcessor<I> {
	boolean process(I input, IMessageSender<String> sender);
}
