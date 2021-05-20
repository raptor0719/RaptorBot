package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class I_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"i", "e", "o", "u", "ui", "y", "ie"};

	protected I_PhonemeData() {
		super(Phoneme.I, POTENTIAL_GRAPHEMES);
	}
}
