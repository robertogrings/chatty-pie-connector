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

package com.chattypie.support;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonParser {

	public static JsonNode readJson(InputStream streamContainingJson) {
		try {
			ObjectMapper jsonReader = theSameObjectMapperAsSpring();
			return jsonReader.readTree(streamContainingJson);
		} catch (IOException e) {
			throw new RuntimeException("Could not read from the stream: closed or broken?", e);
		}
	}

	private static ObjectMapper theSameObjectMapperAsSpring() {
		return Jackson2ObjectMapperBuilder.json().build();
	}
}
