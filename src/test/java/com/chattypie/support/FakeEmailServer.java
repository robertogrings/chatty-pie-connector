package com.chattypie.support;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class FakeEmailServer {
	private GreenMail greenMailServer;

	public static FakeEmailServer create(int port) {
		return new FakeEmailServer(port);
	}

	private FakeEmailServer(int port) {
		greenMailServer = new GreenMail(new ServerSetup(port, "127.0.0.1", ServerSetup.PROTOCOL_SMTP));
	}

	public FakeEmailServer start() {
		greenMailServer.start();
		return this;
	}

	public void stop() {
		greenMailServer.stop();
	}

	public String lastMessageSubject() throws MessagingException {
		MimeMessage[] receivedMessages = greenMailServer.getReceivedMessages();
		MimeMessage lastMessage = receivedMessages[receivedMessages.length - 1];
		return lastMessage.getSubject();
	}
}
