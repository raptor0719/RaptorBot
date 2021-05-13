package raptor.bot.tts;

import java.util.List;

import raptor.bot.utils.audio.SoundPlayer;
import raptor.bot.utils.audio.WavSoundBite;

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
			final WavSoundBite audio = audioManager.getPhonemesAudio(phonemes);
			SoundPlayer.playSound(audio.getFormat(), audio.getData());
		} catch (Exception e) {
			System.err.println("Error attempting to text-to-speech phrase: " + input);
			e.printStackTrace();
		}
	}
}
