package com.chattypie.util;

import static com.chattypie.util.MysqlDockerContainer.newContainer;
import static java.lang.String.format;

import java.io.IOException;

import org.springframework.beans.factory.DisposableBean;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * A factory class for creating a {@link MysqlDataSource} instance and its underlying mysql database
 * (running in a Docker container).
 * <p>
 * Be aware that:
 * * The class has a dependency on docker (both the cli and the docker engine must be installed locally)
 * * Failing to call shutdown after the application is finished would result in the database container not
 * being stopped/removed, which is a serious resource leak.
 * <p>
 * NB: It is important to implement {@link DisposableBean} here in order to properly clean up the test db container
 */
public class LocalDockerMysqlDatasourceFactory implements DisposableBean {
	private final MysqlDockerContainer mysqlDockerContainer;

	LocalDockerMysqlDatasourceFactory() throws IOException {
		mysqlDockerContainer = newContainer().startAndWait();
	}

	MysqlDataSource getDatasource() {
		String url = format(
				"jdbc:mysql://%s:%s/chatty_pie_connector?createDatabaseIfNotExist=true&useLegacyDatetimeCode=false",
				mysqlDockerContainer.getHostName(),
				mysqlDockerContainer.getPort()
		);

		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(url);
		mysqlDataSource.setUser("root");
		mysqlDataSource.setPassword("password");
		return mysqlDataSource;
	}

	@Override
	public void destroy() throws Exception {
		//NB! : Very important to kill container here: when we are finished using a container,
		//		we should remove it to avoid resource leaks
		mysqlDockerContainer.kill();
	}
}
