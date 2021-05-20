package raptor.bot.tts;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PhonemeParserTest {
	@Test
	public void singleWord() throws Exception {
		final String word = "hope";

		final List<Phoneme> actual = getParser().parse(word);

		final List<Phoneme> expected = Arrays.asList(new Phoneme[] {
				Phoneme.AWH,
				Phoneme.P,
				Phoneme.E
		});

		Assert.assertTrue(actual.containsAll(expected));
	}

	@Test
	public void phrase() throws Exception {
		final String word = "I go to the store";

		final List<Phoneme> actual = getParser().parse(word);

		final List<Phoneme> expected = Arrays.asList(new Phoneme[] {
				Phoneme.EE, Phoneme.WORD_SPACE, Phoneme.DGE, Phoneme.I, Phoneme.WORD_SPACE,
				Phoneme.T, Phoneme.I, Phoneme.WORD_SPACE, Phoneme.T, Phoneme.E, Phoneme.WORD_SPACE,
				Phoneme.S, Phoneme.OE, Phoneme.R
		});

		Assert.assertTrue(actual.containsAll(expected));
	}

	@Test
	public void multipleSentences() throws Exception {
		final String word = "Jim likes pie. He eats it. He rests.";

		final List<Phoneme> actual = getParser().parse(word);

		final List<Phoneme> expected = Arrays.asList(new Phoneme[] {
				Phoneme.DGE, Phoneme.EE, Phoneme.M, Phoneme.WORD_SPACE, Phoneme.L, Phoneme.IE,
				Phoneme.CK, Phoneme.S, Phoneme.WORD_SPACE, Phoneme.P, Phoneme.E, Phoneme.SENTENCE_SPACE,
				Phoneme.H, Phoneme.E, Phoneme.WORD_SPACE, Phoneme.AI, Phoneme.T, Phoneme.S,
				Phoneme.WORD_SPACE, Phoneme.EE, Phoneme.T, Phoneme.SENTENCE_SPACE, Phoneme.H,
				Phoneme.E, Phoneme.WORD_SPACE, Phoneme.R, Phoneme.E, Phoneme.S, Phoneme.S
		});

		Assert.assertTrue(actual.containsAll(expected));
	}

	private PhonemeParser getParser() {
		return new PhonemeParser();
	}

	private void printPhonemes(final List<Phoneme> phonemes) {
		for (final Phoneme p : phonemes)
			System.out.print(p.name() + " ");
		System.out.println();
		printPhonemesList(phonemes);
	}

	private void printPhonemesList(final List<Phoneme> phonemes) {
		for (final Phoneme p : phonemes)
			System.out.print("Phoneme." + p.name() + ", ");
		System.out.println();
	}
}
