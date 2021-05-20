package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class Z_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"z", "zz", "s", "ss", "x", "ze", "se"};

	protected Z_PhonemeData() {
		super(Phoneme.Z, POTENTIAL_GRAPHEMES);
	}
}
