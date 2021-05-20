package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class V_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"v", "f", "ph", "ve"};

	protected V_PhonemeData() {
		super(Phoneme.V, POTENTIAL_GRAPHEMES);
	}
}
