	DROP TABLE IF EXISTS `CART_OPTIONS`;

	CREATE TABLE `CART_OPTIONS` (
		`ID`	CHAR(50)	NOT NULL,
		`CARTS_ID`	CHAR(50)	NOT NULL,
		`OPTION_ITEMS_ID`	CHAR(50)	NOT NULL,
		`CREATED_AT`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
		`UPDATED_AT`	TIMESTAMP	NOT NULL	DEFAULT CURRENT_TIMESTAMP
	);