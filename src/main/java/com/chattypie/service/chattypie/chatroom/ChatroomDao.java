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
