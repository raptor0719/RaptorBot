package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class CH_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"ch", "tch", "tu", "ti", "te"};

	protected CH_PhonemeData() {
		super(Phoneme.CH, POTENTIAL_GRAPHEMES);
	}
}
