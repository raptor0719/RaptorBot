package raptor.bot.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BinaryDataTools {
	public static byte[] serializeString(final String str) throws IOException {
		final ByteArrayOutputStream os = new ByteArrayOutputStream(str.length()*2 + 4);
		final DataOutputStream dos = new DataOutputStream(os);

		try {
			dos.writeInt(str.length());
			dos.writeChars(str);

			return os.toByteArray();
		} catch (final IOException e) {
			throw e;
		} finally {
			try {
				os.close();
				dos.close();
			} catch (Throwable t) {
				/* SWALLOW */
			}
		}
	}

	public static String marshalString(final DataInputStream dis) throws IOException {
		final int length = dis.readInt();
		final char[] buffer = new char[length];
		for (int i = 0; i < length; i++)
			buffer[i] = dis.readChar();

		return new String(buffer);
	}
}
