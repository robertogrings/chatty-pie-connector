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

import static com.chattypie.persistence.model.QChatroomCreationRecord.chatroomCreationRecord;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

import com.chattypie.persistence.model.ChatroomCreationRecord;
import com.querydsl.sql.SQLQueryFactory;

@RequiredArgsConstructor
public class ChatroomDao {
	private final SQLQueryFactory queryFactory;

	public void storeChatroom(String chatroomToPersist) {
		queryFactory
			.insert(chatroomCreationRecord)
			.set(chatroomCreationRecord.id, chatroomToPersist)
			.executeWithKey(Chatroom.class);
	}

	public List<ChatroomCreationRecord> readCreatedSince(ZonedDateTime since) {
		return queryFactory
			.selectFrom(chatroomCreationRecord)
			.where(chatroomCreationRecord.creationDate.gt(since.toInstant()))
			.orderBy(chatroomCreationRecord.creationDate.desc())
			.fetch();
	}
}
