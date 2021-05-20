package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class F_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"f", "ff", "ph", "gh", "lf", "ft"};

	protected F_PhonemeData() {
		super(Phoneme.F, POTENTIAL_GRAPHEMES);
	}
}
