package com.chattypie.util;

import static com.chattypie.util.ManageMysqlInDockerContainer.CONTAINER_MYSQL_PORT_FILE_PATH;
import static lombok.AccessLevel.PRIVATE;

import java.nio.file.Files;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ITDatabaseUtils {

	private static final int DEFAULT_MYSQL_PORT = 3306;

	@SneakyThrows
	public static String readTestDatabaseUrl() {

		int itDatabaseMysqlPort;

		if (Files.exists(CONTAINER_MYSQL_PORT_FILE_PATH)) {
			itDatabaseMysqlPort = Integer.parseInt(
				Files.readAllLines(CONTAINER_MYSQL_PORT_FILE_PATH).get(0)
			);
		} else {
			itDatabaseMysqlPort = DEFAULT_MYSQL_PORT;
		}

		String testDatabaseUrl = String.format(
			"jdbc:mysql://localhost:%d/chatty_pie_connector?useLegacyDatetimeCode=false&createDatabaseIfNotExist=true",
			itDatabaseMysqlPort
		);

		log.info("The test database URL used is {}", testDatabaseUrl);

		return testDatabaseUrl;
	}
}
