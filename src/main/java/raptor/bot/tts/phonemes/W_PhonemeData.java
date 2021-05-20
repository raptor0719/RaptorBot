package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class W_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"w", "wh", "u", "o"};

	protected W_PhonemeData() {
		super(Phoneme.W, POTENTIAL_GRAPHEMES);
	}
}
