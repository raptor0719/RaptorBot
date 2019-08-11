package raptor.bot.command;

public enum CommandWords {
	SOUND("sound"),
	HELP("help");

	private final String word;

	private CommandWords(final String word) {
		this.word = word;
	}

	public String getWord() {
		return word;
	}
}
