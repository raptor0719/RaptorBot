package raptor.bot.utils;

import java.util.Iterator;

public class RemovingIteratorWrapper<E> implements Iterator<E> {

	private final Iterator<E> iter;

	public RemovingIteratorWrapper(final Iterator<E> iter) {
		this.iter = iter;
	}

	@Override
	public boolean hasNext() {
		return iter.hasNext();
	}

	@Override
	public E next() {
		final E next = iter.next();
		iter.remove();
		return next;
	}
}
