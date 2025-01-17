DROP TABLE IF EXISTS `CATEGORIES`;

CREATE TABLE `CATEGORIES` (
	`ID`			CHAR(50)  		PRIMARY KEY    	NOT NULL,
	`NAME`			VARCHAR(100)	NOT NULL,
	`SEQ`			INT				NOT NULL	DEFAULT 0,
	`CREATED_AT`	TIMESTAMP		NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`UPDATED_AT`	TIMESTAMP		NOT NULL	DEFAULT CURRENT_TIMESTAMP
);