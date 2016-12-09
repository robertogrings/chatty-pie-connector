package com.chattypie.util;

import java.time.Duration;

public class Delayer {
	public void delayFor(Duration duration) {
		try {
			Thread.sleep(duration.toMillis());
		} catch (InterruptedException ignored) { // NOSONAR: no need to log this
			Thread.currentThread().interrupt();
		}
	}
}
