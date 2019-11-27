package raptor.bot.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import raptor.bot.api.ISoundManager;

public class SoundManager implements ISoundManager<String> {
	private final Map<String, String> sounds;

	public SoundManager(final String soundsConfigPath) {
		this.sounds = readSoundProperties(soundsConfigPath);
		verifySoundsExist();
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

	private Map<String, String> readSoundProperties(final String path) {
		final File soundPropsFile = new File(path);
		final String parentPath = soundPropsFile.getParentFile().getAbsolutePath();

		FileInputStream fis = null;
		try {
			final Properties soundProps = new Properties();

			fis = new FileInputStream(soundPropsFile);
			soundProps.load(fis);

			final Map<String, String> soundsMap = new HashMap<String, String>();
			for (final Map.Entry<Object, Object> e : soundProps.entrySet())
				soundsMap.put((String)e.getKey(), Paths.get(parentPath, (String)e.getValue()).toString());

			return soundsMap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new HashMap<String, String>();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Throwable t) {/* IGNORE FAILURES */}
		}
	}

	private boolean verifySoundsExist() {
		boolean allExist = true;
		final List<String> keysToRemove = new ArrayList<>();
		for (final Map.Entry<String, String> e : sounds.entrySet()) {
			final File soundFile = new File(e.getValue());
			if (!soundFile.exists() || soundFile.isDirectory()) {
				allExist = false;
				keysToRemove.add(e.getKey());
				System.err.println(String.format("The '%s' sound's file '%s' was not found. It is being removed from the list of available sounds.", e.getKey(), e.getValue()));
			}
		}
		for (final String key : keysToRemove)
			sounds.remove(key);
		return allExist;
	}
}
