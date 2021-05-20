package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class OI_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"oi", "oy", "uoy"};

	protected OI_PhonemeData() {
		super(Phoneme.OI, POTENTIAL_GRAPHEMES);
	}
}
