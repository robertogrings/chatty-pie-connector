package com.chattypie.support.fake;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.DisposableBean;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

public class FakeEmailServer implements DisposableBean {
	private GreenMail greenMailServer;
	private int port;

	static FakeEmailServer create(int port) {
		return new FakeEmailServer(port);
	}

	private FakeEmailServer(int port) {
		this.port = port;
		greenMailServer = new GreenMail(new ServerSetup(port, "127.0.0.1", ServerSetup.PROTOCOL_SMTP));
	}

	FakeEmailServer start() {
		greenMailServer.start();
		return this;
	}

	@Override
	public void destroy() throws Exception {
		greenMailServer.stop();
	}

	int getPort() {
		return port;
	}

	public String lastMessageSubject() throws MessagingException {
		MimeMessage[] receivedMessages = greenMailServer.getReceivedMessages();
		MimeMessage lastMessage = receivedMessages[receivedMessages.length - 1];
		return lastMessage.getSubject();
	}
}
