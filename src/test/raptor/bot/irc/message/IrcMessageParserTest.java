package raptor.bot.irc.message;

import org.junit.Assert;
import org.junit.Test;

import raptor.bot.irc.message.messages.ClientMessage;
import raptor.bot.irc.message.messages.JoinDeclarationMessage;
import raptor.bot.irc.message.messages.ModeDeclarationMessage;
import raptor.bot.irc.message.messages.NoticeMessage;
import raptor.bot.irc.message.messages.NumericServerReplyMessage;
import raptor.bot.irc.message.messages.PingMessage;

public class IrcMessageParserTest {
	@Test
	public void basicTest() {
		final String init1 = ":raptor.irc NOTICE * :*** Looking up your hostname...";
		Assert.assertEquals(new NoticeMessage("*** Looking up your hostname...", "raptor.irc"), IrcMessageParser.parseIrcMessage(init1));

		final String init2 = ":raptor.irc NOTICE * :*** Couldn't resolve your hostname; using your IP address instead";
		Assert.assertEquals(new NoticeMessage("*** Couldn't resolve your hostname; using your IP address instead", "raptor.irc"), IrcMessageParser.parseIrcMessage(init2));

		final String ping1 = "PING :D2CA925F";
		Assert.assertEquals(new PingMessage("D2CA925F"), IrcMessageParser.parseIrcMessage(ping1));

		final String numericReply001 = ":raptor.irc 001 CABRAL :Welcome to the RaptorNet IRC Network CABRAL!guest@192.168.1.26";
		Assert.assertEquals(new NumericServerReplyMessage("Welcome to the RaptorNet IRC Network CABRAL!guest@192.168.1.26", "raptor.irc", "001", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply001));

		final String numericReply002 = ":raptor.irc 002 CABRAL :Your host is raptor.irc, running version UnrealIRCd-4.2.3";
		Assert.assertEquals(new NumericServerReplyMessage("Your host is raptor.irc, running version UnrealIRCd-4.2.3", "raptor.irc", "002", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply002));

		final String numericReply003 = ":raptor.irc 003 CABRAL :This server was created Tue Apr 30 09:48:52 2019";
		Assert.assertEquals(new NumericServerReplyMessage("This server was created Tue Apr 30 09:48:52 2019", "raptor.irc", "003", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply003));

		final String numericReply004 = ":raptor.irc 004 CABRAL raptor.irc UnrealIRCd-4.2.3 iowrsxzdHtIDZRqpWGTSB lvhopsmntikraqbeIzMQNRTOVKDdGLPZSCcf";
		Assert.assertEquals(new NumericServerReplyMessage("", "raptor.irc", "004", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply004));

		final String numericReply005_1 = ":raptor.irc 005 CABRAL AWAYLEN=307 CASEMAPPING=ascii CHANLIMIT=#:10 CHANMODES=beI,kLf,l,psmntirzMQNRTOVKDdGPZSCc CHANNELLEN=32 CHANTYPES=# DEAF=d ELIST=MNUCT EXCEPTS EXTBAN=~,tmTSOcaRrnqj HCN INVEX :are supported by this server";
		Assert.assertEquals(new NumericServerReplyMessage("are supported by this server", "raptor.irc", "005", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply005_1));

		final String numericReply005_2 = ":raptor.irc 005 CABRAL KICKLEN=307 KNOCK MAP MAXCHANNELS=10 MAXLIST=b:60,e:60,I:60 MAXNICKLEN=30 MODES=12 NAMESX NETWORK=RaptorNet NICKLEN=30 PREFIX=(qaohv)~&@%+ QUITLEN=307 :are supported by this server";
		Assert.assertEquals(new NumericServerReplyMessage("are supported by this server", "raptor.irc", "005", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply005_2));

		final String numericReply005_3 = ":raptor.irc 005 CABRAL SAFELIST SILENCE=15 STATUSMSG=~&@%+ TARGMAX=DCCALLOW:,ISON:,JOIN:,KICK:4,KILL:,LIST:,NAMES:1,NOTICE:1,PART:,PRIVMSG:4,SAJOIN:,SAPART:,USERHOST:,USERIP:,WATCH:,WHOIS:1,WHOWAS:1 TOPICLEN=360 UHNAMES USERIP WALLCHOPS WATCH=128 WATCHOPTS=A :are supported by this server";
		Assert.assertEquals(new NumericServerReplyMessage("are supported by this server", "raptor.irc", "005", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply005_3));

		final String numericReply396 = ":raptor.irc 396 CABRAL 966B23D5.E90A6C54.8B5E3F59.IP :is now your displayed host";
		Assert.assertEquals(new NumericServerReplyMessage("is now your displayed host", "raptor.irc", "396", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply396));

		final String numericReply251 = ":raptor.irc 251 CABRAL :There are 1 users and 1 invisible on 1 servers";
		Assert.assertEquals(new NumericServerReplyMessage("There are 1 users and 1 invisible on 1 servers", "raptor.irc", "251", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply251));

		final String numericReply254 = ":raptor.irc 254 CABRAL 1 :channels formed";
		Assert.assertEquals(new NumericServerReplyMessage("channels formed", "raptor.irc", "254", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply254));

		final String numericReply255 = ":raptor.irc 255 CABRAL :I have 2 clients and 0 servers";
		Assert.assertEquals(new NumericServerReplyMessage("I have 2 clients and 0 servers", "raptor.irc", "255", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply255));

		final String numericReply265 = ":raptor.irc 265 CABRAL 2 2 :Current local users 2, max 2";
		Assert.assertEquals(new NumericServerReplyMessage("Current local users 2, max 2", "raptor.irc", "265", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply265));

		final String numericReply266 = ":raptor.irc 266 CABRAL 2 2 :Current global users 2, max 2";
		Assert.assertEquals(new NumericServerReplyMessage("Current global users 2, max 2", "raptor.irc", "266", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply266));

		final String numericReply422 = ":raptor.irc 422 CABRAL :MOTD File is missing";
		Assert.assertEquals(new NumericServerReplyMessage("MOTD File is missing", "raptor.irc", "422", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply422));

		final String modeDeclaration = ":CABRAL MODE CABRAL :+iwx";
		Assert.assertEquals(new ModeDeclarationMessage("+iwx", "CABRAL", "CABRAL"), IrcMessageParser.parseIrcMessage(modeDeclaration));

		final String joinDeclaration = ":CABRAL!guest@966B23D5.E90A6C54.8B5E3F59.IP JOIN :#general";
		Assert.assertEquals(new JoinDeclarationMessage("#general", "CABRAL!guest@966B23D5.E90A6C54.8B5E3F59.IP"), IrcMessageParser.parseIrcMessage(joinDeclaration));

		final String numericReply353 = ":raptor.irc 353 CABRAL = #general :CABRAL @raptor0719";
		Assert.assertEquals(new NumericServerReplyMessage("CABRAL @raptor0719", "raptor.irc", "353", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply353));

		final String numericReply366 = ":raptor.irc 366 CABRAL #general :End of /NAMES list.";
		Assert.assertEquals(new NumericServerReplyMessage("End of /NAMES list.", "raptor.irc", "366", "CABRAL"), IrcMessageParser.parseIrcMessage(numericReply366));

		final String clientMessage1 = ":raptor0719!raptor0719@966B23D5.E90A6C54.8B5E3F59.IP PRIVMSG #general :hello";
		Assert.assertEquals(new ClientMessage("hello", "raptor0719!raptor0719@966B23D5.E90A6C54.8B5E3F59.IP", "#general"), IrcMessageParser.parseIrcMessage(clientMessage1));

		final String clientMessage2 = ":raptor0719!raptor0719@966B23D5.E90A6C54.8B5E3F59.IP PRIVMSG #general :how are you";
		Assert.assertEquals(new ClientMessage("how are you", "raptor0719!raptor0719@966B23D5.E90A6C54.8B5E3F59.IP", "#general"), IrcMessageParser.parseIrcMessage(clientMessage2));

		final String clientMessage3 = ":raptor0719!raptor0719@966B23D5.E90A6C54.8B5E3F59.IP PRIVMSG #general :you piece of motherless sack of monkey yellow bellied";
		Assert.assertEquals(new ClientMessage("you piece of motherless sack of monkey yellow bellied", "raptor0719!raptor0719@966B23D5.E90A6C54.8B5E3F59.IP", "#general"), IrcMessageParser.parseIrcMessage(clientMessage3));
	}
}
