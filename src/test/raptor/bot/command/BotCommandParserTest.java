package raptor.bot.command;

import org.junit.Assert;
import org.junit.Test;

import raptor.bot.command.commands.BotCommand;
import raptor.bot.command.commands.SoundCommand;

public class BotCommandParserTest {
	@Test
	public void soundCommand_correct() {
		final String message = "!sound ynot";
		final BotCommand command = BotCommandParser.parseBotCommand(message);
		Assert.assertEquals("sound", command.getCommand());
		final SoundCommand soundCommand = (SoundCommand)command;
		Assert.assertEquals("ynot", soundCommand.getSound());
	}

	@Test
	public void aliasCommand_correct() {
		final String message = "!ynot";
		final BotCommand command = BotCommandParser.parseBotCommand(message);
		Assert.assertEquals("ynot", command.getCommand());
	}
}
