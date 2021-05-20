package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class THS_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"th"};

	protected THS_PhonemeData() {
		super(Phoneme.THS, POTENTIAL_GRAPHEMES);
	}
}
