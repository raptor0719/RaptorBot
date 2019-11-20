package raptor.bot.api;

public interface IParser<T> {
	T parse(String s);
}
