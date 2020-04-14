package raptor.bot.command;

import raptor.bot.api.IParser;
import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.HelpCommand;
import raptor.bot.command.commands.MemeCommand;
import raptor.bot.command.commands.SoundCommand;
import raptor.bot.command.commands.WisdomCommand;
import raptor.bot.command.commands.alias.AliasCreateCommand;
import raptor.bot.command.commands.alias.AliasDeleteCommand;
import raptor.bot.command.commands.alias.AliasListCommand;
import raptor.bot.command.commands.chatstats.ChatStatsCommand;
import raptor.bot.command.commands.chatstats.ChatStatsUserMessageCountCommand;
import raptor.bot.command.commands.madlib.MadlibCommand;
import raptor.bot.command.commands.madlib.MadlibFillCommand;
import raptor.bot.command.commands.madlib.MadlibFormatCommand;

public enum BotMethod {
	SOUND("sound", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			return new SoundCommand(s.split(" ")[0].toLowerCase());
		}
	}),
	HELP("help", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			return new HelpCommand(s.split(" ")[0].toLowerCase());
		}
	}),
	ALIAS("alias", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			final String action = s.split(" ")[0].toLowerCase();
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
	}),
	MADLIB("madlib", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			final String action = s.split(" ")[0].toLowerCase();
			final String remaining = s.substring(s.indexOf(" ") + 1);

			switch (action) {
				case MadlibFillCommand.SUB_COMMAND_WORD:
					return new MadlibFillCommand(remaining);
				case MadlibFormatCommand.SUB_COMMAND_WORD:
					return new MadlibFormatCommand();
				default:
					return new MadlibCommand("");
			}
		}
	}),
	CHAT_STATS("chatstats", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			return ("".equals(s)) ? new ChatStatsCommand() : new ChatStatsUserMessageCountCommand(s.split(" ")[0]);
		}
	}),
	WISDOM("wisdom", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			return ("".equals(s)) ? new WisdomCommand() : new WisdomCommand(Integer.parseInt(s.split(" ")[0]));
		}
	}),
	MEME("meme", new IParser<BotCommand>() {
		@Override
		public BotCommand parse(final String s) {
			return ("".equals(s)) ? new MemeCommand() : new MemeCommand(s.split(" ")[0]);
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
