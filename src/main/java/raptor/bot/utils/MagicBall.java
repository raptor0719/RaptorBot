package raptor.bot.utils;

import java.util.Arrays;
import java.util.List;

import raptor.bot.api.message.IMessageSender;
import raptor.bot.irc.ChatMessage;

public class MagicBall {
	private static final List<Responder> RESPONSES = Arrays.asList(new Responder[] {

		});

	private static final long AFK_LENGTH = minutes(5);
	private static final long SHAKES_DROPOFF = minutes(5);

	private static final int BUSY_RESPONSE_WEIGHT = 1;
	private static final long BUSY_LENGTH = minutes(5);
	private static final List<String> BUSY_RESPONSE_STAGES = Arrays.asList(new String[] {
			"I'm a little busy now. Ask me later.",
			"Enough with the shaking.",
			"I said I'm busy.",
			"Stop, please.",
			"I dare you to bother me again."
		});

	private static final List<String> EMPTY_MESSAGE_RESPONSE_STAGES = Arrays.asList(new String[] {
			"You have to actually ask me something for an answer.",
			"Just ask something. Anything!",
			"Seriously, anything. It really isn't that hard.",
			"Are you slow?",
			"Okay, really?",
			"You're doing this on purpose now.",
			"You do that again and you're asking for it."
		});
	private static final long LAST_QUESTION_DROPOFF = minutes(3);

	private long lastShake = 0;
	private int shakesInTimespan = 0;
	private long timestampWentAfk = 0;
	private boolean isAfk = false;

	private int busyDialogueStage = 0;
	private long timestampWentBusy = 0;
	private boolean isBusy = false;

	private int emptyMessageDialogueStage = 0;

	private long lastQuestionTimestamp = 0;
	private String lastQuestion = null;

	public MagicBall() {}

	public void shake(final ChatMessage question, final IMessageSender<String> messageService) {
		// Afk routine
		isAfk = System.currentTimeMillis() - timestampWentAfk > AFK_LENGTH;
		if (isAfk)
			return;

		// Busy routine
		isBusy = System.currentTimeMillis() - timestampWentBusy > BUSY_LENGTH;
		if (isBusy) {
			if (busyDialogueStage >= BUSY_RESPONSE_STAGES.size()) {
				messageService.sendMessage(String.format("/timeout %s 60", question.getUser()));
			} else {
				messageService.sendMessage(BUSY_RESPONSE_STAGES.get(busyDialogueStage));
				busyDialogueStage += 1;
			}

			return;
		} else {
			busyDialogueStage = 0;
		}

		// Empty message routine
		if (question.getMessage().isEmpty()) {
			if (emptyMessageDialogueStage >= EMPTY_MESSAGE_RESPONSE_STAGES.size()) {
				messageService.sendMessage(String.format("/timeout %s 60", question.getUser()));
			} else {
				messageService.sendMessage(EMPTY_MESSAGE_RESPONSE_STAGES.get(emptyMessageDialogueStage));
				emptyMessageDialogueStage += 1;
			}

			return;
		} else {
			emptyMessageDialogueStage = 0;
		}

		// Repeated message routine
		lastQuestion = (System.currentTimeMillis() - lastQuestionTimestamp > LAST_QUESTION_DROPOFF) ? null : lastQuestion;
		if (question.getMessage().equals(lastQuestion)) {
			messageService.sendMessage("The answer doesn’t just change just because you ask it again.");
			return;
		}

		// Respond from the 8 ball!
		lastQuestionTimestamp = System.currentTimeMillis();
		lastQuestion = question.getMessage();
	}

	private int getTotalWeight() {
		int weight = BUSY_RESPONSE_WEIGHT;
		for (final Responder r : RESPONSES)
			weight += r.getWeight();
		return weight;
	}

	private static long minutes(final int minutes) {
		return minutes * 60 * 1000;
	}

	private static Responder response(final int weight, final String response) {
		return new SingleResponse(weight, response);
	}

	private static Responder delayedResponse(final int weight, final int delay, final String response) {
		return new DelayedSingleResponse(weight, delay, response);
	}

	private static Responder listResponse(final int weight, final List<Responder> responses) {
		return new ListResponse(weight, responses);
	}

	private interface Responder {
		void respond(IMessageSender<String> messageService);
		int getWeight();
	}

	private static class SingleResponse implements Responder {
		private final int weight;
		private final String response;

		public SingleResponse(final int weight, final String response) {
			this.weight = weight;
			this.response = response;
		}

		@Override
		public void respond(final IMessageSender<String> messageService) {
			messageService.sendMessage(response);
		}

		@Override
		public int getWeight() {
			return weight;
		}
	}

	private static class DelayedSingleResponse implements Responder {
		private final int weight;
		private final int delay;
		private final String response;

		public DelayedSingleResponse(final int weight, final int delay, final String response) {
			this.weight = weight;
			this.delay = delay;
			this.response = response;
		}

		@Override
		public void respond(final IMessageSender<String> messageService) {
			try {
				Thread.sleep(delay);
			} catch (Throwable t) {}
			messageService.sendMessage(response);
		}

		@Override
		public int getWeight() {
			return weight;
		}
	}

	private static class ListResponse implements Responder {
		private final int weight;
		private final List<Responder> responses;

		public ListResponse(final int weight, final List<Responder> responses) {
			this.responses = responses;
			this.weight = weight;
		}

		@Override
		public void respond(final IMessageSender<String> messageService) {
			for (final Responder r : responses)
				r.respond(messageService);
		}

		@Override
		public int getWeight() {
			return weight;
		}
	}
}
