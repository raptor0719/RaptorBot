package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class SH_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"sh", "ce", "s", "ci", "si", "ch", "sci", "ti"};

	protected SH_PhonemeData() {
		super(Phoneme.SH, POTENTIAL_GRAPHEMES);
	}
}
