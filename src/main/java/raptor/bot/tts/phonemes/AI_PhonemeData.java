package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class AI_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"a", "ai", "eigh", "aigh", "ay", "er", "et", "ei", "au", "a_e", "ea", "ey"};

	protected AI_PhonemeData() {
		super(Phoneme.AI, POTENTIAL_GRAPHEMES);
	}
}
