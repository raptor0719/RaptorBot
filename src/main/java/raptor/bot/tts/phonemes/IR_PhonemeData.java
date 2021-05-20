package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class IR_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"ir", "er", "ur", "ear", "or", "our", "yr"};

	protected IR_PhonemeData() {
		super(Phoneme.IR, POTENTIAL_GRAPHEMES);
	}
}
