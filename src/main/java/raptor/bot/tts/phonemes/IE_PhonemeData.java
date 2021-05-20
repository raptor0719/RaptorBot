package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class IE_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"i", "y", "igh", "ie", "uy", "ye", "ai", "is", "eigh", "i_e"};

	protected IE_PhonemeData() {
		super(Phoneme.IE, POTENTIAL_GRAPHEMES);
	}
}
