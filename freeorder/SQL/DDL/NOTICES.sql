
DROP TABLE IF EXISTS `NOTICES`;

CREATE TABLE `NOTICES` (
	`ID`	BIGINT  PRIMARY KEY   AUTO_INCREMENT 	NOT NULL,
	`thumbnail`	VARCHAR(255)	NULL	COMMENT '이벤트 섬네일파일경로',
	`TYPE`	VARCHAR(30)	NOT NULL	COMMENT '타입(공지사항, 이벤트)',
	`TITLE`	VARCHAR(100)	NOT NULL	COMMENT '공지사항 제목',
	`CONTENT`	VARCHAR(1000)	NOT NULL	COMMENT '공지사항 내용',
	`WRITER`	VARCHAR(100)	NOT NULL	COMMENT '작성자',
	`CREATED_AT`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '등록일자',
	`UPDATED_AT`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '수정일자'
);
