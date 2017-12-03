CREATE TABLE bank_transaction (
	protiucet_cisloUctu VARCHAR(240),
	protiucet_kodBanky VARCHAR(240),
	protiucet_nazevBanky VARCHAR(240),
	protiucet_nazevUctu VARCHAR(240),
	datumPohybu DATE,
	objem DOUBLE,
	konstantniSymbol VARCHAR(240),
	variabilniSymbol VARCHAR(240),
	uzivatelskaIdentifikace VARCHAR(240),
	typ VARCHAR(100),
	mena VARCHAR(50),
	idPokynu LONG,
	idPohybu LONG,
	komentar VARCHAR(240),
	provedl VARCHAR(240),
	zpravaProPrijemnce VARCHAR(240)
);