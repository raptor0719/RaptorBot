package raptor.bot.command.commands.madlib;

public class MadlibFillCommand extends MadlibCommand {
	public static final String SUB_COMMAND_WORD = "fill";

	private final String phrase;

	public MadlibFillCommand(final String phrase) {
		super(SUB_COMMAND_WORD);
		this.phrase = phrase;
	}

	public String getPhrase() {
		return phrase;
	}
}
