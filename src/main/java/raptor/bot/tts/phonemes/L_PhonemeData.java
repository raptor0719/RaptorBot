package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class L_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"l", "ll"};

	protected L_PhonemeData() {
		super(Phoneme.L, POTENTIAL_GRAPHEMES);
	}
}
