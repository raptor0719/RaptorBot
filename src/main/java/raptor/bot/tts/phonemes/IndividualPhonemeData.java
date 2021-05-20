package raptor.bot.tts.phonemes;

import raptor.bot.tts.Phoneme;

public abstract class IndividualPhonemeData {
	private final Phoneme phoneme;
	private final String[] potentialGraphemes;

	protected IndividualPhonemeData(final Phoneme phoneme, final String[] potentialGraphemes) {
		this.phoneme = phoneme;
		this.potentialGraphemes = potentialGraphemes;
	}

	public Phoneme getPhoneme() {
		return phoneme;
	}

	public String[] getPotentialGraphemes() {
		return potentialGraphemes;
	}

	public boolean examine(final String potentialGrapheme, final int offset, final String word) {
		for (final String s : potentialGraphemes)
			if (s.equals(potentialGrapheme))
				return true;
		return false;
	}
}
