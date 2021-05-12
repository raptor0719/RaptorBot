package raptor.bot.tts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PhonemeParser {
	private static final Map<String, List<Phoneme>> GRAPHEME_MAP;
	private static final int MAX_LENGTH_GRAPHEME;

	static {
		final Map<Phoneme, String[]> graphemes = new HashMap<>();

		graphemes.put(Phoneme.B, new String[] {"b", "bb"});
		graphemes.put(Phoneme.D, new String[] {"d", "dd", "ed"});
		graphemes.put(Phoneme.F, new String[] {"f", "ff", "ph", "gh", "lf", "ft"});
		graphemes.put(Phoneme.G, new String[] {"g", "gg", "gh", "gu", "gue"});
		graphemes.put(Phoneme.H, new String[] {"h", "wh"});
		graphemes.put(Phoneme.DGE, new String[] {"j", "ge", "g", "dge", "di", "gg"});
		graphemes.put(Phoneme.CK, new String[] {"k", "c", "ch", "cc", "lk", "qu", "ck", "x"});
		graphemes.put(Phoneme.L, new String[] {"l", "ll"});
		graphemes.put(Phoneme.M, new String[] {"m", "mm", "mb", "mn", "lm"});
		graphemes.put(Phoneme.N, new String[] {"n", "nn", "kn", "gn", "pn", "mn"});
		graphemes.put(Phoneme.P, new String[] {"p", "pp"});
		graphemes.put(Phoneme.R, new String[] {"r", "rr", "wr", "rh"});
		graphemes.put(Phoneme.S, new String[] {"s", "ss", "c", "sc", "ps", "st", "ce", "se"});
		graphemes.put(Phoneme.T, new String[] {"t", "tt", "th", "ed"});
		graphemes.put(Phoneme.V, new String[] {"v", "f", "ph", "ve"});
		graphemes.put(Phoneme.W, new String[] {"w", "wh", "u", "o"});
		graphemes.put(Phoneme.Z, new String[] {"z", "zz", "s", "ss", "x", "ze", "se"});
		graphemes.put(Phoneme.SZ, new String[] {"s", "si", "z"});
		graphemes.put(Phoneme.CH, new String[] {"ch", "tch", "tu", "ti", "te"});
		graphemes.put(Phoneme.SH, new String[] {"sh", "ce", "s", "ci", "si", "ch", "sci", "ti"});
		graphemes.put(Phoneme.THS, new String[] {"th"});
		graphemes.put(Phoneme.THZ, new String[] {"th"});
		graphemes.put(Phoneme.NG, new String[] {"ng", "n", "ngue"});
		graphemes.put(Phoneme.Y, new String[] {"y", "i", "j"});
		graphemes.put(Phoneme.A, new String[] {"a", "ai", "au"});
		graphemes.put(Phoneme.AI, new String[] {"a", "ai", "eigh", "aigh", "ay", "er", "et", "ei", "au", "a_e", "ea", "ey"});
		graphemes.put(Phoneme.E, new String[] {"e", "ea", "u", "ie", "ai", "a", "eo", "ei", "ae"});
		graphemes.put(Phoneme.EE, new String[] {"e", "ee", "ea", "y", "ey", "oe", "ie", "i", "ei", "eo", "ay"});
		graphemes.put(Phoneme.I, new String[] {"i", "e", "o", "u", "ui", "y", "ie"});
		graphemes.put(Phoneme.IE, new String[] {"i", "y", "igh", "ie", "uy", "ye", "ai", "is", "eigh", "i_e"});
		graphemes.put(Phoneme.AWH, new String[] {"a", "ho", "au", "aw", "ough"});
		graphemes.put(Phoneme.OE, new String[] {"o", "oa", "o_e", "oe", "ow", "ough", "eau", "oo", "ew"});
		graphemes.put(Phoneme.OO, new String[] {"o", "oo", "u", "ou"});
		graphemes.put(Phoneme.UH, new String[] {"u", "o", "oo", "ou"});
		graphemes.put(Phoneme.UE, new String[] {"o", "oo", "ew", "ue", "u_e", "oe", "ough", "ui", "oew", "ou"});
		graphemes.put(Phoneme.OI, new String[] {"oi", "oy", "uoy"});
		graphemes.put(Phoneme.OW, new String[] {"ow", "ou", "ough"});
		graphemes.put(Phoneme.UR, new String[] {"a", "er", "i", "ar", "our", "ur"});
		graphemes.put(Phoneme.AIR, new String[] {"air", "are", "ear", "ere", "eir", "ayer"});
		graphemes.put(Phoneme.AH, new String[] {"a"});
		graphemes.put(Phoneme.IR, new String[] {"ir", "er", "ur", "ear", "or", "our", "yr"});
		graphemes.put(Phoneme.AW, new String[] {"aw", "a", "or", "oor", "ore", "oar", "our", "augh", "ar", "ough", "au"});
		graphemes.put(Phoneme.EER, new String[] {"ear", "eer", "ere", "ier"});
		graphemes.put(Phoneme.URE, new String[] {"ure", "our"});

		final Map<String, List<Phoneme>> graphemeMap = new HashMap<>();
		int maxLengthGrapheme = -1;

		for (final Map.Entry<Phoneme, String[]> entry : graphemes.entrySet()) {
			for (final String grapheme : entry.getValue()) {
				if (!graphemeMap.containsKey(grapheme)) {
					final List<Phoneme> phonemeList = new ArrayList<>();
					graphemeMap.put(grapheme, phonemeList);
				}

				graphemeMap.get(grapheme).add(entry.getKey());

				maxLengthGrapheme = Math.max(maxLengthGrapheme, grapheme.length());
			}
		}

		GRAPHEME_MAP = Collections.unmodifiableMap(graphemeMap);
		MAX_LENGTH_GRAPHEME = maxLengthGrapheme;
	}

	public List<Phoneme> parse(final String input) throws Exception {
		final List<Phoneme> phonemes = new ArrayList<>();

		final String[] sentences = input.split("[!?.][ ]?");

		for (final String sentence : sentences) {
			parseSentence(sentence, phonemes);
			phonemes.add(Phoneme.SENTENCE_SPACE);
		}

		return phonemes;
	}

	private void parseSentence(final String sentence, final List<Phoneme> phonemes) throws Exception {
		final String[] words = sentence.split("[;,]?[ ]|[;,]");

		for (final String word : words) {
			parseWord(word, phonemes);
			phonemes.add(Phoneme.WORD_SPACE);
		}
	}

	private void parseWord(final String word, final List<Phoneme> phonemes) throws Exception {
		String current = word;

		while (current.length() > 0) {
			final int maxLength = Math.min(MAX_LENGTH_GRAPHEME, current.length());

			String potentialGrapheme = null;
			boolean foundGrapheme = false;
			for (int l = maxLength; l > 0; l--) {
				potentialGrapheme = current.substring(0, l);

				if (!GRAPHEME_MAP.containsKey(potentialGrapheme))
					continue;

				foundGrapheme = true;
				break;
			}

			if (!foundGrapheme)
				throw new Exception("Unable to find valid grapheme for word: " + word);

			current = current.substring(potentialGrapheme.length());
			phonemes.add(getPhoneme(potentialGrapheme));
		}
	}

	private Phoneme getPhoneme(final String grapheme) {
		return GRAPHEME_MAP.get(grapheme).get(0);
	}
}
