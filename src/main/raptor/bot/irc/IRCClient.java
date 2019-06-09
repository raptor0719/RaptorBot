package raptor.bot.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class IRCClient {
	private final String address;
	private final int port;

	private Socket connection;

	public IRCClient(final String address, final int port) {
		this.address = address;
		this.port = port;
	}

	public void connect() throws UnknownHostException, IOException {
		connection = new Socket(address, port);
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
		writer.write("USER guest * * :Daniel\n");
		writer.write("NICK CABRAL\n");
		writer.flush();
		final BufferedReader ostream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while(true) {
			final String message = ostream.readLine();
			if (message == null)
				continue;
			System.out.println(message);
			if (!connection.isConnected() || connection.isClosed() || message.contains("ERROR"))
				break;
			if (message.contains("PING")) {
				writer.write("PONG " + message.substring(5, message.length()) + "\n");
				writer.flush();
			}
			if (message.contains(":CABRAL")) {
				writer.write("JOIN #general\n");
				writer.flush();
			}
		}
	}
}
