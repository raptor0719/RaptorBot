package raptor.bot.api;

import raptor.bot.api.message.IMessageSender;

public interface IInherentBotProcessor {
	boolean process(IMessageSender<String> sender);
}
