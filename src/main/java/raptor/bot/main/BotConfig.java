package raptor.bot.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Properties;

public class BotConfig {
	private final long botMessageCooldown;

	private final String aliasFilePath;
	private final String soundsFilePath;
	private final String dictionaryFilePath;

	private final boolean playSoundsConcurrently;

	private final String memesFilePath;
	private final String memesActiveFilePath;

	private final String ircIp;
	private final int ircPort;
	private final String ircUser;
	private final String ircPassword;
	private final String ircChannel;

	private final boolean compileChatMimicDictionary;

	private final boolean enableChatDatastoreSql;
	private final String chatDatastoreSqlConnectionURL;
	private final String chatDatastoreSqlSchema;
	private final String chatDatastoreSqlTable;

	private final boolean enableChatDatastoreFile;
	private final String chatDatastoreFileDirectoryPath;

	public BotConfig(final InputStream configStream) throws IOException {
		final Properties props = new Properties();
		props.load(configStream);

		botMessageCooldown = Math.max(Long.parseLong(props.getProperty("bot-message-cooldown", "1000")), 1000L);

		aliasFilePath = props.getProperty("alias-file", "aliases.txt");
		soundsFilePath = props.getProperty("sounds-file", Paths.get("sounds", "sounds.properties").toString());
		dictionaryFilePath = props.getProperty("dictionary-file", "dictionary.txt");

		playSoundsConcurrently = Boolean.parseBoolean(props.getProperty("play-sounds-concurrently", "true"));

		memesFilePath = props.getProperty("memes-file", Paths.get("memes", "memes.properties").toString());
		memesActiveFilePath = props.getProperty("memes-active-file", "current.gif");

		ircIp = checkExistsOrError("irc-ip", props);
		ircPort = Integer.parseInt(checkExistsOrError("irc-port", props));
		ircUser = checkExistsOrError("irc-user", props);
		ircPassword = readIrcPassword(checkExistsOrError("irc-password-file", props));
		ircChannel = checkExistsOrError("irc-channel", props);

		compileChatMimicDictionary = Boolean.parseBoolean(props.getProperty("compile-chat-mimic-dictionary", "false"));

		enableChatDatastoreFile = Boolean.parseBoolean(props.getProperty("enable-chatDatastore-file", "false"));
		chatDatastoreFileDirectoryPath = (enableChatDatastoreFile) ? props.getProperty("chatDatastore-file-dirPath") : null;

		enableChatDatastoreSql = Boolean.parseBoolean(props.getProperty("enable-chatDatastore-sql", "false"));
		chatDatastoreSqlConnectionURL = (enableChatDatastoreSql) ? props.getProperty("chatDatastore-sql-connectionUrl") : null;
		chatDatastoreSqlSchema = (enableChatDatastoreSql) ? props.getProperty("chatDatastore-sql-schema") : null;
		chatDatastoreSqlTable = (enableChatDatastoreSql) ? props.getProperty("chatDatastore-sql-table") : null;
	}

	public long getBotMessageCooldown() {
		return botMessageCooldown;
	}

	public String getAliasFilePath() {
		return aliasFilePath;
	}

	public String getSoundsFilePath() {
		return soundsFilePath;
	}

	public String getDictionaryFilePath() {
		return dictionaryFilePath;
	}

	public boolean isPlaySoundsConcurrently() {
		return playSoundsConcurrently;
	}

	public String getMemeFilePath() {
		return memesFilePath;
	}

	public String getMemeActiveFilePath() {
		return memesActiveFilePath;
	}

	public String getIrcIp() {
		return ircIp;
	}

	public int getIrcPort() {
		return ircPort;
	}

	public String getIrcUser() {
		return ircUser;
	}

	public String getIrcPassword() {
		return ircPassword;
	}

	public String getIrcChannel() {
		return ircChannel;
	}

	public boolean isCompileChatMimicDictionary() {
		return compileChatMimicDictionary;
	}

	public boolean isEnableChatDatastoreSql() {
		return enableChatDatastoreSql;
	}

	public String getChatDatastoreSqlConnectionURL() {
		return chatDatastoreSqlConnectionURL;
	}

	public String getChatDatastoreSqlSchema() {
		return chatDatastoreSqlSchema;
	}

	public String getChatDatastoreSqlTable() {
		return chatDatastoreSqlTable;
	}

	public boolean isEnableChatDatastoreFile() {
		return enableChatDatastoreFile;
	}

	public String getChatDatastoreFileDirectoryPath() {
		return chatDatastoreFileDirectoryPath;
	}

	private String checkExistsOrError(final String propName, final Properties props) {
		final String prop = props.getProperty(propName);
		if (prop == null || prop.trim().isEmpty())
			throw new RuntimeException(String.format("The property %s must be defined.", propName));
		return prop;
	}

	private String readIrcPassword(final String passwordFilePath) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(passwordFilePath)));
			return reader.readLine();
		} catch (final IOException e) {
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable t) {
					/* IGNORE FAILURES */
				}
			}
		}
	}
}
