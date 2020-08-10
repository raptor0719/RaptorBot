package raptor.bot.chatter.mimic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.chatter.mimic.structures.SignalWord;
import raptor.bot.chatter.mimic.structures.Word;
import raptor.bot.irc.ChatMessage;

public class ChatMimicDictionary {
	public static final int PROXIMITY_LINE_COUNT = 30;

	private final List<String> firstWords;
	private final Map<String, Word> words;
	private final List<String> lastWords;
	private final int[] lengthWeights;

	private ChatMimicDictionary(final List<String> firstWords, final Map<String, Word> words, final List<String> lastWords, final int[] lengthWeights) {
		this.firstWords = firstWords;
		this.words = words;
		this.lastWords = lastWords;
		this.lengthWeights = lengthWeights;
	}

	public boolean containsWord(final String word) {
		return containsWord(words, word);
	}

	public boolean isSignaledBy(final String word, final String signalingWord) {
		if (!containsWord(words, word))
			return false;
		return containsSignalWord(getWord(words, word).signalWords, signalingWord);
	}

	public double getSignal(final String word, final String signalingWord) {
		if (!containsWord(words, word))
			throw new RuntimeException(String.format("The word '%s' was not contained in the dictionary.", word));
		final  Map<String, SignalWord> signalWords = getWord(words, word).signalWords;
		if (!containsSignalWord(signalWords, signalingWord))
			throw new RuntimeException(String.format("The word '%s' was not contained in the signaled list.", signalingWord));
		return getSignalWord(signalWords, signalingWord).signal;
	}

	public List<String> getFirstWords() {
		return firstWords;
	}

	public List<String> getLastWords() {
		return lastWords;
	}

	public int[] getLengthWeights() {
		return lengthWeights;
	}

	public List<String> getFollowingWords(final String word) {
		if (!containsWord(words, word))
			throw new RuntimeException(String.format("The word '%s' was not contained in the dictionary.", word));
		return getWord(words, word).followedWords;
	}

	/* STATIC */

	public static ChatMimicDictionary compile(final IChatDatastore chatDatastore) {
		final int totalMessageCount = chatDatastore.getTotalMessageCount();

		final double[] proximityWeights = getProximityWeights(PROXIMITY_LINE_COUNT);
		final LinkedList<String> proximityLines = new LinkedList<>();

		final List<String> firstWords = new ArrayList<>();
		final List<String> lastWords = new ArrayList<>();
		final Map<String, Word> words = new HashMap<>();
		final Map<Integer, Integer> lengthWeights = new HashMap<>();

		System.out.println("TotalCount: " + totalMessageCount);
		final Iterator<ChatMessage> messages = chatDatastore.getMessagesInRange(0, totalMessageCount - 1);
		int i = 0;
		while (messages.hasNext()) {
			if (i%5000 == 0)
				System.out.println(i);
			final  Map<String, SignalWord> signalWords = getSignalWords(proximityLines, proximityWeights);

			final String line = messages.next().getMessage();
			if (line.startsWith("!"))
				continue;
			final String[] lineWords = line.split(" ");

			lengthWeights.put(lineWords.length, lengthWeights.containsKey(lineWords.length) ? lengthWeights.get(lineWords.length) + 1 : 1);

			for (int j = 0; j < lineWords.length; j++) {
				final String currentWord = lineWords[j];
//				System.out.print(" " + currentWord);

				if (!containsWord(words, currentWord))
					words.put(currentWord, new Word(currentWord));

				final Word wordStruct = getWord(words, currentWord);
				wordStruct.signalWords = combineSignals(wordStruct.signalWords, signalWords);

				final String nextWord = (j == lineWords.length - 1) ? null : lineWords[j+1];
				if (nextWord != null && !containsString(wordStruct.followedWords, nextWord))
					wordStruct.followedWords.add(nextWord);

				if (j == 0)
					firstWords.add(wordStruct.word);
				else if (j == lineWords.length - 1)
					lastWords.add(wordStruct.word);
			}

			proximityLines.addFirst(line);

			if (proximityLines.size() > PROXIMITY_LINE_COUNT)
				proximityLines.removeLast();
//			System.out.println();
			i++;
		}

		return new ChatMimicDictionary(firstWords, words, lastWords, compileLengthWeights(lengthWeights));
	}

	/* INTERNAL */

	private static int[] compileLengthWeights(final Map<Integer, Integer> lengthWeights) {
		int maxLength = -1;
		for (final Map.Entry<Integer, Integer> e : lengthWeights.entrySet())
			maxLength = (e.getKey() > maxLength) ? e.getKey() : maxLength;

		final int[] result = new int[maxLength];
		Arrays.fill(result, 0);
//		System.out.println("Max length: " + maxLength);

		for (final Map.Entry<Integer, Integer> e : lengthWeights.entrySet())
			result[e.getKey() - 1] = e.getValue();

		return result;
	}

	public static double[] getProximityWeights(final int lineCount) {
		final double[] weights = new double[lineCount];
		final double totalSpan = 0.75;
		final double weightMultiplier = totalSpan/lineCount;

		for (int i = 0; i < weights.length; i++)
			weights[i] = 1.0 - (weightMultiplier * i);

		return weights;
	}

	public static  Map<String, SignalWord> getSignalWords(final List<String> lines, final double[] proximityWeights) {
		final Map<String, SignalWord> signalWords = new HashMap<>();

		for (int i = 0; i < lines.size(); i++) {
			final String[] words = lines.get(i).split(" ");

			for (final String s : words)
				if (!containsSignalWord(signalWords, s))
					signalWords.put(s, new SignalWord(s, proximityWeights[i]));
				else
					getSignalWord(signalWords, s).signal += proximityWeights[i];
		}

		return signalWords;
	}

	private static Map<String, SignalWord> combineSignals(final  Map<String, SignalWord> s1, final  Map<String, SignalWord> s2) {
		final  Map<String, SignalWord> result = new HashMap<>();

		for (final Map.Entry<String, SignalWord> s : s1.entrySet())
			result.put(s.getKey(), new SignalWord(s.getValue().word, s.getValue().signal));

		for (final Map.Entry<String, SignalWord> s : s2.entrySet())
			if (containsSignalWord(result, s.getValue().word))
				getSignalWord(result, s.getValue().word).signal += s.getValue().signal;
			else
				result.put(s.getKey(), new SignalWord(s.getValue().word, s.getValue().signal));

		return result;
	}

	private static boolean containsWord(final Map<String, Word> collection, final String word) {
		return collection.containsKey(word);
	}

	public static boolean containsSignalWord(final Map<String, SignalWord> collection, final String word) {
		return collection.containsKey(word);
	}

	private static Word getWord(final Map<String, Word> collection, final String word) {
		return collection.get(word);
	}

	public static SignalWord getSignalWord(final Map<String, SignalWord> collection, final String word) {
		return collection.get(word);
	}

	private static boolean containsString(final Collection<String> collection, final String word) {
		for (final String s : collection)
			if (s.equals(word))
				return true;
		return false;
	}
}
