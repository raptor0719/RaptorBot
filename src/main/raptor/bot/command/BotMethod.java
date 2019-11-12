package raptor.bot.command;

import raptor.bot.api.IParser;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.command.commands.alias.AliasCreateCommand;
import raptor.bot.command.commands.alias.AliasDeleteCommand;
import raptor.bot.command.commands.alias.AliasListCommand;

public enum BotMethod {
	SOUND("sound", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			return new SoundCommand(s.split(" ")[0]);
		}
	}),
	HELP("help", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			return new HelpCommand(s.split(" ")[0]);
		}
	}),
	ALIAS("alias", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			final String action = s.split(" ")[0];
			final String remaining = s.substring(s.indexOf(" ") + 1);

			switch (action) {
				case AliasCreateCommand.SUB_COMMAND_WORD:
					return new AliasCreateCommand(remaining.split(" ")[0], remaining.substring(remaining.indexOf(" ") + 1));
				case AliasDeleteCommand.SUB_COMMAND_WORD:
					return new AliasDeleteCommand(remaining.split(" ")[0]);
				default:
					return new AliasListCommand();
			}
		}
	});

	private final String word;
	private final IParser<BotCommand> parser;

	private BotMethod(final String word, final IParser<BotCommand> parser) {
		this.word = word;
		this.parser = parser;
	}

	public String getWord() {
		return word;
	}

	public BotCommand parse(final String s) {
		return parser.parse(s);
	}
}
