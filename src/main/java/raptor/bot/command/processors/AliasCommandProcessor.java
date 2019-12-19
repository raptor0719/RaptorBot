package raptor.bot.command.processors;

import raptor.bot.api.IAliasManager;
import raptor.bot.api.message.IMessageSender;
import raptor.bot.command.BotCommandProcessor;
import raptor.bot.command.BotMethod;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.alias.AliasCommand;
import raptor.bot.command.commands.alias.AliasCreateCommand;
import raptor.bot.command.commands.alias.AliasDeleteCommand;
import raptor.bot.command.commands.alias.AliasListCommand;
import raptor.bot.irc.ChatMessage;

public class AliasCommandProcessor extends BotCommandProcessor {
	private final IAliasManager aliasManager;

	public AliasCommandProcessor(final IAliasManager aliasManager) {
		super(BotMethod.ALIAS.getWord(), "Use '!alias list' to list all aliases. Use '!alias create <alias> <command>' to create a new alias or replace an existing alias. Use '!alias delete <alias>' to delete an alias");
		this.aliasManager = aliasManager;
	}

	@Override
	protected boolean doProcess(final ChatMessage message, final IMessageSender<String> sender) {
		final String input = message.getMessage();
		final BotCommand command = BotMethod.ALIAS.parse(input);

		if (!(command instanceof AliasCommand)) {
			System.err.println("Expected instanceof AliasCommand.");
			return false;
		}

		if (command instanceof AliasCreateCommand) {
			final AliasCreateCommand create = (AliasCreateCommand)command;
			aliasManager.create(create.getAlias(), create.getAliasedCommand());
			sender.sendMessage("Created new alias.");
		} else if (command instanceof AliasDeleteCommand) {
			final AliasDeleteCommand delete = (AliasDeleteCommand)command;
			aliasManager.delete(delete.getAlias());
			sender.sendMessage("Deleted alias.");
		} else if (command instanceof AliasListCommand) {
			sender.sendMessage(buildAliasList());
		} else {
			System.err.println("Expected instanceof AliasCreateCommand, AliasDeleteCommand, or AliasListCommand.");
		}

		return true;
	}

	private String buildAliasList() {
		final Iterable<String> aliases = aliasManager.getAliases();

		if (!aliases.iterator().hasNext())
			return "";

		String aliasList = "The following is a list of aliases: ";
		for (final String s : aliases)
			aliasList += s + ", ";
		return aliasList.substring(0, aliasList.length() - 2) + ".";
	}
}
