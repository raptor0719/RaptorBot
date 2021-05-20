package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class EE_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"e", "ee", "ea", "y", "ey", "oe", "ie", "i", "ei", "eo", "ay"};

	protected EE_PhonemeData() {
		super(Phoneme.EE, POTENTIAL_GRAPHEMES);
	}
}
