package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class N_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"n", "nn", "kn", "gn", "pn", "mn"};

	protected N_PhonemeData() {
		super(Phoneme.N, POTENTIAL_GRAPHEMES);
	}
}
