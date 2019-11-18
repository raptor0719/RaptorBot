package raptor.bot.utils.words;

public enum PartOfSpeech {
	Noun("n"),
	Plural("pl"),
	Verb("v"),
	VerbTransitive("vt"),
	VerbIntransitive("vi"),
	Adjective("adj"),
	Adverb("adv"),
	Pronoun("pn"),
	Conjunction("con"),
	Interjection("int"),
	Preposition("prep"),
	DefiniteArticle("d"),
	IndefiniteArticle("i");

	private final String id;

	private PartOfSpeech(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
