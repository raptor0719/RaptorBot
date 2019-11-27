package raptor.bot.utils.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import raptor.bot.api.chat.IChatDatastore;

public class FileChatDatastore implements IChatDatastore {
	private static final String CHATLOG_FILE_NAME = "chatlog.txt";

	private final String chatLogFilePath;

	public FileChatDatastore(final String dirPath) {
		this.chatLogFilePath = Paths.get(dirPath, CHATLOG_FILE_NAME).toString();
		if (!checkDirExists(dirPath))
			throw new RuntimeException(String.format("The specified directory %s for the chat datastore did not exist.", dirPath));
		try {
			checkAndCreateFile(chatLogFilePath);
		} catch (IOException e) {
			throw new RuntimeException("Error creating chat datastore chatlog file.");
		}
	}

	@Override
	public void storeMessage(final String channel, final String user, final String message, final long timestamp) {
		appendLineToFile(String.format("%s %s %s %s", channel, user, timestamp, escape(message)), chatLogFilePath);
	}

	@Override
	public int getTotalMessageCount() {
		final File chatLog = new File(chatLogFilePath);

		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(chatLog));

			int total = 0;
			String line = reader.readLine();
			while (line != null) {
				total++;
				line = reader.readLine();
			}
			return total;
		} catch (Throwable t) {
			System.err.println("Error reading chat datastore chatlog: " + t.getMessage());
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				/* IGNORE FAILURE */
			}
		}

		return -1;
	}

	private void appendLineToFile(final String line, final String filePath) {
		final File file = new File(filePath);

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write(line);
			writer.newLine();
			writer.flush();
		} catch (Throwable t) {
			System.err.println("Error writing to file: " + t.getMessage());
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				/* IGNORE FAILURE */
			}
		}
	}

	private String escape(final String message) {
		return message.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
	}

	private boolean checkDirExists(final String dirPath) {
		final File dir = new File(dirPath);
		return dir.exists() && dir.isDirectory();
	}

	private void checkAndCreateFile(final String filePath) throws IOException {
		final File chatLog = new File(filePath);

		if (!chatLog.exists())
			chatLog.createNewFile();
	}
}
