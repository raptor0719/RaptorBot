package raptor.bot.command.commands;

import raptor.bot.command.BotMethod;

public class WisdomCommand extends BotCommand {
	public static final String COMMAND_WORD = BotMethod.WISDOM.getWord();

	private final int index;

	public WisdomCommand() {
		this(-1);
	}

	public WisdomCommand(final int index) {
		super(COMMAND_WORD);
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}
