package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class E_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"e", "ea", "u", "ie", "ai", "a", "eo", "ei", "ae"};

	protected E_PhonemeData() {
		super(Phoneme.E, POTENTIAL_GRAPHEMES);
	}
}
