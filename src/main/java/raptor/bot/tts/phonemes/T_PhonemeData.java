package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class T_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"t", "tt", "th", "ed"};

	protected T_PhonemeData() {
		super(Phoneme.T, POTENTIAL_GRAPHEMES);
	}
}
