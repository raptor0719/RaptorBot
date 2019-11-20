package raptor.bot.irc.message.messages;

public class ModeDeclarationMessage extends SourcedMessage {
	private final String nick;

	public ModeDeclarationMessage(final String payload, final String source, final String nick) {
		super(payload, source);
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nick == null) ? 0 : nick.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModeDeclarationMessage other = (ModeDeclarationMessage) obj;
		if (nick == null) {
			if (other.nick != null)
				return false;
		} else if (!nick.equals(other.nick))
			return false;
		return true;
	}
}
