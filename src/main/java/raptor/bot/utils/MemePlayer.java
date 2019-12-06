package raptor.bot.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MemePlayer {
	public static String currentFileName = "current.gif";
	private static Thread memeThread = null;
	private static Queue<MemeManager.MemeInfo> memeQueue = new ConcurrentLinkedQueue<>();

	public static void playMeme(final String memePath, final long length) {
		if (memeThread != null) {
			memeQueue.add(new MemeManager.MemeInfo(memePath, length));
			return;
		}

		if (!memeQueue.isEmpty())
			memeQueue.clear();

		memeQueue.add(new MemeManager.MemeInfo(memePath, length));
		memeThread = new Thread() {
			@Override
			public void run() {
				while (!memeQueue.isEmpty())
					play (memeQueue.poll());
				memeThread = null;
			}
		};
		memeThread.start();
	}

	private static void play(final MemeManager.MemeInfo memeInfo) {
		final File from = new File(memeInfo.filePath);
		final File to = new File(currentFileName);
		try {
			Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);

			Thread.sleep(memeInfo.length);

			to.delete();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
