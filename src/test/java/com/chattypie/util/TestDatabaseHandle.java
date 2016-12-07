package com.chattypie.util;

import static com.chattypie.persistence.model.QChatroomCreationRecord.chatroomCreationRecord;
import static java.lang.String.format;

import java.sql.SQLException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;

import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.chattypie.service.chattypie.chatroom.Chatroom;
import com.querydsl.sql.SQLQueryFactory;

@RequiredArgsConstructor
public class TestDatabaseHandle {
	private final JdbcTemplate jdbcTemplate;
	private final SQLQueryFactory queryFactory;

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
}
