package raptor.bot.api;

public interface IMemeManager {
	String getMemeFile(String meme);
	long getMemeLength(String meme);
	boolean exists(String meme);
	Iterable<String> getMemes();
}
