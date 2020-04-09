package raptor.bot.utils;

import java.util.List;

public class UniformRandomItemIterator<T> extends InfiniteIterator<T> {
	private final List<T> items;

	public UniformRandomItemIterator(final List<T> items) {
		this.items = items;
	}

	@Override
	public T next() {
		return items.get((int)(Math.random() * (items.size())));
	}
}
