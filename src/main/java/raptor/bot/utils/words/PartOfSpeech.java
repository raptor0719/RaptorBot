package raptor.bot.utils.words;

public enum PartOfSpeech {
	Noun("n"),
	PeopleNoun("N"),
	PresentVerb("v"),
	PastVerb("pv"),
	Adjective("adj"),
	Adverb("adv"),
	Interjection("int");

	private final String id;

	private PartOfSpeech(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
