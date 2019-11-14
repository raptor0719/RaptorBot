package raptor.bot.utils.words;

import java.util.List;
import java.util.Map;

public class WordBank {
	private final Map<PartOfSpeech, List<String>> words;

	public WordBank(final Map<PartOfSpeech, List<String>> words) {
		this.words = words;
	}

	public String getRandomWord(final PartOfSpeech pos) {
		final List<String> wordList = words.get(pos);
		return wordList.get(getIntInRange(wordList.size()));
	}

	private int getIntInRange(final int upperExclusive) {
		return (int) (Math.random()*upperExclusive);
	}
}
