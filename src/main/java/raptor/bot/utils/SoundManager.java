package raptor.bot.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import raptor.bot.api.ISoundManager;

public class SoundManager implements ISoundManager<String> {
	private final Map<String, String> sounds;

	public SoundManager(final Map<String, String> sounds) {
		this.sounds = sounds;
	}

	@Override
	public InputStream getSound(final String keyword) {
		if (exists(keyword)) {
			final File f = new File(sounds.get(keyword));
			try {
				return new BufferedInputStream(new FileInputStream(f));
			} catch (Throwable t) {
				System.out.println(String.format("The sound %s was not found.",	keyword));
			}
		}

		return null;
	}

	@Override
	public boolean exists(final String keyword) {
		return sounds.containsKey(keyword);
	}

	@Override
	public Iterable<String> getKeys() {
		return sounds.keySet();
	}
}
