package raptor.bot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import raptor.bot.api.IMemeManager;

public class MemeManager implements IMemeManager {
	private final Map<String, MemeInfo> memes;

	public MemeManager(final String memeConfigPath) {
		this.memes = readMemeProperties(memeConfigPath);
		verifyMemesValid();
	}

	@Override
	public String getMemeFile(final String meme) {
		if (!exists(meme))
			return null;
		return memes.get(meme).filePath;
	}

	@Override
	public long getMemeLength(final String meme) {
		if (!exists(meme))
			return 0;
		return memes.get(meme).length;
	}

	@Override
	public boolean exists(final String meme) {
		return memes.containsKey(meme);
	}

	@Override
	public Iterable<String> getMemes() {
		return memes.keySet();
	}

	private Map<String, MemeInfo> readMemeProperties(final String propPath) {
		final File memePropsFile = new File(propPath);
		final String parentPath = memePropsFile.getParentFile().getAbsolutePath();

		FileInputStream fis = null;
		try {
			final Properties memeProps = new Properties();

			fis = new FileInputStream(memePropsFile);
			memeProps.load(fis);

			final Map<String, MemeInfo> memesMap = new HashMap<String, MemeInfo>();
			for (final Map.Entry<Object, Object> e : memeProps.entrySet()) {
				final String key = (String)e.getKey();
				if (key.endsWith(".length"))
					continue;
				if (!memeProps.containsKey(key + ".length")) {
					System.out.println(String.format("The meme %s did not have a length specified.", key));
					continue;
				}
				final String path = Paths.get(parentPath, (String)e.getValue()).toString();
				final long length = Long.parseLong(memeProps.getProperty(key + ".length"));
				memesMap.put(key, new MemeInfo(path, length));
			}

			return memesMap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new HashMap<String, MemeInfo>();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Throwable t) {/* IGNORE FAILURES */}
		}
	}

	private boolean verifyMemesValid() {
		boolean allExist = true;
		final List<String> keysToRemove = new ArrayList<>();
		for (final Map.Entry<String, MemeInfo> e : memes.entrySet()) {
			final File soundFile = new File(e.getValue().filePath);
			if (!soundFile.exists() || soundFile.isDirectory()) {
				allExist = false;
				keysToRemove.add(e.getKey());
				System.err.println(String.format("The '%s' meme's file '%s' was not found. It is being removed from the list of available memes.", e.getKey(), e.getValue().filePath));
			} else if (e.getValue().length < 1) {
				System.err.println(String.format("The '%s' memes's length was 0 or less. It is being removed from the list of available sounds.", e.getKey()));
			}
		}
		for (final String key : keysToRemove)
			memes.remove(key);
		return allExist;
	}

	protected static class MemeInfo {
		public final String filePath;
		public final long length;

		public MemeInfo(final String fileName, final long length) {
			this.filePath = fileName;
			this.length = length;
		}
	}
}
