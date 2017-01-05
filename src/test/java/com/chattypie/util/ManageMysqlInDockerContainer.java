package com.chattypie.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.delete;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManageMysqlInDockerContainer {
	private static final Path CONTAINER_ID_FILE_PATH = Paths.get("target", "mysql-docker-container-id.txt");
	public static final Path CONTAINER_MYSQL_PORT_FILE_PATH = Paths.get("target", "mysql-docker-container-port.txt");

	public static void main(String[] args) throws Exception {
		Optional<String> existingContainerId = getExistingMysqlDockerContainerId();
		if (existingContainerId.isPresent()) {
			log.info("KILLING EXISTING MYSQL IN DOCKER CONTAINER");
			MysqlDockerContainer.byId(existingContainerId.get()).kill();
			delete(CONTAINER_ID_FILE_PATH);
			delete(CONTAINER_MYSQL_PORT_FILE_PATH);
		} else {
			log.info("STARTING MYSQL IN DOCKER CONTAINER");
			MysqlDockerContainer mysqlDockerContainer = MysqlDockerContainer.newContainer().startAndWait();
			persistContainerId(mysqlDockerContainer.getContainerId());
			persistContainerMysqlPort(mysqlDockerContainer.getPort());
		}

		System.exit(0);
	}

	private static Optional<String> getExistingMysqlDockerContainerId() throws IOException {
		if (exists(CONTAINER_ID_FILE_PATH)) {
			return Optional.of(new String(readAllBytes(CONTAINER_ID_FILE_PATH), UTF_8));
		}
		return Optional.empty();
	}

	private static void persistContainerId(String containerId) throws IOException {
		write(CONTAINER_ID_FILE_PATH, containerId.getBytes(UTF_8), CREATE, TRUNCATE_EXISTING);
	}

	private static void persistContainerMysqlPort(int port) throws IOException {
		write(CONTAINER_MYSQL_PORT_FILE_PATH, Integer.toString(port).getBytes(UTF_8), CREATE, TRUNCATE_EXISTING);
	}
}
