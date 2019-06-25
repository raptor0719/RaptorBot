package raptor.bot.irc.message.messages;

public class SourcedMessage extends IrcMessage {
	private final String source;

	public SourcedMessage(final String payload, final String source) {
		super(payload);
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		SourcedMessage other = (SourcedMessage) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
}
