package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class NG_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"ng", "n", "ngue"};

	protected NG_PhonemeData() {
		super(Phoneme.NG, POTENTIAL_GRAPHEMES);
	}
}
