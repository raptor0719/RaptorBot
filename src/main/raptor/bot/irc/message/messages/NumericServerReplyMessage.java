package raptor.bot.irc.message.messages;

public class NumericServerReplyMessage extends SourcedMessage {
	private final String responseCode;
	private final String nick;

	public NumericServerReplyMessage(final String payload, final String source, final String responseCode, final String nick) {
		super(payload, source);
		this.responseCode = responseCode;
		this.nick = nick;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public String getNick() {
		return nick;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((nick == null) ? 0 : nick.hashCode());
		result = prime * result + ((responseCode == null) ? 0 : responseCode.hashCode());
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
		NumericServerReplyMessage other = (NumericServerReplyMessage) obj;
		if (nick == null) {
			if (other.nick != null)
				return false;
		} else if (!nick.equals(other.nick))
			return false;
		if (responseCode == null) {
			if (other.responseCode != null)
				return false;
		} else if (!responseCode.equals(other.responseCode))
			return false;
		return true;
	}
}
