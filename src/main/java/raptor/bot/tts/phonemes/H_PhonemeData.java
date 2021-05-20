package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class H_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"h", "wh"};

	protected H_PhonemeData() {
		super(Phoneme.H, POTENTIAL_GRAPHEMES);
	}
}
