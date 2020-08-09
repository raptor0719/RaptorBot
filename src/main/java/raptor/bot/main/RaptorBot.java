package raptor.bot.main;

import java.util.Iterator;

import raptor.bot.api.IBotProcessor;
import raptor.bot.api.IInherentBotProcessor;
import raptor.bot.api.ITransformer;
import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageService;
import raptor.bot.irc.ChatMessage;

public class RaptorBot {
	private final IMessageService<ChatMessage, String> messageService;
	private final ITransformer<ChatMessage, String> chatProcessor;
	private final IChatDatastore chatDatastore;
	private final IBotProcessor<ChatMessage> botProcessor;
	private final IInherentBotProcessor inherentProcessor;

	public RaptorBot(final IMessageService<ChatMessage, String> messageService, final ITransformer<ChatMessage, String> chatProcessor, final IChatDatastore chatDatastore, final IBotProcessor<ChatMessage> botProcessor, final IInherentBotProcessor inherentProcessor) {
		this.messageService = messageService;
		this.chatProcessor = chatProcessor;
		this.chatDatastore = chatDatastore;
		this.botProcessor = botProcessor;
		this.inherentProcessor = inherentProcessor;
	}

	public void process() {
		inherentProcessor.process(messageService);

		final Iterator<ChatMessage> messages = messageService.receiveMessages();
		while (messages.hasNext()) {
			final ChatMessage message = messages.next();
			storeMessageToChatLog(message);

			final boolean wasProcessed = botProcessor.process(message, messageService);

			if (!wasProcessed)
				messageService.sendMessage(chatProcessor.transform(message));
		}
	}

	private void storeMessageToChatLog(final ChatMessage message) {
		try {
			chatDatastore.storeMessage(message.getChannel().substring(1), message.getUser().split("!")[0], message.getMessage(), System.currentTimeMillis());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
