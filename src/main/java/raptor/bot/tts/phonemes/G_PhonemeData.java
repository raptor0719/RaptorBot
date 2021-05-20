package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class G_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"g", "gg", "gh", "gu", "gue"};

	protected G_PhonemeData() {
		super(Phoneme.G, POTENTIAL_GRAPHEMES);
	}
}
