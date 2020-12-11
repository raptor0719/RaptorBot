package raptor.bot.utils;

import java.util.Arrays;
import java.util.List;

import raptor.bot.api.message.IMessageSender;

public class MagicBall {
	private static final List<Responder> RESPONSES = Arrays.asList(new Responder[] {
			response(3, "Absolutely!"),
			response(3, "It's looking like it."),
			response(3, "Yessir."),
			response(3, "Yes!"),
			response(3, "More than likely."),
			response(3, "You can count on it."),
			response(3, "Why not?"),
			listResponse(1,
					response("Hold on..."),
					delayedResponse("/me shakes a Magic 8 Ball.", 2),
					delayedResponse("\"Outlook good.\" ?!", 5),
					delayedResponse("Well that's bull.", 8)),

			response(3, "You're asking me?"),
			response(3, "Who asks that?"),
			response(3, "Why would you even ask that?"),
			response(3, "Find out for yourself."),
			response(3, "I probably shouldn't tell you."),
			response(3, "What do you think?"),

			response(2, "No LUL"),
			response(2, "You wish KEKW"),
			response(3, "Absolutely not!"),
			response(3, "I'm gonna give a \"no\" on this one."),
			response(3, "Probably not."),
			response(3, "Nope."),
			response(3, "No!"),
			response(3, "I don't think so."),
			listResponse(1,
					response("Yes!"),
					delayedResponse("Oh wait, no.", 3)),
		});

	private static final long AFK_LENGTH = minutes(5);
	private static final long SHAKES_DROPOFF = minutes(5);
	private static final int SHAKES_AFK_THRESHOLD = 10;
	private static final Responder AFK_RESPONDER = listResponse(0,
			response(0, "I need to recharge my juice. Be right back!"),
			response(0, "/me heads out for a bit."));

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
	private static final long LAST_QUESTION_DROPOFF = minutes(1);

	private static final int TOTAL_WEIGHT;
	static {
		int weight = BUSY_RESPONSE_WEIGHT;
		for (final Responder r : RESPONSES)
			weight += r.getWeight();
		TOTAL_WEIGHT = weight;
	}

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

	public void shake(final String question, final String user, final IMessageSender<String> messageService) {
		// Afk routine
		isAfk = System.currentTimeMillis() - timestampWentAfk < AFK_LENGTH;
		if (isAfk)
			return;

		// Busy routine
		isBusy = System.currentTimeMillis() - timestampWentBusy < BUSY_LENGTH;
		if (isBusy) {
			if (busyDialogueStage >= BUSY_RESPONSE_STAGES.size()) {
				messageService.sendMessage(String.format("/timeout %s 60", pullActualUsernameFromUser(user)));
			} else {
				messageService.sendMessage(BUSY_RESPONSE_STAGES.get(busyDialogueStage));
				busyDialogueStage += 1;
			}

			return;
		} else {
			busyDialogueStage = 0;
		}

		// Empty message routine
		if (question.isEmpty()) {
			if (emptyMessageDialogueStage >= EMPTY_MESSAGE_RESPONSE_STAGES.size()) {
				messageService.sendMessage(String.format("/timeout %s 60", pullActualUsernameFromUser(user)));
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
		if (question.equals(lastQuestion)) {
			messageService.sendMessage("The answer doesn’t just change just because you ask it again.");
			return;
		}

		// Handle shake threshold falloff
		if (System.currentTimeMillis() - lastShake > SHAKES_DROPOFF)
			shakesInTimespan = 0;

		// Tired threshold, so goes afk
		if (shakesInTimespan > SHAKES_AFK_THRESHOLD) {
			isAfk = true;
			AFK_RESPONDER.respond(messageService);
			timestampWentAfk = System.currentTimeMillis();
			return;
		}

		// Respond from the 8 ball!
		lastQuestionTimestamp = System.currentTimeMillis();
		lastQuestion = question;
		lastShake = System.currentTimeMillis();
		shakesInTimespan += 1;

		final int generated = generateRandomValue(TOTAL_WEIGHT);

		if (generated < BUSY_RESPONSE_WEIGHT) {
			isBusy = true;
			messageService.sendMessage(BUSY_RESPONSE_STAGES.get(0));
			busyDialogueStage = 1;
			timestampWentBusy = System.currentTimeMillis();
		} else {
			int leftToGo = generated - BUSY_RESPONSE_WEIGHT;
			for (final Responder r : RESPONSES) {
				leftToGo -= r.getWeight();
				if (leftToGo <= 0) {
					r.respond(messageService);
					break;
				}
			}
		}
	}

	private int generateRandomValue(final int total) {
		return (int) (Math.random() * total);
	}

	private String pullActualUsernameFromUser(final String userString) {
		return userString.substring(0, userString.indexOf("!"));
	}

	private static long minutes(final int minutes) {
		return minutes * 60 * 1000;
	}

	private static Responder response(final int weight, final String response) {
		return new SingleResponse(weight, response);
	}

	private static Responder response(final String response) {
		return response(1, response);
	}

	private static Responder delayedResponse(final int weight, final String response, final int delay) {
		return new DelayedSingleResponse(weight, delay, response);
	}

	private static Responder delayedResponse(final String response, final int delay) {
		return delayedResponse(1, response, delay);
	}

	private static Responder listResponse(final int weight, final Responder... responses) {
		return new ListResponse(weight, Arrays.asList(responses));
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
			final Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(delay * 1000);
					} catch (Throwable t) {}
					messageService.sendMessage(response);
				}
			};
			t.start();
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
