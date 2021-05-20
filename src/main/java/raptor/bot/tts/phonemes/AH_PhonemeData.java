package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class AH_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"a"};

	protected AH_PhonemeData() {
		super(Phoneme.AH, POTENTIAL_GRAPHEMES);
	}
}
