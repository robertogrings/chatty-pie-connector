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

package com.chattypie.util;

import static java.lang.String.format;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds string maps out of raw strings.
 */
public class MapBuilder {
	/**
	 * Parses comma-delimited lists of colon-delimited key-value pairs.
	 * i.e. <code>key1:value1,color:red,favorite-fruit:banana</code>
	 * Trailing comma is optional.
	 *
	 * @param commaDelimitedKeyValuePairs string to parse, i.e. <code>key1:value1,key2:value2</code>
	 * @return The map version of this string
	 */
	public Map<String, String> fromCommaDelimitedKeyValuePairs(String commaDelimitedKeyValuePairs) {
		Map<String, String> parsedMap = new HashMap<>();
		String[] keyValuePairs = commaDelimitedKeyValuePairs.split(",");
		for (String colonDelimitedKeyValuePair : keyValuePairs) {
			String[] keyValue = colonDelimitedKeyValuePair.split(":");
			if (keyValue.length != 2) {
				throw new IllegalArgumentException(format("This pair is broken: '%s'. Separate your key from your value with a ':'.", colonDelimitedKeyValuePair));
			}

			parsedMap.put(keyValue[0], keyValue[1]);
		}
		return parsedMap;
	}
}
