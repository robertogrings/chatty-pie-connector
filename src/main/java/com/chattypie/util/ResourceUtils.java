package com.chattypie.util;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import com.google.common.io.Resources;

@Slf4j
public final class ResourceUtils {
	private ResourceUtils() {
	}

	public static String readResourceFileAsString(String path) {
		try {
			return Resources.toString(
				getResource(path),
				UTF_8
			);
		} catch (IOException e) {
			log.error("Failed reading resource", e);
			throw new RuntimeException(e);
		}
	}
}
