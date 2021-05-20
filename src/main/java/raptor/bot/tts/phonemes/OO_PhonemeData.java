package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class OO_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"o", "oo", "u", "ou"};

	protected OO_PhonemeData() {
		super(Phoneme.OO, POTENTIAL_GRAPHEMES);
	}
}
