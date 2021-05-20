package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class M_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"m", "mm", "mb", "mn", "lm"};

	protected M_PhonemeData() {
		super(Phoneme.M, POTENTIAL_GRAPHEMES);
	}
}
