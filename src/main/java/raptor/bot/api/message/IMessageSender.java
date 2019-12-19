package raptor.bot.api.message;

public interface IMessageSender<S> {
	void sendMessage(S message);
}
