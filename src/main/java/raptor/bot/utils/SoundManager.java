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
	private final Map<String, InfiniteIterator<String>> sounds;

	public SoundManager(final String soundsConfigPath) {
		this.sounds = readSoundProperties(soundsConfigPath);
	}

	@Override
	public InputStream getSound(final String keyword) {
		if (exists(keyword)) {
			final File f = new File(sounds.get(keyword).next());
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

	private Map<String, InfiniteIterator<String>> readSoundProperties(final String path) {
		final File soundPropsFile = new File(path);
		final String parentPath = soundPropsFile.getParentFile().getAbsolutePath();

		FileInputStream fis = null;
		try {
			final Properties soundProps = new Properties();

			fis = new FileInputStream(soundPropsFile);
			soundProps.load(fis);

			final Map<String, InfiniteIterator<String>> soundsMap = new HashMap<String, InfiniteIterator<String>>();
			for (final Map.Entry<Object, Object> e : soundProps.entrySet()) {
				final String key = (String)e.getKey();
				final String value = (String)e.getValue();

				if (!value.contains(",")) {
					final String soundFilePath = Paths.get(parentPath, value).toString();

					if (isValid(soundFilePath))
						soundsMap.put(key, new SingleItemIterator<String>(soundFilePath));
					else
						System.err.println(String.format("The '%s' sound's file '%s' was not found.", key, soundFilePath));

					continue;
				}

				final String[] values = value.split(",");
				final List<String> listOfSounds = new ArrayList<String>();
				for (final String s : values) {
					System.out.println(s);
					final String soundFilePath = Paths.get(parentPath, s).toString();

					if (isValid(soundFilePath))
						listOfSounds.add(soundFilePath);
					else
						System.err.println(String.format("The '%s' sound's file '%s' was not found.", key, soundFilePath));
				}

				if (listOfSounds.size() > 0)
					soundsMap.put(key, new UniformRandomItemIterator<String>(listOfSounds));
				else
					System.err.println(String.format("None of the '%s' sound's files were found so this sound is not even being added to the list.", key));
			}

			return soundsMap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new HashMap<String, InfiniteIterator<String>>();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Throwable t) {/* IGNORE FAILURES */}
		}
	}

	private boolean isValid(final String path) {
		final File file = new File(path);
		return file.exists() && !file.isDirectory();
	}
}
