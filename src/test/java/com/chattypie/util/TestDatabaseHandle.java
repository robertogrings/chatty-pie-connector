package com.chattypie.util;

import static com.chattypie.persistence.model.QChatroomCreationRecord.chatroomCreationRecord;
import static java.lang.String.format;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.chattypie.datasource.DatasourceConfiguration;
import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.querydsl.sql.SQLQueryFactory;

public class TestDatabaseHandle {
	private final JdbcTemplate jdbcTemplate;
	private final SQLQueryFactory queryFactory;
	private String url;

	public TestDatabaseHandle(String url) {
		this.url = url;
		DataSource dataSource = createDatasource(url);

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.queryFactory = new DatasourceConfiguration().nonTransactionalQueryFactory(new JdbcTemplate(dataSource), dataSource);
	}

	public static String testUrl(String hostAndPort) {
		return format("jdbc:mysql://%s/chatty_pie_connector?createDatabaseIfNotExist=true&useLegacyDateTimeCode=false", hostAndPort);
	}

	public void truncateSchema() throws SQLException {
		List<String> truncateStatements = jdbcTemplate.queryForList(format(
				"SELECT concat('TRUNCATE TABLE ', TABLE_NAME, ';')\n" +
						"FROM INFORMATION_SCHEMA.TABLES\n" +
						"WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME NOT LIKE 'schema_version'",
				"chatty_pie_connector"
		), String.class);
		truncateStatements.forEach(jdbcTemplate::execute);
	}

	public void givenChatroomExistsInDatabase(ChatroomCreationRecord chatroom) {
		queryFactory
				.insert(chatroomCreationRecord)
				.populate(chatroom)
				.executeWithKey(Chatroom.class);
	}

	public String getUrl() {
		return url;
	}

	private DataSource createDatasource(String url) {
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(url);
		mysqlDataSource.setUser("root");
		mysqlDataSource.setPassword("password");
		return mysqlDataSource;
	}
}
