package raptor.bot.command;

import raptor.bot.command.commands.BotCommand;

public class BotCommandParser {
	public static BotCommand parseBotCommand(final String message) {
		try {
			if (message.startsWith("!")) {
				final String cleaned = message.trim().substring(1);
				final String commandWord = cleaned.split(" ")[0].toLowerCase();
				final String params = (commandWord.equals(cleaned)) ? "" : cleaned.substring(cleaned.indexOf(" ") + 1);

				for (final BotMethod b : BotMethod.values()) {
					if (b.getWord().equals(commandWord)) {
						return b.parse(params);
					}
				}

				return new BotCommand(commandWord);
			}
		} catch (Throwable t) {
			System.out.println("Error while parsing: " + t.getMessage());
		}

		return null;
	}
}
