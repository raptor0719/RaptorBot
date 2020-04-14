package raptor.bot.command.commands.chatstats;

public class ChatStatsUserMessageCountCommand extends ChatStatsCommand {
	private final String user;

	public ChatStatsUserMessageCountCommand(final String user) {
		this.user = user;
	}

	public String getUser() {
		return user;
	}
}
