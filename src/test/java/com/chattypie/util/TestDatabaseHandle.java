package com.chattypie.util;

import static java.lang.String.format;

import java.sql.SQLException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;

@RequiredArgsConstructor
public class TestDatabaseHandle {
	private final JdbcTemplate jdbcTemplate;

	public void truncateSchema(String schemaName) throws SQLException {

		List<String> truncateStatements = jdbcTemplate.queryForList(format(
			"SELECT concat('TRUNCATE TABLE ', TABLE_NAME, ';')\n" +
			"FROM INFORMATION_SCHEMA.TABLES\n" +
			"WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME NOT LIKE 'schema_version'",
			schemaName
		), String.class);
		truncateStatements.forEach(jdbcTemplate::execute);
	}
}
