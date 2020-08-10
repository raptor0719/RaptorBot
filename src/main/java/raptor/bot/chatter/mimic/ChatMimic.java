package raptor.bot.chatter.mimic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import raptor.bot.chatter.mimic.structures.SignalWord;

public class ChatMimic {
	private final ChatMimicDictionary dictionary;

	public ChatMimic(final ChatMimicDictionary dictionary) {
		this.dictionary = dictionary;
	}

	public String mimic(final List<String> proximityLines) {
		final double[] proximityWeights = ChatMimicDictionary.getProximityWeights(proximityLines.size());
		final  Map<String, SignalWord> signalingWords = ChatMimicDictionary.getSignalWords(proximityLines, proximityWeights);

		final int targetLength = generateLength(dictionary.getLengthWeights());
		System.out.println("Target Length: " + targetLength);
		System.out.println("Signaling words length: " + signalingWords.size());

		final  Map<String, SignalWord> weightedFirstWords = getWeightedWords(dictionary.getFirstWords(), signalingWords);
		final String firstWord = generateWord(weightedFirstWords);

		String phrase = "";
		String currentWord = firstWord;
		int wordCount = 0;
		while (currentWord != null) {
			System.out.println("Phrase: " + phrase);
			System.out.println("Current word: " + currentWord);
			phrase = phrase + " " + currentWord;
			wordCount++;

			if (wordCount >= targetLength) {
				final List<String> validLastWords = getValidLastWords(dictionary.getFollowingWords(currentWord), dictionary.getLastWords());
				if (validLastWords.size() > 0) {
					final  Map<String, SignalWord> weightedLastWords = getWeightedWords(validLastWords, signalingWords);
					final String lastWord = generateWord(weightedLastWords);
					phrase = phrase + " " + lastWord;
					break;
				}
			}

			final  Map<String, SignalWord> weightedNextWords = getWeightedWords(dictionary.getFollowingWords(currentWord), signalingWords);
			final String generatedWord = generateWord(weightedNextWords);
			currentWord = generatedWord;
		}

		return phrase;
	}

	private List<String> getValidLastWords(final List<String> followedWords, final List<String> lastWords) {
		final List<String> result = new ArrayList<>();

		for (final String f : followedWords)
			for (final String l : lastWords)
				if (f.equals(l))
					result.add(f);

		return result;
	}

	private String generateWord(final  Map<String, SignalWord> weightedWords) {
		final List<SignalWord> signals = new ArrayList<>();
		final Iterator<SignalWord> iter = weightedWords.values().iterator();
		SignalWord curr;
		while (iter.hasNext()) {
			curr = iter.next();
			signals.add(curr);
		}

		System.out.println("\nGenerating a word...");
		final double total = getTotalSignal(weightedWords);
		System.out.println("Total: " + total);
		System.out.println("Weighted word count: " + signals.size());

		double generated = Math.random() * total;
		System.out.println("Generated: " + generated);
		int index = -1;
		while (generated > 0) {
			index++;
			generated -= signals.get(index).signal;
		}
		System.out.println("Index: " + index);

		return (index < 0) ? null : signals.get(index).word;
	}

	private int generateLength(final int[] lengthWeights) {
		int total = 0;
		for (final int i : lengthWeights)
			total += i;
		int generated = (int) (Math.random() * total);
		int index = -1;
		while (generated > 0) {
			index++;
			generated -= lengthWeights[index];
		}
		return index;
	}

	private double getTotalSignal(final  Map<String, SignalWord> words) {
		double total = 0;
		for (final Map.Entry<String, SignalWord> e : words.entrySet())
			total += e.getValue().signal;
		return total;
	}

	private  Map<String, SignalWord> getWeightedWords(final List<String> words, final  Map<String, SignalWord> signalingWords) {
		final  Map<String, SignalWord> result = new HashMap<>();

		for (final String f : words) {
			for (final Map.Entry<String, SignalWord> e : signalingWords.entrySet()) {
				final SignalWord k = e.getValue();
				if (!dictionary.isSignaledBy(f, k.word))
					continue;

				final double historicalSignalStrength = dictionary.getSignal(f, k.word);
				final double proximitySignalStrength = k.signal;
				if (!ChatMimicDictionary.containsSignalWord(result, f))
					result.put(f, new SignalWord(f, historicalSignalStrength * proximitySignalStrength));
				else
					ChatMimicDictionary.getSignalWord(result, f).signal += historicalSignalStrength * proximitySignalStrength;
			}
		}

		return result;
	}
}
