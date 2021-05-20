package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class AW_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"aw", "a", "or", "oor", "ore", "oar", "our", "augh", "ar", "ough", "au"};

	protected AW_PhonemeData() {
		super(Phoneme.AW, POTENTIAL_GRAPHEMES);
	}
}
