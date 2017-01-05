package com.chattypie.support;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.amazonaws.services.cloudfront.model.InvalidArgumentException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class JsonParser {

	static JsonNode readJson(InputStream streamContainingJson) {
		try {
			ObjectMapper jsonReader = theSameObjectMapperAsSpring();
			return jsonReader.readTree(streamContainingJson);
		} catch (IOException e) {
			throw new InvalidArgumentException("Could not read from the stream: closed or broken?");
		}
	}

	private static ObjectMapper theSameObjectMapperAsSpring() {
		return Jackson2ObjectMapperBuilder.json().build();
	}
}
