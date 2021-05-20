package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class OW_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"ow", "ou", "ough"};

	protected OW_PhonemeData() {
		super(Phoneme.OW, POTENTIAL_GRAPHEMES);
	}
}
