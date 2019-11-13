package raptor.bot.api;

import java.io.InputStream;

public interface ISoundManager<K> {
	InputStream getSound(K key);
	boolean exists(K key);
	Iterable<String> getKeys();
}
