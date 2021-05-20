package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class UH_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"u", "o", "oo", "ou"};

	protected UH_PhonemeData() {
		super(Phoneme.UH, POTENTIAL_GRAPHEMES);
	}
}
