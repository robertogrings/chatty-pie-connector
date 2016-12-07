CREATE TABLE IF NOT EXISTS chatroom_creation_record (
  id                 VARCHAR(255) NOT NULL,
  creation_date      DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
