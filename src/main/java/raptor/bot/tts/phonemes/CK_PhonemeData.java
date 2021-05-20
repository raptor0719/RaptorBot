package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class CK_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"k", "c", "ch", "cc", "lk", "qu", "ck", "x", "q"};

	protected CK_PhonemeData() {
		super(Phoneme.CK, POTENTIAL_GRAPHEMES);
	}
}
