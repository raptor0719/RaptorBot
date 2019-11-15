package raptor.bot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import raptor.bot.utils.words.PartOfSpeech;
import raptor.bot.utils.words.WordBank;

public class MadlibManagerTest {

	@Test
	public void test() {
		final MadlibManager m = getMadlibManager();
		Assert.assertEquals(m.fill("-n -con -pre"), "Noun Conjunction Preposition");
	}

	private MadlibManager getMadlibManager() {
		return new MadlibManager(getWordBank());
	}

	private WordBank getWordBank() {
		final Map<PartOfSpeech, List<String>> wordBank = new HashMap<>();

		for (final PartOfSpeech p : PartOfSpeech.values()) {
			final List<String> l = new ArrayList<>();
			l.add(p.name());
			wordBank.put(p, l);
		}

		return new WordBank(wordBank);
	}
}
