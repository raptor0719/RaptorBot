package raptor.bot.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import raptor.bot.api.IAliasManager;

public class AliasManager implements IAliasManager {
	private final String filePath;
	private Map<String, String> aliasMap;

	public AliasManager(final String filePath) {
		this.filePath = filePath;
		this.aliasMap = Collections.unmodifiableMap(readAliasMappings(filePath));
	}

	@Override
	public void create(final String alias, final String phrase) {
		final Map<String, String> modifiableMap = new HashMap<String, String>(aliasMap);
		modifiableMap.put(alias, phrase);
		aliasMap = Collections.unmodifiableMap(modifiableMap);
		writeAliasMappings(filePath, aliasMap);
		System.out.println("AliasManager - create - new alias '" + alias + "' was create for phrase: " + phrase);
	}

	@Override
	public void delete(final String alias) {
		final Map<String, String> modifiableMap = new HashMap<String, String>(aliasMap);
		modifiableMap.remove(alias);
		aliasMap = Collections.unmodifiableMap(modifiableMap);
		writeAliasMappings(filePath, aliasMap);
		System.out.println("AliasManager - delete - alias '" + alias + "' was deleted.");
	}

	@Override
	public boolean isAlias(final String alias) {
		return aliasMap.containsKey(alias);
	}

	@Override
	public String getAliasedPhrase(final String alias) {
		return aliasMap.get(alias);
	}

	@Override
	public Iterable<String> getAliases() {
		return aliasMap.keySet();
	}

	private Map<String, String> readAliasMappings(final String filePath) {
		final File file = new File(filePath);

		FileInputStream fis = null;
		try {
			final Properties props = new Properties();

			fis = new FileInputStream(file);
			props.load(fis);

			final Map<String, String> aliasMap = new HashMap<String, String>();
			for (final Map.Entry<Object, Object> e : props.entrySet())
				aliasMap.put((String)e.getKey(), (String)e.getValue());

			return aliasMap;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				fis.close();
			} catch (Throwable t) {
				/* IGNORE FAILURES */
			}
		}
	}

	private void writeAliasMappings(final String filePath, final Map<String, String> aliasMap) {
		final File file = new File(filePath);
		BufferedWriter writer = null;
		try {
			if (file.exists())
				file.delete();
			if (!file.createNewFile())
				throw new IOException("AliasManager - readAliasMappings - Alias mapping file could not be created.");
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			for (final Map.Entry<String, String> e : aliasMap.entrySet()) {
				writer.write(e.getKey() + " = " + e.getValue() + "\n");
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (Throwable t) {}
		}
	}
}
