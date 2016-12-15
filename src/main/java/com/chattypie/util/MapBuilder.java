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
