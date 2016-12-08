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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManageMysqlInDockerContainer {
	private static Path containerIdFilePath = Paths.get("target", "mysql-docker-container-id.txt");

	public static void main(String[] args) throws Exception {
		String existingContainerId = getExistingMysqlDockerContainerId();
		if (existingContainerId.isEmpty()) {
			log.info("STARTING MYSQL IN DOCKER CONTAINER");
			MysqlDockerContainer mysqlDockerContainer = MysqlDockerContainer.newContainer().startAndWait();
			persistContainerId(mysqlDockerContainer.getContainerId());
		} else {
			log.info("KILLING EXISTING MYSQL IN DOCKER CONTAINER");
			MysqlDockerContainer.byId(existingContainerId).kill();
			delete(containerIdFilePath);
		}
		System.exit(0);
	}

	private static String getExistingMysqlDockerContainerId() throws IOException {
		if (exists(containerIdFilePath)) {
			return new String(readAllBytes(containerIdFilePath), UTF_8);
		}
		return "";
	}

	private static void persistContainerId(String containerId) throws IOException {
		write(containerIdFilePath, containerId.getBytes(UTF_8), CREATE, TRUNCATE_EXISTING);
	}
}
