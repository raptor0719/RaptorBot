package raptor.bot.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AliasManager {
	private final String filePath;
	private Map<String, String> aliasMap;

	public AliasManager(final String filePath) {
		this.filePath = filePath;
		this.aliasMap = Collections.unmodifiableMap(readAliasMappings(filePath));
	}

	public void create(final String alias, final String phrase) {
		final Map<String, String> modifiableMap = new HashMap<String, String>(aliasMap);
		modifiableMap.put(alias, phrase);
		aliasMap = Collections.unmodifiableMap(modifiableMap);
		writeAliasMappings(filePath, aliasMap);
		System.out.println("AliasManager - create - new alias '" + alias + "' was create for phrase: " + phrase);
	}

	public void delete(final String alias) {
		final Map<String, String> modifiableMap = new HashMap<String, String>(aliasMap);
		modifiableMap.remove(alias);
		aliasMap = Collections.unmodifiableMap(modifiableMap);
		writeAliasMappings(filePath, aliasMap);
		System.out.println("AliasManager - delete - alias '" + alias + "' was deleted.");
	}

	public Map<String, String> getAliases() {
		return aliasMap;
	}

	private Map<String, String> readAliasMappings(final String filePath) {
		final File file = new File(filePath);
		BufferedReader reader = null;
		try {
			final Map<String, String> aliasMap = new HashMap<String, String>();
			if (!file.exists()) {
				System.out.println("AliasManager - readAliasMappings - Alias file did not exist. Creating a new one.");
				file.createNewFile();
				return aliasMap;
			}
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String current = reader.readLine();
			while (current != null) {
				final int firstSpace = current.indexOf(' ');
				if (firstSpace+1 < current.length()) {
					aliasMap.put(current.substring(0, firstSpace), current.substring(firstSpace+1, current.length()));
				} else {
					System.out.println("AliasManager - readAliasMappings - Invalid alias entry was encountered.");
				}
				current = reader.readLine();
			}
			return aliasMap;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Throwable t) {}
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
				writer.write(e.getKey() + " " + e.getValue());
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
