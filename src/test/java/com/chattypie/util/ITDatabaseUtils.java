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

import static com.chattypie.util.ManageMysqlInDockerContainer.CONTAINER_MYSQL_HOST_AND_PORT_FILE_PATH;
import static lombok.AccessLevel.PRIVATE;

import java.nio.file.Files;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ITDatabaseUtils {

	private static final String DEFAULT_LOCAL_MYSQL_HOST_PORT = "localhost:3306";

	@SneakyThrows
	public static String readTestDatabaseUrl() {

		String itDatabaseHostPort;

		if (Files.exists(CONTAINER_MYSQL_HOST_AND_PORT_FILE_PATH)) {
			itDatabaseHostPort = Files.readAllLines(CONTAINER_MYSQL_HOST_AND_PORT_FILE_PATH).get(0);
		} else {
			itDatabaseHostPort = DEFAULT_LOCAL_MYSQL_HOST_PORT;
		}

		String testDatabaseUrl = String.format(
			"jdbc:mysql://%s/chatty_pie_connector?useLegacyDatetimeCode=false&createDatabaseIfNotExist=true",
			itDatabaseHostPort
		);

		log.info("The test database URL used is {}", testDatabaseUrl);

		return testDatabaseUrl;
	}
}
