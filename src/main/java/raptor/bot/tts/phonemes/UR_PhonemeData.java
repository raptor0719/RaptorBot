package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class UR_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"a", "er", "ar", "our", "ur"};

	protected UR_PhonemeData() {
		super(Phoneme.UR, POTENTIAL_GRAPHEMES);
	}
}
