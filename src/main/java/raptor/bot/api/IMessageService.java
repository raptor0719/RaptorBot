package raptor.bot.api;

import java.util.Iterator;

public interface IMessageService<R, S> {
	Iterator<R> receiveMessages();
	void sendMessage(S message);
}
