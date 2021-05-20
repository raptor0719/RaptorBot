package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class SZ_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"s", "si", "z"};

	protected SZ_PhonemeData() {
		super(Phoneme.SZ, POTENTIAL_GRAPHEMES);
	}
}
