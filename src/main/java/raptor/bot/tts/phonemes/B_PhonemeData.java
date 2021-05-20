package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class B_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"b", "bb"};

	protected B_PhonemeData() {
		super(Phoneme.B, POTENTIAL_GRAPHEMES);
	}
}
