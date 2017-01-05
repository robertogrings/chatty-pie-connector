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
