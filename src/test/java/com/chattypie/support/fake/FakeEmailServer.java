/*
 * Copyright 2017 AppDirect, Inc. and/or its affiliates
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
