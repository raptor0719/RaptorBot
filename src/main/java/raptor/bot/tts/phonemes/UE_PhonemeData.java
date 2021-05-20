package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class UE_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"o", "oo", "ew", "ue", "u_e", "oe", "ough", "ui", "oew", "ou"};

	protected UE_PhonemeData() {
		super(Phoneme.UE, POTENTIAL_GRAPHEMES);
	}
}
