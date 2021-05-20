package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class URE_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"ure", "our"};

	protected URE_PhonemeData() {
		super(Phoneme.URE, POTENTIAL_GRAPHEMES);
	}
}
