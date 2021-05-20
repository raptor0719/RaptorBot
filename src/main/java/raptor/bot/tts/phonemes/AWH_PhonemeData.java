package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class AWH_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"a", "ho", "au", "aw", "ough"};

	protected AWH_PhonemeData() {
		super(Phoneme.AWH, POTENTIAL_GRAPHEMES);
	}
}
