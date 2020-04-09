package raptor.bot.utils;

import java.util.Iterator;

public abstract class InfiniteIterator<T> implements Iterator<T> {
	@Override
	public boolean hasNext() {
		return true;
	}
}
