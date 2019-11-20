package raptor.bot.command.commands.alias;

public class AliasCreateCommand extends AliasCommand {
	public static final String SUB_COMMAND_WORD = "create";
	private final String alias;
	private final String aliasedCommand;

	public AliasCreateCommand(final String alias, final String aliasedCommand) {
		super(SUB_COMMAND_WORD);
		this.alias = alias;
		this.aliasedCommand = aliasedCommand;
	}

	public String getAlias() {
		return alias;
	}

	public String getAliasedCommand() {
		return aliasedCommand;
	}
}
