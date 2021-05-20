package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public class DGE_PhonemeData extends IndividualPhonemeData {
	private static final String[] POTENTIAL_GRAPHEMES = new String[] {"j", "ge", "g", "dge", "di", "gg"};

	protected DGE_PhonemeData() {
		super(Phoneme.DGE, POTENTIAL_GRAPHEMES);
	}
}
