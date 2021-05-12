package raptor.bot.tts;

import java.util.List;

//TODO: Fill this entire class in
public class TextToSpeech {
	private final PhonemeAudioManager audioManager;
	private final PhonemeParser parser;

	public TextToSpeech(final PhonemeAudioManager audioManager, final PhonemeParser parser) {
		this.audioManager = audioManager;
		this.parser = parser;
	}

	public void speakPhrase(final String input) {
		try {
			final List<Phoneme> phonemes = parser.parse(input);
		} catch (Exception e) {
			System.err.println("Error attempting to text-to-speech phrase: " + input);
			e.printStackTrace();
		}
	}

	private byte[] getBytes(final List<Phoneme> phonemes) {
		return new byte[] {};
	}
}
