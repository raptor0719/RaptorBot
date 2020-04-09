package raptor.bot.utils;

public class SingleItemIterator<T> extends InfiniteIterator<T> {
	private final T item;

	public SingleItemIterator(final T item) {
		this.item = item;
	}

	@Override
	public T next() {
		return item;
	}

}
