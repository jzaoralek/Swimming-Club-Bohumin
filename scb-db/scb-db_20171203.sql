DROP TABLE bank_transaction;

CREATE TABLE bank_transaction (
	protiucet_cisloUctu VARCHAR(240),
	protiucet_kodBanky VARCHAR(240),
	protiucet_nazevBanky VARCHAR(240) CHARACTER SET utf8,
	protiucet_nazevUctu VARCHAR(240) CHARACTER SET utf8,
	datumPohybu DATE,
	objem DOUBLE,
	konstantniSymbol VARCHAR(240),
	variabilniSymbol VARCHAR(240),
	uzivatelskaIdentifikace VARCHAR(240) CHARACTER SET utf8,
	typ VARCHAR(100) CHARACTER SET utf8,
	mena VARCHAR(50),
	idPokynu BIGINT,
	idPohybu BIGINT,
	komentar VARCHAR(240) CHARACTER SET utf8,
	provedl VARCHAR(240) CHARACTER SET utf8,
	zpravaProPrijemnce VARCHAR(240) CHARACTER SET utf8,
	PRIMARY KEY (idPohybu)
);