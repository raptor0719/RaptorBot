package raptor.bot.utils;

import raptor.bot.api.IMadlibManager;
import raptor.bot.utils.words.WordBank;

public class MadlibManager implements IMadlibManager {
	private final WordBank wordBank;

	public MadlibManager(final WordBank wordBank) {
		this.wordBank = wordBank;
	}

	@Override
	public String fill(final String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFormat() {
		// TODO Auto-generated method stub
		return null;
	}

}
