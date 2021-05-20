package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class P_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"p", "pp"};

	protected P_PhonemeData() {
		super(Phoneme.P, POTENTIAL_GRAPHEMES);
	}
}
