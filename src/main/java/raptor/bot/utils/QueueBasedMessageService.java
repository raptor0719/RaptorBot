package raptor.bot.utils;

import java.util.Iterator;
import java.util.Queue;

import raptor.bot.api.IMessageService;

public class QueueBasedMessageService<R, S> implements IMessageService<R, S> {
	private final Queue<R> receiverQueue;
	private final Queue<S> senderQueue;

	public QueueBasedMessageService(final Queue<R> receiverQueue, final Queue<S> senderQueue) {
		this.receiverQueue = receiverQueue;
		this.senderQueue = senderQueue;
	}

	@Override
	public Iterator<R> receiveMessages() {
		return new RemovingIteratorWrapper<>(receiverQueue.iterator());
	}

	@Override
	public void sendMessage(final S message) {
		senderQueue.add(message);
	}
}
