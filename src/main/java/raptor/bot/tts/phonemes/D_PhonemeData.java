package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class D_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"d", "dd", "ed"};

	protected D_PhonemeData() {
		super(Phoneme.D, POTENTIAL_GRAPHEMES);
	}
}
