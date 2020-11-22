package raptor.bot.chatter.mimic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import raptor.bot.utils.BinaryDataTools;

public class ChatMimicDictionary {
	public static final int PROXIMITY_LINE_COUNT = 30;
	private static final byte[] MAGIC_NUMBER = new byte[]{1, 3, 3, 7};

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
		System.out.println("Compiling ChatMimic Dictionary...");
		final int totalMessageCount = chatDatastore.getTotalMessageCount();

		final double[] proximityWeights = getProximityWeights(PROXIMITY_LINE_COUNT);
		final LinkedList<String> proximityLines = new LinkedList<>();

		final List<String> firstWords = new ArrayList<>();
		final List<String> lastWords = new ArrayList<>();
		final Map<String, Word> words = new HashMap<>();
		final Map<Integer, Integer> lengthWeights = new HashMap<>();

		final Iterator<ChatMessage> messages = chatDatastore.getMessagesInRange(0, totalMessageCount - 1);
		int i = 0;
		while (messages.hasNext()) {
			if (i%1000 == 0)
				System.out.println(String.format("Processed %s messages.", i));
			final  Map<String, SignalWord> signalWords = getSignalWords(proximityLines, proximityWeights);

			final String line = messages.next().getMessage();
			i++;
			if (line.startsWith("!"))
				continue;
			final String[] lineWords = line.split(" ");

			lengthWeights.put(lineWords.length, lengthWeights.containsKey(lineWords.length) ? lengthWeights.get(lineWords.length) + 1 : 1);

			for (int j = 0; j < lineWords.length; j++) {
				final String currentWord = lineWords[j];

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
		}

		System.out.println("Finished compiling ChatMimic dictionary!");
		return new ChatMimicDictionary(firstWords, words, lastWords, compileLengthWeights(lengthWeights));
	}

	public static void serialize(final ChatMimicDictionary dic, final OutputStream os) throws IOException {
		final DataOutputStream dos = new DataOutputStream(os);

		System.out.println("Serializing magic number...");
		dos.write(MAGIC_NUMBER);

		System.out.println("Serializing first words...");
		serializeStringList(dic.firstWords, os);

		System.out.println("Serializing words...");
		final List<Word> words = extractKeys(dic.words);
		dos.writeInt(words.size());
		for (final Word w : words)
			serializeWord(w, os);

		System.out.println("Serializing last words...");
		serializeStringList(dic.lastWords, os);

		System.out.println("Serializing length weights...");
		serializeIntArray(dic.lengthWeights, os);
	}

	public static ChatMimicDictionary marshal(final InputStream is) throws IOException {
		final DataInputStream dis = new DataInputStream(is);

		final byte[] magicNumber = new byte[4];
		dis.read(magicNumber);

		if (!Arrays.equals(magicNumber, MAGIC_NUMBER))
			throw new RuntimeException("File was not a mimdic file.");

		System.out.println("Marshalling first words...");
		final List<String> firstWords = marshalStringList(dis);

		System.out.println("Marshalling word list...");
		final int wordListLength = dis.readInt();
		final Map<String, Word> words = new HashMap<String, Word>();
		for (int i = 0; i < wordListLength; i++) {
			if (i % 100 == 0)
				System.out.println("- " + i + " out of " + wordListLength + " words marshalled.");
			final Word word = marshalWord(dis);
			words.put(word.word, word);
		}

		System.out.println("Marshalling last words...");
		final List<String> lastWords = marshalStringList(dis);

		System.out.println("Marshalling length weights...");
		final int[] lengthWeights = marshalIntArray(dis);

		System.out.println("Compiling dictionary...");
		return new ChatMimicDictionary(firstWords, words, lastWords, lengthWeights);
	}

	private static void serializeStringList(final List<String> list, final OutputStream os) throws IOException {
		final DataOutputStream dos = new DataOutputStream(os);

		dos.writeInt(list.size());

		for (final String s : list) {
			dos.write(BinaryDataTools.serializeString(s));
		}
	}

	private static List<String> marshalStringList(final DataInputStream dis) throws IOException {
		final int length = dis.readInt();
		final List<String> strs = new ArrayList<String>();
		for (int i = 0; i < length; i++)
			strs.add(BinaryDataTools.marshalString(dis));

		return strs;
	}

	private static void serializeIntArray(final int[] arr, final OutputStream os) throws IOException {
		final DataOutputStream dos = new DataOutputStream(os);

		dos.writeInt(arr.length);

		for (final int i : arr) {
			dos.writeInt(i);
		}
	}

	private static int[] marshalIntArray(final DataInputStream dis) throws IOException {
		final int length = dis.readInt();
		final int[] arr = new int[length];
		for (int i = 0; i < length; i++)
			arr[i] = dis.readInt();

		return arr;
	}

	private static void serializeWord(final Word w, final OutputStream os) throws IOException {
		final DataOutputStream dos = new DataOutputStream(os);

		dos.write(BinaryDataTools.serializeString(w.word));

		final List<SignalWord> signalWords = extractKeys(w.signalWords);
		dos.writeInt(signalWords.size());
		for (final SignalWord sw : signalWords) {
			serializeSignalWord(sw, os);
		}

		serializeStringList(w.followedWords, os);
	}

	private static Word marshalWord(final DataInputStream dis) throws IOException {
		final String wordLiteral = BinaryDataTools.marshalString(dis);

		final int signalWordsLength = dis.readInt();
		final Map<String, SignalWord> signalWords = new HashMap<String, SignalWord>();
		for (int i = 0; i < signalWordsLength; i++) {
			final SignalWord sw = marshalSignalWord(dis);
			signalWords.put(sw.word, sw);
		}

		final List<String> followedWords = marshalStringList(dis);

		return new Word(wordLiteral, signalWords, followedWords);
	}

	private static void serializeSignalWord(final SignalWord w, final OutputStream os) throws IOException {
		final DataOutputStream dos = new DataOutputStream(os);

		dos.write(BinaryDataTools.serializeString(w.word));
		dos.writeDouble(w.signal);
	}

	private static SignalWord marshalSignalWord(final DataInputStream dis) throws IOException {
		final String wordLiteral = BinaryDataTools.marshalString(dis);
		final double signal = dis.readDouble();

		return new SignalWord(wordLiteral, signal);
	}

	private static <T> List<T> extractKeys(final Map<?, T> map) {
		final List<T> list = new ArrayList<T>();

		for (final Map.Entry<?, T> e : map.entrySet())
			list.add(e.getValue());

		return list;
	}

	/* INTERNAL */

	private static int[] compileLengthWeights(final Map<Integer, Integer> lengthWeights) {
		int maxLength = -1;
		for (final Map.Entry<Integer, Integer> e : lengthWeights.entrySet())
			maxLength = (e.getKey() > maxLength) ? e.getKey() : maxLength;

		final int[] result = new int[maxLength];
		Arrays.fill(result, 0);

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
