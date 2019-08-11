package raptor.bot.command.commands.alias;

public class AliasDeleteCommand extends AliasCommand {
	public static final String SUB_COMMAND_WORD = "delete";
	private final String alias;

	public AliasDeleteCommand(final String alias) {
		super(SUB_COMMAND_WORD);
		this.alias = alias;
	}

	public String getAlias() {
		return alias;
	}
}
