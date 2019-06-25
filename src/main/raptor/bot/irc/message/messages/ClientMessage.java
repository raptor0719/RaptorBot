package raptor.bot.irc.message.messages;

public class ClientMessage extends SourcedMessage {
	private final String channel;

	public ClientMessage(final String payload, final String source, final String channel) {
		super(payload, source);
		this.channel = channel;
	}

	public String getChannel() {
		return channel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
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
		ClientMessage other = (ClientMessage) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		return true;
	}
}
