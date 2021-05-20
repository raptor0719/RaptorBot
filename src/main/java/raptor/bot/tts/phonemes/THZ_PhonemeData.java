package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class THZ_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"th"};

	protected THZ_PhonemeData() {
		super(Phoneme.THZ, POTENTIAL_GRAPHEMES);
	}
}
