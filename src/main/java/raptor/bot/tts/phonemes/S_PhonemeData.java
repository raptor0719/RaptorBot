package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class S_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"s", "ss", "c", "sc", "ps", "st", "ce", "se"};

	protected S_PhonemeData() {
		super(Phoneme.S, POTENTIAL_GRAPHEMES);
	}
}
