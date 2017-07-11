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

package com.chattypie.service.chattypie.chatroom;

import static java.time.ZonedDateTime.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.chattypie.datasource.DatasourceConfiguration;
import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.chattypie.util.ITTestDataSourceConfiguration;
import com.chattypie.util.TestDatabaseHandle;
import com.querydsl.sql.SQLQueryFactory;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {DatasourceConfiguration.class, ITTestDataSourceConfiguration.class})
public class ChatroomDaoIT {
	@Autowired
	private SQLQueryFactory queryFactory;

	@Autowired
	private TestDatabaseHandle testDb;

	private ChatroomDao testedChatroomDao;

	@Before
	public void setUp() throws Exception {
		testedChatroomDao = new ChatroomDao(queryFactory);
	}

	@After
	public void tearDown() throws Exception {
		testDb.truncateSchema();
	}

	@Test
	public void testStoreChatroom_whenAChatroomIsStored_thenItCanBeReadBack() throws Exception {
		//Given
		String expectedChatroomId = "expectedChatroomId";
		Instant tenSecondsBeforeTest = now().minusSeconds(10).toInstant();

		//When
		testedChatroomDao.storeChatroom(expectedChatroomId);
		List<ChatroomCreationRecord> chatroomsRead = testedChatroomDao.readCreatedSince(
			now().minusDays(1)
		);

		Instant tenSecondsAfterTest = now().plusSeconds(10).toInstant();

		//Then
		assertThat(chatroomsRead)
			.hasSize(1);
		assertThat(chatroomsRead.get(0).getId())
			.isEqualTo(expectedChatroomId);
		assertThat(chatroomsRead.get(0).getCreationDate())
			.isBetween(tenSecondsBeforeTest, tenSecondsAfterTest);
	}

	@Test
	public void testReadCreatedSince_whenChatroomsExistInTheDataStore_thenOnlyTheOnesCreatedAfterTheRequested() {
		//Given
		ZonedDateTime timeOfTest = now();

		String chatroom1Id = "chatroom1Id";
		String chatroom2Id = "chatroom2Id";
		String chatroom3Id = "chatroom3Id";

		ChatroomCreationRecord chatroom1 = new ChatroomCreationRecord(
			timeOfTest.minusDays(3).truncatedTo(SECONDS).toInstant(),
			chatroom1Id
		);
		ChatroomCreationRecord chatroom2 = new ChatroomCreationRecord(
			timeOfTest.minusHours(2).truncatedTo(SECONDS).toInstant(),
			chatroom2Id
		);
		ChatroomCreationRecord chatroom3 = new ChatroomCreationRecord(
			timeOfTest.minusMinutes(15).truncatedTo(SECONDS).toInstant(),
			chatroom3Id
		);

		testDb.givenChatroomExistsInDatabase(chatroom1);
		testDb.givenChatroomExistsInDatabase(chatroom2);
		testDb.givenChatroomExistsInDatabase(chatroom3);

		//When
		List<ChatroomCreationRecord> chatroomsRead = testedChatroomDao.readCreatedSince(
			now().minusDays(1)
		);

		//Then
		assertThat(chatroomsRead).hasSize(2);
		assertThat(chatroomsRead.get(0).getId()).isEqualTo(chatroom3.getId());
		assertThat(chatroomsRead.get(0).getCreationDate()).isEqualTo(chatroom3.getCreationDate());
		assertThat(chatroomsRead.get(1).getId()).isEqualTo(chatroom2.getId());
		assertThat(chatroomsRead.get(1).getCreationDate()).isEqualTo(chatroom2.getCreationDate());
	}
}
