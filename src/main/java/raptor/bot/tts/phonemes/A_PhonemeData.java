package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class A_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"a", "ai", "au"};

	protected A_PhonemeData() {
		super(Phoneme.A, POTENTIAL_GRAPHEMES);
	}
}
