DROP TABLE IF EXISTS `USER_AUTH`;

CREATE TABLE `USER_AUTH` (
	`ID`	BIGINT  PRIMARY KEY     NOT NULL,
	`USERNAME`	VARCHAR(100)	NOT NULL,
	`AUTH`	VARCHAR(100)	NOT NULL
);