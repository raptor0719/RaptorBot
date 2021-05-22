package raptor.bot.tts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PhonemeParser {
	private static final Map<String, List<Phoneme>> GRAPHEME_MAP;
	private static final Map<String, List<Phoneme>> SPLIT_GPAPHEME_MAP;
	private static final int MAX_LENGTH_GRAPHEME;

	static {
		final Map<Phoneme, String[]> graphemes = new HashMap<>();

		graphemes.put(Phoneme.B, new String[] {"b", "bb"});
		graphemes.put(Phoneme.D, new String[] {"d", "dd"});
		graphemes.put(Phoneme.F, new String[] {"f", "ff", "ph"});
		graphemes.put(Phoneme.G, new String[] {"g", "gg", "gh"});
		graphemes.put(Phoneme.H, new String[] {"h", "wh"});
		graphemes.put(Phoneme.DGE, new String[] {"j", "dge"});
		graphemes.put(Phoneme.CK, new String[] {"k", "c", "cc", "ck", "q"});
		graphemes.put(Phoneme.L, new String[] {"l", "ll"});
		graphemes.put(Phoneme.M, new String[] {"m", "mm"});
		graphemes.put(Phoneme.N, new String[] {"n", "nn"});
		graphemes.put(Phoneme.P, new String[] {"p", "pp"});
		graphemes.put(Phoneme.R, new String[] {"r", "rr"});
		graphemes.put(Phoneme.S, new String[] {"s", "ss"});
		graphemes.put(Phoneme.T, new String[] {"t", "tt"});
		graphemes.put(Phoneme.V, new String[] {"v"});
		graphemes.put(Phoneme.W, new String[] {"w"});
		graphemes.put(Phoneme.Z, new String[] {"z", "zz"});
		graphemes.put(Phoneme.SZ, new String[] {});
		graphemes.put(Phoneme.CH, new String[] {"ch", "tch"});
		graphemes.put(Phoneme.SH, new String[] {"sh"});
		graphemes.put(Phoneme.THS, new String[] {"th"});
		graphemes.put(Phoneme.THZ, new String[] {});
		graphemes.put(Phoneme.NG, new String[] {"ng", "ngue"});
		graphemes.put(Phoneme.Y, new String[] {"y"});
		graphemes.put(Phoneme.A, new String[] {"a"});
		graphemes.put(Phoneme.AY, new String[] {"a", "eigh", "aigh", "ay", "ei","a_e", "ey"});
		graphemes.put(Phoneme.E, new String[] {"e"});
		graphemes.put(Phoneme.EE, new String[] {"ee", "ea", "e_e"});
		graphemes.put(Phoneme.I, new String[] {"i"});
		graphemes.put(Phoneme.IE, new String[] {"igh", "ie", "ye", "ai", "i_e"});
		graphemes.put(Phoneme.AWH, new String[] {"au", "aw", "ough"});
		graphemes.put(Phoneme.OE, new String[] {"o", "oa", "o_e", "oe", "ough", "eau"});
		graphemes.put(Phoneme.OO, new String[] {"oo"});
		graphemes.put(Phoneme.UH, new String[] {"u"});
		graphemes.put(Phoneme.UE, new String[] {"ew", "ue", "u_e", "oew"});
		graphemes.put(Phoneme.OI, new String[] {"oi", "oy", "uoy"});
		graphemes.put(Phoneme.OW, new String[] {"ow", "ou"});
		graphemes.put(Phoneme.UR, new String[] {"our", "ur"});
		graphemes.put(Phoneme.AIR, new String[] {"air", "ere", "eir", "ayer"});
		graphemes.put(Phoneme.AH, new String[] {"ah"});
		graphemes.put(Phoneme.IR, new String[] {"ir", "ear", "yr"});
		graphemes.put(Phoneme.AW, new String[] {"aw", "augh"});
		graphemes.put(Phoneme.EER, new String[] {"ear", "eer", "ere", "ier"});
		graphemes.put(Phoneme.URE, new String[] {"ure"});

		final Map<String, List<Phoneme>> graphemeMap = new HashMap<>();
		final Map<String, List<Phoneme>> splitGraphemeMap = new HashMap<>();
		int maxLengthGrapheme = -1;

		final List<Map.Entry<Phoneme, String[]>> sortedEntries = graphemes.entrySet().stream().sorted(new Comparator<Map.Entry<Phoneme, String[]>>() {
			@Override
			public int compare(final Map.Entry<Phoneme, String[]> o1, final Map.Entry<Phoneme, String[]> o2) {
				return o1.getKey().name().compareTo(o2.getKey().name());
			}
		}).collect(Collectors.toList());

		for (final Map.Entry<Phoneme, String[]> entry : sortedEntries) {
			for (final String grapheme : entry.getValue()) {
				if (grapheme.contains("_")) {
					if (!splitGraphemeMap.containsKey(grapheme)) {
						final List<Phoneme> phonemeList = new ArrayList<>();
						splitGraphemeMap.put(grapheme, phonemeList);
					}

					splitGraphemeMap.get(grapheme).add(entry.getKey());

					maxLengthGrapheme = Math.max(maxLengthGrapheme, grapheme.length());

					continue;
				}

				if (!graphemeMap.containsKey(grapheme)) {
					final List<Phoneme> phonemeList = new ArrayList<>();
					graphemeMap.put(grapheme, phonemeList);
				}

				graphemeMap.get(grapheme).add(entry.getKey());

				maxLengthGrapheme = Math.max(maxLengthGrapheme, grapheme.length());
			}
		}

		GRAPHEME_MAP = Collections.unmodifiableMap(graphemeMap);
		SPLIT_GPAPHEME_MAP = Collections.unmodifiableMap(splitGraphemeMap);
		MAX_LENGTH_GRAPHEME = maxLengthGrapheme;
	}

	private final DefinedWordPhonemeManager definedWordManager;

	public PhonemeParser() {
		this(new DefinedWordPhonemeManager(Collections.emptyMap()));
	}

	public PhonemeParser(final DefinedWordPhonemeManager definedWordManager) {
		this.definedWordManager = definedWordManager;
	}

	public List<Phoneme> parse(final String input) throws Exception {
		final String uncased = input.toLowerCase();

		final List<Phoneme> phonemes = new ArrayList<>();

		final String[] sentences = uncased.split("[!?.][ ]?");

		for (int i = 0; i < sentences.length; i++) {
			parseSentence(sentences[i], phonemes);

			if (i < sentences.length - 1)
				phonemes.add(Phoneme.SENTENCE_SPACE);
		}

		return phonemes;
	}

	private void parseSentence(final String sentence, final List<Phoneme> phonemes) throws Exception {
		final String[] words = sentence.split("[;,]?[ ]|[;,]");

		for (int i = 0; i < words.length; i++) {
			parseWord(words[i], phonemes);

			if (i < words.length - 1)
				phonemes.add(Phoneme.WORD_SPACE);
		}
	}

	private void parseWord(final String word, final List<Phoneme> phonemes) throws Exception {
		if (definedWordManager.isDefined(word)) {
			phonemes.addAll(definedWordManager.getPhonemes(word));
			return;
		}

		String current = word;

		while (current.length() > 0) {
			final int maxLength = Math.min(MAX_LENGTH_GRAPHEME, current.length());

			boolean foundGrapheme = false;
			for (int l = maxLength; l > 0; l--) {
				final String potentialGrapheme = current.substring(0, l);

				if (matchesSplitGrapheme(potentialGrapheme)) {
					final String splitGrapheme = potentialGrapheme.charAt(0) + "_" + potentialGrapheme.charAt(2);
					phonemes.add(getPhoneme(splitGrapheme));

					current = current.charAt(1) + ((current.length() > 3) ? current.substring(3) : "");

					foundGrapheme = true;
					break;
				} else if (GRAPHEME_MAP.containsKey(potentialGrapheme)) {
					phonemes.add(getPhoneme(potentialGrapheme));

					current = current.substring(potentialGrapheme.length());

					foundGrapheme = true;
					break;
				}
			}

			if (!foundGrapheme)
				throw new Exception("Unable to find valid grapheme for word: " + word);
		}
	}

	private boolean matchesSplitGrapheme(final String grapheme) {
		for (final Map.Entry<String, List<Phoneme>> e : SPLIT_GPAPHEME_MAP.entrySet()) {
			final String matchString = e.getKey().charAt(0) + "[a-zA-Z]" + e.getKey().charAt(2);
			if (grapheme.matches(matchString))
				return true;
		}
		return false;
	}

	private Phoneme getPhoneme(final String grapheme) {
		return (grapheme.contains("_")) ?
				SPLIT_GPAPHEME_MAP.get(grapheme).get(getIndex(SPLIT_GPAPHEME_MAP.get(grapheme).size())) :
					GRAPHEME_MAP.get(grapheme).get(getIndex(GRAPHEME_MAP.get(grapheme).size()));
	}

	private int getIndex(final int count) {
		return 0;
	}
}
