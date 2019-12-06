package raptor.bot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundPlayer {
	private static Thread soundThread = null;
	private static final Queue<InputStream> soundQueue = new ConcurrentLinkedQueue<InputStream>();
	/**
	 * A method that will play a sound provided by the given InputStream.
	 * @param audio
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws LineUnavailableException
	 */
	public static void playSound(final InputStream audio) {
		if (soundThread != null) {
			soundQueue.add(audio);
			return;
		}
		if (!soundQueue.isEmpty())
			soundQueue.clear();
		soundQueue.add(audio);
		soundThread = new Thread() {
			@Override
			public void run() {
				while (!soundQueue.isEmpty()) {
					try {
						play(soundQueue.poll());
					} catch (Throwable t) {
						System.err.println("An error occured while playing a sound:");
						t.printStackTrace();
					}
				}
				soundThread = null;
			}
		};
		soundThread.start();
	}

	private static void play(final InputStream audio) throws UnsupportedAudioFileException,IOException,LineUnavailableException {
		final AudioInputStream input = AudioSystem.getAudioInputStream(audio);
		final Clip clip = AudioSystem.getClip();
		clip.open(input);
		clip.start();

		final BlockingLineListener listener = new BlockingLineListener();
		clip.addLineListener(listener);
		while(!listener.stopped()) {
			// FIXME: This seems really dumb to have to do this but without it execution will not continue past this loop.
			// Presumably this is due to optimizations in the runtime or the while loop was blocking the sentinel value
			// from being updated. I don't know which but this works good enough for my purposes.
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {}
		}

		clip.close();
		input.close();
	}

	private static class BlockingLineListener implements LineListener {
		boolean stopped = false;

		@Override
		public void update(final LineEvent event) {
			if (event.getType().equals(LineEvent.Type.STOP)) {
				stopped = true;
			}
		}

		public boolean stopped() {
			return stopped;
		}
	}
}
