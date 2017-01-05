package com.chattypie.util;

import static com.chattypie.persistence.model.QChatroomCreationRecord.chatroomCreationRecord;
import static java.lang.String.format;

import java.sql.SQLException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.JdbcTemplate;

import com.chattypie.datasource.DatasourceConfiguration;
import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.querydsl.sql.SQLQueryFactory;

@Slf4j
public class TestDatabaseHandle {
	private final JdbcTemplate jdbcTemplate;
	private final SQLQueryFactory queryFactory;
	private final MysqlDataSource dataSource;

	public TestDatabaseHandle(String url) {
		log.info("Connecting to database at {}", url);
		dataSource = createDatasource(url);

		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.queryFactory = new DatasourceConfiguration().nonTransactionalQueryFactory(new JdbcTemplate(dataSource), dataSource);
	}

	public String getDatabaseUrl() {
		return dataSource.getUrl();
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

	private static MysqlDataSource createDatasource(String url) {
		MysqlDataSource mysqlDataSource = new MysqlDataSource();
		mysqlDataSource.setURL(url);
		mysqlDataSource.setUser("root");
		mysqlDataSource.setPassword("password");
		return mysqlDataSource;
	}
}
