package raptor.bot.test.utils;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import raptor.bot.api.chat.IChatDatastore;
import raptor.bot.api.message.IMessageService;
import raptor.bot.irc.ChatMessage;
import raptor.bot.main.RaptorBot;

public class TestWindow extends JFrame {
	public TestWindow(final RaptorBot bot, final IMessageService<String, ChatMessage> botInputOutput, final IChatDatastore chatDatastore) {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(200, 200, 1000, 600);

		final JPanel container = new JPanel();
		container.setVisible(true);

		final JTextArea output = new JTextArea();
		output.setPreferredSize(new Dimension(800, 500));
		output.setEditable(false);
		output.setVisible(true);
		output.setLineWrap(true);

		final JTextField chatInput = new JTextField();
		chatInput.setPreferredSize(new Dimension(400, 25));
		chatInput.setVisible(true);

		final JButton submit = new JButton("Submit");
		submit.setPreferredSize(new Dimension(100, 25));
		submit.setVisible(true);

		final Action submitAction = new AbstractAction("submit") {
			@Override
			public void actionPerformed(ActionEvent e) {
				final String text = chatInput.getText();
				chatInput.setText("");

				botInputOutput.sendMessage(new ChatMessage("#TestWindow", "master!master@master.twitch.tv", text, System.currentTimeMillis()));
				output.insert("master: " + text + "\n", 0);
				bot.process();
				final Iterator<String> botResponses = botInputOutput.receiveMessages();
				while (botResponses.hasNext()) {
					final String message = botResponses.next();
					output.insert((message != null && !message.isEmpty()) ? "raptorbot: " + message + "\n" : "", 0);
				}
			}
		};
		submit.setAction(submitAction);
		submitAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_ENTER);
		submit.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "submit");
		submit.getActionMap().put("submit", submitAction);

		container.add(output);
		container.add(chatInput);
		container.add(submit);

		this.add(container);
		this.setVisible(true);

		chatInput.requestFocus();
	}
}
