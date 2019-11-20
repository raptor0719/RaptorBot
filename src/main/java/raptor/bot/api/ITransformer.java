package raptor.bot.api;

public interface ITransformer<S, T> {
	T transform(S in);
}
