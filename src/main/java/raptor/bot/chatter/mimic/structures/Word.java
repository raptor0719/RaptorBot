package raptor.bot.chatter.mimic.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Word {
	public String word;
	public  Map<String, SignalWord> signalWords;
	public List<String> followedWords;

	public Word(final String word) {
		this.word = word;
		signalWords = new HashMap<>();
		followedWords = new ArrayList<>();
	}
}
