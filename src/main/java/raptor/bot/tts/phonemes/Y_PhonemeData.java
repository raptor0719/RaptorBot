package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class Y_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"y", "i", "j"};

	protected Y_PhonemeData() {
		super(Phoneme.Y, POTENTIAL_GRAPHEMES);
	}
}
