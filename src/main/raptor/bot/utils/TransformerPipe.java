package raptor.bot.utils;

import java.util.Collection;

import raptor.bot.api.ITransformer;

public class TransformerPipe<T> implements ITransformer<T, T> {
	private final Collection<ITransformer<T, T>> transformers;

	public TransformerPipe(final Collection<ITransformer<T, T>> transformers) {
		this.transformers = transformers;
	}

	@Override
	public T transform(final T in) {
		T result = in;
		for (final ITransformer<T, T> t : transformers) {
			result = t.transform(result);
		}
		return result;
	}
}
