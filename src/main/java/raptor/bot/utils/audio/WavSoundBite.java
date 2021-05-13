package raptor.bot.utils.audio;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;

import raptor.bot.utils.BinaryDataTools;
import raptor.bot.utils.LittleEndianDataInputStreamWrapper;

public class WavSoundBite {
	private static final String CHUNK_ID = "RIFF";
	private static final String FORMAT = "WAVE";

	private static final String SUBCHUNK1_ID = "fmt ";
	private static final int SUBCHUNK1_SIZE = 16;
	private static final int AUDIO_FORMAT = 1;

	private static final String SUBCHUNK2_ID = "data";

	private final AudioFormat format;
	private final byte[] audio;

	public WavSoundBite(final int numChannels, final int sampleRate, final int bitsPerSample, final byte[] audio) {
		this.audio = audio;
		this.format = new AudioFormat(sampleRate, bitsPerSample, numChannels, false, false);
	}

	public WavSoundBite(final AudioFormat format, final byte[] audio) {
		this.format = format;
		this.audio = audio;
	}

	public AudioFormat getFormat() {
		return format;
	}

	public byte[] getData() {
		return audio;
	}

	public static WavSoundBite read(final InputStream input) throws IOException {
		LittleEndianDataInputStreamWrapper dis = null;

		try {
			dis = new LittleEndianDataInputStreamWrapper(new DataInputStream(input));

			final String chunkId = BinaryDataTools.readBinaryString(4, dis.getWrapped());
			dis.readInt(); // chunkSize
			final String format = BinaryDataTools.readBinaryString(4, dis.getWrapped());
			final String subchunk1Id = BinaryDataTools.readBinaryString(4, dis.getWrapped());
			final int subchunk1Size = dis.readInt();
			final short audioFormat = dis.readShort();
			final short numChannels = dis.readShort();
			final int sampleRate = dis.readInt();
			dis.readInt(); // byteRate
			dis.readShort(); // blockAlign
			final short bitsPerSample = dis.readShort();
			final String subchunk2Id = BinaryDataTools.readBinaryString(4, dis.getWrapped());
			final int subchunk2Size = dis.readInt();

			if (!FORMAT.equals(format) || !SUBCHUNK1_ID.equals(subchunk1Id) || !SUBCHUNK2_ID.equals(subchunk2Id))
				throw new IllegalArgumentException("Non-wav format encountered.");
			else if (!CHUNK_ID.equals(chunkId))
				throw new IllegalArgumentException("Chunk type not supported. (Only RIFF chunk type is supported)");
			else if (AUDIO_FORMAT != audioFormat || SUBCHUNK1_SIZE != subchunk1Size)
				throw new IllegalArgumentException("Compressed format not supported. (Only linear PCM is supported)");

			final byte[] data = new byte[subchunk2Size];
			dis.getWrapped().readFully(data);

			return new WavSoundBite(numChannels, sampleRate, bitsPerSample, data);
		} finally {
			if (dis != null)
				dis.close();
		}
	}
}
