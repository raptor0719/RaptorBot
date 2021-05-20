package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class OE_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"o", "oa", "o_e", "oe", "ow", "ough", "eau", "oo", "ew"};

	protected OE_PhonemeData() {
		super(Phoneme.OE, POTENTIAL_GRAPHEMES);
	}
}
