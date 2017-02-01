package com.chattypie.util;

import static java.time.Duration.ofMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.google.common.base.Stopwatch;

public class DelayerTest {

	@Test
	public void delaysCurrentThread_forProperAmountOfTime() throws Exception {
		Stopwatch stopWatch = Stopwatch.createStarted();

		new Delayer().delayFor(ofMillis(500));

		assertThat(stopWatch.elapsed(MILLISECONDS)).isGreaterThanOrEqualTo(500L);
	}
}
