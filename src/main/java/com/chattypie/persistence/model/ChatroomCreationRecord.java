package com.chattypie.persistence.model;

import javax.annotation.Generated;

/**
 * ChatroomCreationRecord is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class ChatroomCreationRecord {

    public ChatroomCreationRecord() {
    }

    public ChatroomCreationRecord(java.time.Instant creationDate, String id) {
        this.creationDate = creationDate;
        this.id = id;
    }

    private java.time.Instant creationDate;

    private String id;

    public java.time.Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(java.time.Instant creationDate) {
        this.creationDate = creationDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
         return "creationDate = " + creationDate + ", id = " + id;
    }

}

