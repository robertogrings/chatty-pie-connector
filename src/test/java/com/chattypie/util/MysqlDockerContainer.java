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

import java.io.IOException;
import java.lang.reflect.Field;

import lombok.extern.slf4j.Slf4j;

import org.springframework.util.ReflectionUtils;

import com.xebialabs.overcast.host.CloudHost;
import com.xebialabs.overcast.host.CloudHostFactory;
import com.xebialabs.overcast.host.DockerHost;
import com.xebialabs.overcast.support.docker.DockerDriver;

/**
 * Allows you to start and wait until you have a running mysql docker container.
 * Of course, this class requires docker (both the cli and the docker engine must be installed locally.)
 */
@Slf4j
public class MysqlDockerContainer {
	private static final int MYSQL_DATABASE_PORT_IN_CONTAINER = 3306;
	private final CloudHost mysqlHost;
	private boolean wasStartedByUs;

	private MysqlDockerContainer() {
		mysqlHost = CloudHostFactory.getCloudHost("mysql");
	}

	public static MysqlDockerContainer newContainer() {
		return new MysqlDockerContainer();
	}

	static MysqlDockerContainer byId(String existingContainerId) throws NoSuchFieldException {
		MysqlDockerContainer existingContainer = newContainer();
		setContainerIdOnHost(existingContainer.mysqlHost, existingContainerId);
		return existingContainer;
	}

	public MysqlDockerContainer startAndWait() {
		mysqlHost.setup();

		String containerId = getContainerId();
		waitUntilContainerIsUp(containerId);
		wasStartedByUs = true;

		log.debug("Started mysql docker container #{} on {}", getShortContainerId(), getHostAndPort());
		return this;
	}

	public void kill() {
		log.debug("Killing mysql docker container #{} on {}", getShortContainerId(), getHostAndPort());
		mysqlHost.teardown();
	}

	String getHostName() {
		return mysqlHost.getHostName();
	}

	int getPort() {
		if (!wasStartedByUs) {
			return -1;
		}
		return mysqlHost.getPort(MYSQL_DATABASE_PORT_IN_CONTAINER);
	}

	public String getHostAndPort() {
		return format("%s:%d", getHostName(), getPort());
	}

	String getContainerId() {
		return ((DockerHost) mysqlHost).getDockerDriver().getContainerId();
	}

	private String getShortContainerId() {
		return getContainerId().substring(0, 12);
	}

	private void waitUntilContainerIsUp(String containerId) {
		try {
			int statusCode = new ProcessBuilder(
					"docker", "run",
					"--link", format("%s", containerId),
					"--rm", "martin/wait"
			).start().waitFor();
			if (statusCode != 0) {
				throw new IllegalArgumentException("docker run exited with non-zero code. :( | " + statusCode);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			throw new ProcessCannotStartException(e);
		}
	}

	private static void setContainerIdOnHost(CloudHost dockerHost, String containerId) throws NoSuchFieldException {
		DockerDriver dockerDriver = ((DockerHost) dockerHost).getDockerDriver();
		Field containerIdField = DockerDriver.class.getDeclaredField("containerId");
		ReflectionUtils.makeAccessible(containerIdField);

		ReflectionUtils.setField(containerIdField, dockerDriver, containerId);
	}

	private class ProcessCannotStartException extends RuntimeException {
		private static final long serialVersionUID = 233523461;

		ProcessCannotStartException(Throwable throwable) {
			super(throwable);
		}
	}
}
