package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class R_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"r", "rr", "wr", "rh"};

	protected R_PhonemeData() {
		super(Phoneme.R, POTENTIAL_GRAPHEMES);
	}
}
