package raptor.bot.tts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFormat;

import raptor.bot.utils.audio.WavSoundBite;

public class PhonemeAudioManager {
	private final Map<Phoneme, byte[]> phonemeMap;
	private final AudioFormat format;

	public PhonemeAudioManager(final Map<Phoneme, byte[]> phonemeMap, final AudioFormat format) {
		this.phonemeMap = phonemeMap;
		this.format = format;
	}

	public WavSoundBite getPhonemesAudio(final List<Phoneme> phonemes) {
		final List<byte[]> audio = new ArrayList<>();

		for (final Phoneme ph : phonemes)
			audio.add(phonemeMap.get(ph));

		return new WavSoundBite(format, createByteArray(audio));
	}

	private byte[] createByteArray(final List<byte[]> bytes) {
		int fullLength = 0;
		for (final byte[] b : bytes)
			fullLength += b.length;

		final byte[] audio = new byte[fullLength];

		int current = 0;
		for (final byte[] arr : bytes) {
			for (final byte b : arr) {
				audio[current] = b;
				current++;
			}
		}

		return audio;
	}
}
