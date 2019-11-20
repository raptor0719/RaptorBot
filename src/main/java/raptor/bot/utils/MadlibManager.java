package raptor.bot.utils;

import raptor.bot.api.IMadlibManager;
import raptor.bot.utils.words.PartOfSpeech;
import raptor.bot.utils.words.WordBank;

public class MadlibManager implements IMadlibManager {
	private final WordBank wordBank;

	public MadlibManager(final WordBank wordBank) {
		this.wordBank = wordBank;
	}

	@Override
	public String fill(final String s) {
		String intermediate = s;
		String previous = "";
		for (final PartOfSpeech pos : PartOfSpeech.values()) {
			previous = "";
			while (!previous.equals(intermediate)) {
				previous = intermediate;
				intermediate = replace(intermediate, "-" + pos.getId(), wordBank.getRandomWord(pos));
			}
		}
		return intermediate;
	}

	@Override
	public String getFormat() {
		String intermediate = "";
		for (final PartOfSpeech pos : PartOfSpeech.values())
			intermediate += "-" + pos.getId() + "=" + pos.name() + ", ";
		return intermediate.substring(0, intermediate.length() - 2);
	}

	private String replace(final String s, final String searchStr, final String replacement) {
		String intermediate = s;
		String result = replaceAtBeginning(intermediate, searchStr, replacement);
		if (!result.equals(intermediate)) {
			return result;
		}
		intermediate = result;

		result = replaceAtEnd(intermediate, searchStr, replacement);
		if (!result.equals(intermediate)) {
			return result;
		}
		intermediate = result;

		result = replaceInMiddle(intermediate, searchStr, replacement);
		if (!result.equals(intermediate)) {
			return result;
		}
		intermediate = result;

		result = replaceSolo(intermediate, searchStr, replacement);
		if (!result.equals(intermediate)) {
			return result;
		}
		intermediate = result;

		return intermediate;
	}

	private String replaceAtBeginning(final String s, final String pattern, final String replacement) {
		return s.replaceFirst(String.format("^%s([^a-zA-Z0-9])", pattern), replacement + "$1");
	}

	private String replaceAtEnd(final String s, final String pattern, final String replacement) {
		return s.replaceFirst(String.format("([^a-zA-Z0-9])%s$", pattern), "$1" + replacement);
	}

	private String replaceInMiddle(final String s, final String pattern, final String replacement) {
		return s.replaceFirst(String.format("([^a-zA-Z0-9])%s([^a-zA-Z0-9])", pattern), "$1" + replacement + "$2");
	}

	private String replaceSolo(final String s, final String pattern, final String replacement) {
		return s.replaceFirst(String.format("^%s$", pattern), replacement);
	}
}
