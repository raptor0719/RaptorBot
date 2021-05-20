package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class EER_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"ear", "eer", "ere", "ier"};

	protected EER_PhonemeData() {
		super(Phoneme.EER, POTENTIAL_GRAPHEMES);
	}
}
