package raptor.bot.api.message;

import java.util.Iterator;

public interface IMessageReceiver<R> {
	Iterator<R> receiveMessages();
}
