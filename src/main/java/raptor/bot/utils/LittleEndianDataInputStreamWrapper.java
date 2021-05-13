package raptor.bot.utils;

import java.io.DataInputStream;
import java.io.IOException;

public class LittleEndianDataInputStreamWrapper {
	private final DataInputStream input;

	public LittleEndianDataInputStreamWrapper(final DataInputStream input) {
		this.input = input;
	}

	public int readInt() throws IOException {
		return Integer.reverseBytes(input.readInt());
	}

	public short readShort() throws IOException {
		return Short.reverseBytes(input.readShort());
	}

	public void close() throws IOException {
		input.close();
	}

	public DataInputStream getWrapped() {
		return input;
	}
}
