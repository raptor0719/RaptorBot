package raptor.bot.api;

public interface IAliasManager {
	void create(String alias, String phrase);
	void delete(String alias);
	boolean isAlias(String word);
	String getAliasedPhrase(String alias);
	Iterable<String> getAliases();
}
