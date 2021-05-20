package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class AIR_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"air", "are", "ear", "ere", "eir", "ayer"};

	protected AIR_PhonemeData() {
		super(Phoneme.AIR, POTENTIAL_GRAPHEMES);
	}
}
