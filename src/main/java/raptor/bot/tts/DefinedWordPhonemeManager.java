package raptor.bot.tts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinedWordPhonemeManager {
	private Map<String, List<Phoneme>> definedWordPhonemes;

	public DefinedWordPhonemeManager(final Map<String, List<Phoneme>> definedWordPhonemes) {
		this.definedWordPhonemes = definedWordPhonemes;
	}

	public boolean isDefined(final String word) {
		return definedWordPhonemes.containsKey(word.toLowerCase());
	}

	public List<Phoneme> getPhonemes(final String word) {
		if (!definedWordPhonemes.containsKey(word))
			return null;

		return definedWordPhonemes.get(word);
	}

	public static Map<String, List<Phoneme>> buildDefinedWordPhonemeMap(final InputStream input) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(input));

		final Map<String, List<Phoneme>> definedWords = new HashMap<>();

		String currentLine = reader.readLine();
		while (currentLine != null) {
			if ("".equals(currentLine.trim()) || currentLine.startsWith("#") || !currentLine.contains(" = ")) {
				currentLine = reader.readLine();
				continue;
			}

			final String[] parsed = currentLine.split(" = ");

			final List<Phoneme> phonemes = new ArrayList<>();
			for (final String phonemeId : parsed[1].split(" "))
				for (final Phoneme phoneme : Phoneme.values())
					if (phoneme.name().equalsIgnoreCase(phonemeId.trim()))
						phonemes.add(phoneme);

			definedWords.put(parsed[0].toLowerCase(), Collections.unmodifiableList(phonemes));

			currentLine = reader.readLine();
		}

		return Collections.unmodifiableMap(definedWords);
	}
}
