-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2+deb7u8
-- http://www.phpmyadmin.net
--
-- Počítač: localhost
-- Vygenerováno: Pát 03. srp 2018, 19:25
-- Verze MySQL: 5.6.39
-- Verze PHP: 5.3.29-1~dotdeb.0

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Databáze: `kosatky_test`
--

-- --------------------------------------------------------

--
-- Struktura tabulky `bank_transaction`
--

CREATE TABLE IF NOT EXISTS `bank_transaction` (
  `protiucet_cisloUctu` varchar(240) COLLATE utf8_czech_ci DEFAULT NULL,
  `protiucet_kodBanky` varchar(240) COLLATE utf8_czech_ci DEFAULT NULL,
  `protiucet_nazevBanky` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `protiucet_nazevUctu` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `datumPohybu` date DEFAULT NULL,
  `objem` double DEFAULT NULL,
  `konstantniSymbol` varchar(240) COLLATE utf8_czech_ci DEFAULT NULL,
  `variabilniSymbol` varchar(240) COLLATE utf8_czech_ci DEFAULT NULL,
  `uzivatelskaIdentifikace` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `typ` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `mena` varchar(50) COLLATE utf8_czech_ci DEFAULT NULL,
  `idPokynu` bigint(20) DEFAULT NULL,
  `idPohybu` bigint(20) NOT NULL DEFAULT '0',
  `komentar` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `provedl` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `zpravaProPrijemnce` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`idPohybu`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- --------------------------------------------------------

--
-- Struktura tabulky `codelist_item`
--

CREATE TABLE IF NOT EXISTS `codelist_item` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `item_type` enum('SWIMMING_STYLE') COLLATE utf8_czech_ci NOT NULL,
  `name` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `description` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `codelist_item`
--

INSERT INTO `codelist_item` (`uuid`, `item_type`, `name`, `description`, `modif_at`, `modif_by`) VALUES
('82bb2300-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Prsa', '', '2018-06-02 13:34:28', 'SYSTEM'),
('82bb2648-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Volný způsob', '', '2018-07-20 11:20:31', 'pkadmin'),
('82bb27ec-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Motýlek', '', '2018-06-02 13:34:28', 'SYSTEM'),
('82bb29f4-8234-11e6-ae22-56b6b6499611', 'SWIMMING_STYLE', 'Znak', '', '2018-06-02 13:34:28', 'SYSTEM');

-- --------------------------------------------------------

--
-- Struktura tabulky `configuration`
--

CREATE TABLE IF NOT EXISTS `configuration` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `name` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  `description` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `val` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  `type` enum('STRING','INTEGER','BOOLEAN','ENUM') COLLATE utf8_czech_ci NOT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `configuration`
--

INSERT INTO `configuration` (`uuid`, `name`, `description`, `val`, `type`, `modif_at`, `modif_by`) VALUES
('fd33a4d4-7e99-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_ALLOWED', 'Přihlášky do kurzu povoleny', 'true', 'BOOLEAN', '2018-06-02 13:34:28', 'SYSTEM'),
('61c867ae-7e9a-11e6-ae22-56b6b6499611', 'COURSE_APPLICATION_YEAR', 'Aktuální ročník', '2017/2018', 'ENUM', '2018-07-07 20:00:15', 'SYSTEM'),
('61c867ae-7e9a-11e6-ae22-56b6b6499612', 'ORGANIZATION_NAME', 'Název klubu', 'Klub plaveckých sportů Ostrava', 'STRING', '2018-07-07 20:00:15', 'SYSTEM'),
('61c867ae-7e9a-11e6-ae22-56b6b6499613', 'ORGANIZATION_PHONE', 'Telefonní kontakt na klub', '+420 736 210 698', 'STRING', '2018-07-07 20:00:15', 'SYSTEM'),
('61c867ae-7e9a-11e6-ae22-56b6b6499614', 'ORGANIZATION_EMAIl', 'Emailový kontakt na klub', 'petka.formankova@seznam.cz', 'STRING', '2018-07-07 20:00:15', 'SYSTEM'),
('61c867ae-7e9a-11e6-ae22-56b6b6499615', 'WELCOME_INFO', 'Uvítací informace na homepage', 'Vítejte na stránkách Plaveckých kurzů Ostrava', 'STRING', '2018-07-07 20:00:15', 'SYSTEM'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499616', 'COURSE_APPL_SEL_REQ', 'Výběr kurzu v rámci přihlášky', 'true', 'BOOLEAN', '2018-07-17 17:09:51', 'SYSTEM'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499617', 'BASE_URL', 'Základní url aplikace, NEMĚNIT!', 'https://www.pkbohumin.cz/demo', 'STRING', '2018-07-29 10:00:44', 'SYSTEM'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499618', 'HEALTH_AGREEMENT', 'Text souhlasem se zdravotní způsobilostí, zobrazen na přihlášce.', 'Souhlasím s kolektivním plaveckým výcvikem svého syna/dcery. Prohlašuji na základě lékařského posouzení zdravotního stavu, že můj syn/dcera je způsobilý/způsobilá absolvovat fyzickou zátěž sportovních tréninků a plaveckých závodů bez nebezpečí poškození jeho/jejího zdravotního stavu. V případě změny zdravotního stavu budu neprodleně informovat zástupce Plaveckého klubu.', 'STRING', '2018-07-29 10:00:44', 'SYSTEM'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499619', 'PERSONAL_DATA_PROCESS_AGREEMENT', 'Text souhlasem se zpracováním osobních údajů, zobrazen na přihlášce.', 'Souhlasím se zpracováním osobních údajů podle zákona č. 101/2000 Sb.Souhlasím s možností fotografování svého syna/dcery a s možností zveřejnění fotografií nebo videa v rámci propagace Plaveckého klubu Bohumín. Potvrzuji, že jsem se seznámil s Provozním řádem aquacentra .', 'STRING', '2018-07-29 10:00:44', 'SYSTEM');

-- --------------------------------------------------------

--
-- Struktura tabulky `contact`
--

CREATE TABLE IF NOT EXISTS `contact` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `firstname` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `surname` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `street` varchar(240) COLLATE utf8_czech_ci DEFAULT NULL,
  `land_registry_number` int(11) DEFAULT NULL,
  `house_number` smallint(6) DEFAULT NULL,
  `city` varchar(240) COLLATE utf8_czech_ci DEFAULT NULL,
  `zip_code` varchar(32) COLLATE utf8_czech_ci DEFAULT NULL,
  `email1` varchar(100) COLLATE utf8_czech_ci DEFAULT NULL,
  `email2` varchar(100) COLLATE utf8_czech_ci DEFAULT NULL,
  `phone1` varchar(14) COLLATE utf8_czech_ci DEFAULT NULL,
  `phone2` varchar(14) COLLATE utf8_czech_ci DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `contact`
--

INSERT INTO `contact` (`uuid`, `firstname`, `surname`, `street`, `land_registry_number`, `house_number`, `city`, `zip_code`, `email1`, `email2`, `phone1`, `phone2`, `modif_at`, `modif_by`) VALUES
('62225052-4dd2-4150-91c3-8ebf26fd1572', 'Tonda', 'Blaník', NULL, NULL, NULL, 'Karviná', NULL, 'kosatky@kosatkykarvina.cz', NULL, '+420001001002', NULL, '2018-06-02 13:34:28', 'SYSTEM'),
('8ee15fbc-cff4-4b23-b3b9-78c78d09af03', 'Pavel', 'Adamus', 'Provaznická', 25, NULL, 'Ostrava', '70300', NULL, NULL, NULL, NULL, '2018-06-25 17:40:49', 'anonymousUser'),
('a8ea29da-2929-4c49-8157-93200161d6d6', 'Petr', 'Adamus', NULL, NULL, NULL, NULL, NULL, 'petr.adamus@nomail.cz', NULL, '602001002', NULL, '2018-06-25 17:40:49', 'anonymousUser'),
('e700debe-9ad9-4ba1-8e29-2137a28fb5d1', 'Tereza', 'Bárová', 'Klegova', 20, NULL, 'Ostrava', '70500', NULL, NULL, NULL, NULL, '2018-06-25 17:43:36', 'anonymousUser'),
('33e6dfd3-64d7-41d8-8d12-e81a7583252e', 'Pavel', 'Bár', NULL, NULL, NULL, NULL, NULL, 'pavel.bar@nomail.com', NULL, '602002003', NULL, '2018-06-25 17:43:36', 'anonymousUser'),
('26e3174b-134b-4239-8756-161ab3d970bc', 'Markéta', 'Čížková', 'Sokolská', 1112, NULL, 'Ostrava', '70100', NULL, NULL, NULL, NULL, '2018-06-25 17:46:19', 'anonymousUser'),
('4034c63a-d025-4432-97ae-fab50258a4aa', 'Ondřej', 'Čížek', NULL, NULL, NULL, NULL, NULL, 'ondrej.cizek@nomail.cz', NULL, '602003004', NULL, '2018-06-25 17:46:19', 'anonymousUser'),
('49e08947-53e4-4113-9f84-991f67e478e9', 'Lukáš', 'Drozd', 'Ukrajinská', 5071, NULL, 'Ostrava', '70800', NULL, NULL, NULL, NULL, '2018-06-25 17:48:25', 'anonymousUser'),
('7cea7cfc-4d76-415e-ac3b-936023d7b35d', 'Martin', 'Drozd', NULL, NULL, NULL, NULL, NULL, 'martin.drozd@nomail.cz', NULL, '604005006', NULL, '2018-06-25 17:48:26', 'anonymousUser'),
('02d03b5c-c2aa-4c38-b15b-f2498b94d4b5', 'Martina', 'Ehlová', 'Ludvíka POdéště', 28, NULL, 'Ostrava', '70800', NULL, NULL, NULL, NULL, '2018-06-25 17:50:13', 'anonymousUser'),
('0030739b-5715-493d-b37f-1b5502ddc5e4', 'Tomáš', 'Ehl', NULL, NULL, NULL, NULL, NULL, 'tomas.ehl@nomail.com', NULL, '606001002', NULL, '2018-06-25 17:50:13', 'anonymousUser'),
('80985d99-c056-48e2-a68b-06a7fcbdb8a2', 'Eliška', 'Fialová', 'Bobrovníky', 25, NULL, 'Ostrava', '70900', NULL, NULL, NULL, NULL, '2018-06-25 17:55:55', 'anonymousUser'),
('b6a1def8-51d7-4216-b8f7-e2d6c8092ade', 'Petr', 'Fiala', NULL, NULL, NULL, NULL, NULL, 'petr.fiala@nomail.cz', NULL, '607001002', NULL, '2018-06-25 17:55:55', 'anonymousUser'),
('f2709982-aeff-4303-9d28-201742493e2e', 'Demeter', 'Gyňa', 'Moravská', 2789, NULL, 'Bohumín', '72900', NULL, NULL, NULL, NULL, '2018-06-25 17:58:20', 'anonymousUser'),
('d3a358b3-6d83-455d-ad0e-6ece252d229e', 'David', 'Gyňa', NULL, NULL, NULL, NULL, NULL, 'david.gyna@nomail.cz', NULL, '721001002', NULL, '2018-06-25 17:58:20', 'anonymousUser'),
('d9d74a56-aa5b-40d2-803b-1e6b3d3b73ed', 'Martin', 'Hron', 'Karvinská', 2530, NULL, 'Bohumín', '72801', NULL, NULL, NULL, NULL, '2018-06-25 17:59:57', 'anonymousUser'),
('1326620c-de30-4336-b346-a0b6499c42c8', 'Tereza', 'Hronová', NULL, NULL, NULL, NULL, NULL, 'tereza.honova@nomail.cz', NULL, '728001002', NULL, '2018-06-25 17:59:57', 'anonymousUser'),
('6e87cf89-9ca8-471f-a0cc-1597d1787117', 'Luděk', 'Jan', NULL, 25, NULL, 'Pstruží', '72500', NULL, NULL, NULL, NULL, '2018-06-25 18:02:11', 'anonymousUser'),
('64560c69-2f5b-4cf3-b4fe-9a2d7f3783c6', 'Miluše', 'Janová', NULL, NULL, NULL, NULL, NULL, 'miluse.janova@nomail.com', NULL, '602022003', NULL, '2018-06-25 18:02:11', 'anonymousUser'),
('b2b5b497-f776-4e25-9d2d-ca913d92599f', 'Matheo', 'Kocourek', 'Kasárenská', 2856, NULL, 'Valašské Meziříčí', '70501', NULL, NULL, NULL, NULL, '2018-06-25 18:06:23', 'anonymousUser'),
('50f30982-c291-4243-a10b-240879e553ef', 'Lucie', 'Smékalová', NULL, NULL, NULL, NULL, NULL, 'lucie.smekalova@nomail.cz', NULL, '725001002', NULL, '2018-06-25 18:06:23', 'anonymousUser'),
('a5d23323-4eeb-4b54-bd36-8686e0833d45', 'Miluše', 'Macourková', 'Sokolovská', 1329, 79, 'Ostrava', '70800', NULL, NULL, NULL, NULL, '2018-06-25 18:12:20', 'anonymousUser'),
('69dbfe88-98cc-4fef-9d7a-102c9e1cb4bf', 'Lukáš', 'Macourek', NULL, NULL, NULL, NULL, NULL, 'lukas.macourek@nomail.com', NULL, '602001002', NULL, '2018-06-25 18:12:20', 'anonymousUser'),
('d91edb1b-13bb-4f94-bd93-f5874591b246', 'Marika', 'Nováčková', 'Poděbradova', 1527, NULL, 'Ostrava', '70100', NULL, NULL, NULL, NULL, '2018-06-25 18:14:10', 'anonymousUser'),
('7c34badc-0961-4eeb-b7b4-e2021b010e5b', 'David', 'Nováček', NULL, NULL, NULL, NULL, NULL, 'david.novacek@nomail.com', NULL, '728009002', NULL, '2018-06-25 18:14:10', 'anonymousUser'),
('add3eec0-6022-4547-b003-8ebda55d77fa', 'Marek', 'Ondráček', 'Pobialova', 15, NULL, 'Ostrava', '70100', NULL, NULL, NULL, NULL, '2018-06-25 18:16:45', 'anonymousUser'),
('d903b079-e664-4b29-8dfe-20b26964df03', 'Jaroslav', 'Ondráček', NULL, 0, 0, NULL, NULL, 'jaroslav.ondracek@nomail.cz', NULL, '601002003', NULL, '2018-06-25 19:19:25', 'jaroslav.ondracek@nomail.cz'),
('879a4671-11e9-48ec-801d-07effd5d953c', 'Martina', 'Paykalová', NULL, 27, NULL, 'Kozlovice', '70301', NULL, NULL, NULL, NULL, '2018-06-25 18:19:41', 'anonymousUser'),
('1d9e89a7-75f9-4704-960c-94868489c455', 'Vratislav', 'Pykal', NULL, NULL, NULL, NULL, NULL, 'vratislav.pykal@nomail.cz', NULL, '7011002003', NULL, '2018-06-25 18:19:41', 'anonymousUser'),
('9b6e998d-d6f1-4b76-baae-37050a3e4b21', 'Jan', 'Rampula', 'Dr. Martínka', 1527, NULL, 'Ostrava', '70500', NULL, NULL, NULL, NULL, '2018-06-25 18:21:25', 'anonymousUser'),
('798ab432-4d13-4a5e-8d61-3c782aa44fdc', 'Luděk', 'Rampula', NULL, NULL, NULL, NULL, NULL, 'ludek.rampula@nomail.cz', NULL, '605001002', NULL, '2018-06-25 18:21:25', 'anonymousUser'),
('5d6a6d0c-dae6-4c09-b06d-109d32cff373', 'Jan', 'Stach', 'Chorkovského', 27, NULL, 'Havířov', '782010', NULL, NULL, NULL, NULL, '2018-06-25 18:22:45', 'anonymousUser'),
('162abf4b-b6aa-47c9-b8cb-27d4d501d658', 'Martina', 'Stachová', NULL, NULL, NULL, NULL, NULL, 'martina.stachova@nomail.cz', NULL, '729001002', NULL, '2018-06-25 18:22:45', 'anonymousUser'),
('5217f32d-63d4-46c4-8fc6-5a4443b1c29e', 'Bartoloměj', 'Tokař', 'Na hůrce', 27, NULL, 'Bruntál', '72500', NULL, NULL, NULL, NULL, '2018-06-25 18:24:46', 'anonymousUser'),
('adc485f6-39b6-4e4e-a054-c5d6e12aa5cb', 'Radka', 'Tokařová', NULL, NULL, NULL, NULL, NULL, 'radka.tokanova@nomail.cz', NULL, '604001002', NULL, '2018-06-25 18:24:46', 'anonymousUser'),
('fda4d2df-3386-499c-9d2d-3d69d6493bb8', 'Miroslava', 'Uhlířová', 'Kolárova', 27, NULL, 'Ostrava', '72501', NULL, NULL, NULL, NULL, '2018-06-25 18:26:50', 'anonymousUser'),
('3e8fe8fc-fc06-4575-8d66-8637e9639222', 'Petr', 'Uhlíř', NULL, NULL, NULL, NULL, NULL, 'petr.uhlir@nomail.cz', NULL, '606001002', NULL, '2018-06-25 18:26:50', 'anonymousUser'),
('82f7e963-3c66-43ff-b910-e5c5001b4964', 'Petr', 'Admin', NULL, NULL, NULL, NULL, NULL, 'jakub.zaoralek@gmail.com', NULL, '602001002', NULL, '2018-06-25 18:30:32', 'kosatky'),
('bd4cf3f4-f81e-47f3-8dba-ffd585743b62', 'Adam', 'Trenér', NULL, NULL, NULL, NULL, NULL, 'jakub.zaoralek@gmail.com', NULL, '602001002', NULL, '2018-06-25 18:32:03', 'kosatky'),
('c736384c-637a-4d3e-8d16-fa5bef237bfa', 'Markéta', 'Ondráčková', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2018-06-25 18:37:28', 'jaroslav.ondracek@nomail.cz'),
('64dfca15-16b5-442e-aa7a-58326769820c', 'Jan', 'Ondráček', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2018-06-25 19:19:25', 'jaroslav.ondracek@nomail.cz'),
('922ca06a-e419-438a-aa8e-3a7d7696184f', 'Jakub', 'Zaorálek', NULL, 1, NULL, 'Ostrava', '70300', NULL, NULL, NULL, NULL, '2018-07-17 15:22:27', 'anonymousUser'),
('88c14bfd-ee37-4479-ba1e-2b13d75e0843', 'Jakub', 'Zaorálek', NULL, NULL, NULL, NULL, NULL, 'jakub.zaoralek@gmail.com', NULL, '602001002', NULL, '2018-07-17 15:22:27', 'anonymousUser'),
('1b12a14f-7c24-4ea3-b4da-5c93dfa70925', 'Jakub', 'Zaorálek', NULL, 1, NULL, 'Ostrava', '70800', NULL, NULL, NULL, NULL, '2018-07-17 15:58:14', 'anonymousUser'),
('3d265bfc-3bdd-401b-b55c-b1b832fe7a37', 'Jakub', 'Zaorálek', NULL, NULL, NULL, NULL, NULL, 'jakub.zaoralek@seznam.cz', NULL, '602001002', NULL, '2018-07-17 15:58:15', 'anonymousUser');

-- --------------------------------------------------------

--
-- Struktura tabulky `course`
--

CREATE TABLE IF NOT EXISTS `course` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `name` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `description` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  `year_from` year(4) DEFAULT NULL,
  `year_to` year(4) DEFAULT NULL,
  `price_semester_1` int(11) NOT NULL,
  `price_semester_2` int(11) NOT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  `course_location_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `max_participant_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `course`
--

INSERT INTO `course` (`uuid`, `name`, `description`, `year_from`, `year_to`, `price_semester_1`, `price_semester_2`, `modif_at`, `modif_by`, `course_location_uuid`, `max_participant_count`) VALUES
('7542391e-7d8c-4818-99c0-cb1c6cb62483', 'Chci závodit', 'Popis skupiny: Do závodního plavání jsou zařazovány děti, které mají zájem o závodní a tréninkovou činnost. Snažíme se z dětí vychovávat nejen fyzicky zdatné a výkonné sportovce, ale také kvalitní osobnosti, které využívají vlastnosti získané při pravidelném trénování tj. disciplína, sebekázeň, sebekritika, kolektivní myšlení, svědomitost, vytrvalost, rozvážnost, zásadovost, aj. v samostatném vstupu do dospělého kvalitního života a zaměstnání\n- u dětí navštěvujících tuto skupinu předpokládáme zájem o účast na závodech \n\nCo by dítě mělo zvládnout: uplavat bez problémů  100m základními způsoby – znak, prsa, kraul\n\nDoporučený věk: 10-15 let\n\nNáplň: zdokonalování všech plaveckých způsobů včetně motýla, charakter kondičního tréninku \n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou.', 2017, 2018, 3500, 3500, '2018-07-29 15:35:09', 'pkadmin', 'fd1a8052-b288-4f2f-b1c6-4fc1a8eca3b8', 15),
('475c81e6-cf75-4fdc-b888-d572a1a0a7f6', 'Chci se zdokonalit- Ještěrka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:31:51', 'pkadmin', '2043c750-01f7-4205-94bd-4717b01f1431', 12),
('2820eca0-e63c-47af-b265-e89c16d3b582', 'Chci se zdokonalit- Poruba', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 3000, 3000, '2018-07-29 15:31:29', 'pkadmin', 'fd1a8052-b288-4f2f-b1c6-4fc1a8eca3b8', 15),
('2369ab2a-c6ba-4d04-a73d-bbecc9dc5bba', 'Chci se zdokonalit- Ještěrka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:32:07', 'pkadmin', '2043c750-01f7-4205-94bd-4717b01f1431', 12),
('108e9c7c-f6ba-41f0-ae8b-65e63f144a5b', 'Chci se zdokonalit- Hrabůvka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:32:24', 'pkadmin', 'aec9a3f4-b8bb-460d-87f8-6368655ea510', 16),
('335deeb7-9259-4267-a361-0345c9be5336', 'Chci se zdokonalit- Hrabůvka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:32:56', 'pkadmin', 'aec9a3f4-b8bb-460d-87f8-6368655ea510', 12),
('7dd2efc4-d674-4945-9609-963f4a7bd7b6', 'Chci se zdokonalit- Hrabůvka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:33:13', 'pkadmin', 'aec9a3f4-b8bb-460d-87f8-6368655ea510', 16),
('01781a89-8c87-4c93-a4e2-7205c50e4d07', 'Chci se zdokonalit- Hrabůvka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:33:25', 'pkadmin', 'aec9a3f4-b8bb-460d-87f8-6368655ea510', 16),
('ccd27aab-ccf6-4f4c-8321-5c4ee6238752', 'Chci se zdokonalit- Hrabůvka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:33:36', 'pkadmin', 'aec9a3f4-b8bb-460d-87f8-6368655ea510', 16),
('36cf953f-b9c6-4e39-96fc-9e73360ed322', 'Chci se zdokonalit- Hrabůvka', 'Popis skupiny: Některé děti nechtějí být závodním plavcem, ale přesto se chtějí dále v plavání zdokonalovat nebo mají plavání jako doplňkový sport. Právě pro ně máme zdokonalovací plavání.  Zde se věnujeme rozvíjení pohybových dovedností a zdokonalování všech plaveckých způsobů. Pravidelným tréninkem zlepšujeme u dětí kondici, vytrvalost, podporujeme zdravý způsob života, ke kterému neodmyslitelně pohyb patří.\n-tuto skupinu navštěvují děti, které plavání baví, jako aktivní využití volného času, neúčastní se závodů, po domluvě s trenérem tuto účast umožňujeme\n\nCo by dítě mělo zvládnout: základy techniky plaveckých způsobů znak, prsa a kraul, uplavat bez pomoci 50m\n\nDoporučený věk: 13 - 20 let\n\nNáplň: zdokonalovaní plaveckých způsobů znak, prsa, kraul, základy motýla, plavání s pomůckami, hry\n\nPřípadné dotazy ohledně zařazení konzultujte s vedoucí plaveckých kurzů sl. Formánkovou', 2017, 2018, 2800, 2800, '2018-07-29 15:33:50', 'pkadmin', 'aec9a3f4-b8bb-460d-87f8-6368655ea510', 16);

-- --------------------------------------------------------

--
-- Struktura tabulky `course_application`
--

CREATE TABLE IF NOT EXISTS `course_application` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `year_from` year(4) NOT NULL,
  `year_to` year(4) NOT NULL,
  `course_participant_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `user_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `payed` enum('0','1') COLLATE utf8_czech_ci NOT NULL DEFAULT '0',
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `course_application`
--

INSERT INTO `course_application` (`uuid`, `year_from`, `year_to`, `course_participant_uuid`, `user_uuid`, `payed`, `modif_at`, `modif_by`) VALUES
('d3349f5a-5a05-4155-8b2b-d44519f077cb', 2017, 2018, '048d14b5-08c8-4909-9a5e-22dac851baa6', '06720764-bbc8-4326-ae0d-c098e3a04926', '0', '2018-06-25 17:40:50', 'anonymousUser'),
('01a7b142-7a46-4639-888d-7096e4cd636b', 2017, 2018, '61e38941-9430-4f8a-80e0-311a3ac3b439', 'e219e225-bdd3-4ed8-ae61-21096b34d3bd', '0', '2018-06-25 17:43:36', 'anonymousUser'),
('552220bf-a41b-4f67-bf63-7bfc0dd5c581', 2017, 2018, '04d555f3-96f1-4716-b8fa-d1d379f87819', '6452301a-46ad-4f1d-894c-125e19866dde', '0', '2018-06-25 17:46:19', 'anonymousUser'),
('e8b1caa6-8b5e-4ac2-a672-970611fa12ae', 2017, 2018, '19855ce9-308c-4822-968c-3016d48da869', '2bf2e6e4-2893-4720-81b3-5939cfb5ba5c', '0', '2018-06-25 17:48:26', 'anonymousUser'),
('653edb9d-297c-42d4-a9ab-e4ee154f5135', 2017, 2018, '41804426-5fe1-44b9-b724-93496cb97400', '49f498d1-d557-4b0c-8690-76dcec7f3c33', '0', '2018-06-25 17:50:13', 'anonymousUser'),
('92c9d1a0-4797-44f4-b4a5-a9ab050dac08', 2017, 2018, '01e47996-7488-49df-a79b-17d186fad7ce', '7e3aa524-13f1-4396-a798-4ba070e37909', '0', '2018-06-25 17:55:55', 'anonymousUser'),
('cfbb35bb-f1c7-4f7c-812c-3ca7dcb3310b', 2017, 2018, '352a131e-8d53-4a5f-ae70-897a12b65fc8', '7b02f7f5-0175-4578-8a73-f243fb5bdadb', '0', '2018-06-25 17:58:20', 'anonymousUser'),
('e0d4c680-75a8-4c34-9bd4-6ca76d69af27', 2017, 2018, '388891ea-50d4-4863-8c29-b9d0475555c6', '1158b89b-a79b-4e4e-8bd4-b79264ddedab', '0', '2018-06-25 17:59:57', 'anonymousUser'),
('a9139ec0-7639-4e73-9a63-0845d53edf5d', 2017, 2018, 'a8cef7f9-d45f-493f-97fe-4e81c64498cc', '57b669fe-6c56-4ec1-b665-523a769b2e4f', '0', '2018-06-25 18:02:11', 'anonymousUser'),
('8077aff7-176c-4154-9e61-01f6d7114ee3', 2017, 2018, '53887cee-292d-49e9-8541-98f63a03acce', 'af05d616-82c6-4602-9db2-26b0561c4bbf', '0', '2018-06-25 18:06:23', 'anonymousUser'),
('0fc5fd49-3a4f-44de-aa1d-5d6f7932c94d', 2017, 2018, 'c9e168fc-5e84-4c12-bb13-c7e48a6d7627', '3c9ff9d4-9651-499f-98af-39fb1b219725', '0', '2018-06-25 18:12:20', 'anonymousUser'),
('b67f13dc-be69-4466-972f-61fe89ecf587', 2017, 2018, 'db6ea44a-7bee-4c57-9951-389c39163903', '43f2ac4a-3049-423a-8876-46ed39eef7d7', '0', '2018-06-25 18:14:10', 'anonymousUser'),
('3a88926e-1a55-48a2-9784-642956012a6b', 2017, 2018, '996b0547-6d63-4de4-ae67-ae620ef8d5ee', 'ec8132b7-1ca1-47a6-88ce-4ba1129b6fc3', '0', '2018-06-25 18:16:46', 'anonymousUser'),
('cd4a48d8-8d2f-417a-9d58-25ed2881843b', 2017, 2018, 'cbb75dc2-181b-442a-ae0c-4a03f289fe49', '13f2f2b7-692f-4a7b-967b-362e36d28edb', '0', '2018-06-25 18:19:41', 'anonymousUser'),
('ddb6f103-903b-46f3-afa9-31ce80c6c22d', 2017, 2018, '7143d7db-f514-4623-8725-cca5f4b91555', '45edf303-08d2-42db-aeea-a802a8953466', '0', '2018-06-25 18:21:25', 'anonymousUser'),
('e78747cf-0d3a-44e5-80d1-3e59cfa0a7d8', 2017, 2018, '1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '859a498a-bf02-4c5d-844a-c44b78c59bb6', '0', '2018-06-25 18:22:45', 'anonymousUser'),
('3ad7f0a7-58b5-456f-89f7-c65133832fa4', 2017, 2018, '1cce3954-c33d-45ce-a6dc-c4067f01fc15', '1073b7cc-0dbb-44de-ad14-e4a828a4a11e', '0', '2018-06-25 18:24:46', 'anonymousUser'),
('2ebccb96-ef23-4b2a-814b-58359496513c', 2017, 2018, '18d89b70-8326-46bc-a46c-767d3dd3dfa5', 'd828cb35-c174-4548-868a-aa880bd92303', '0', '2018-06-25 18:26:50', 'anonymousUser'),
('bafb628c-4c67-4632-b893-b0284c3f0b14', 2017, 2018, '261dc4e2-f751-43cb-b19f-b09cf6aa9178', 'ec8132b7-1ca1-47a6-88ce-4ba1129b6fc3', '0', '2018-06-25 18:37:29', 'jaroslav.ondracek@nomail.cz'),
('086c66e8-4d24-4e76-a173-187b77b3c284', 2017, 2018, '53696a6b-97b9-48d1-872c-b1330e81da60', 'ec8132b7-1ca1-47a6-88ce-4ba1129b6fc3', '0', '2018-06-25 19:19:25', 'jaroslav.ondracek@nomail.cz'),
('a15c5c50-3f77-423e-8b1c-4d9084cd7b61', 2017, 2018, 'a7e8833f-d0ff-451e-8fea-2e10a6688852', '8edb5311-8a58-45a3-afa6-de5e22275880', '0', '2018-07-17 15:22:27', 'anonymousUser'),
('a0445e2e-fd78-4f8b-a930-0a730355239c', 2017, 2018, '78c61465-c76e-4d2c-b126-2f35bd8dae2d', '71f3e5f1-acea-4130-93f7-e8863579df1f', '0', '2018-07-17 15:58:15', 'anonymousUser');

-- --------------------------------------------------------

--
-- Struktura tabulky `course_course_participant`
--

CREATE TABLE IF NOT EXISTS `course_course_participant` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `course_participant_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `course_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `course_course_participant`
--

INSERT INTO `course_course_participant` (`uuid`, `course_participant_uuid`, `course_uuid`) VALUES
('03362122-66ae-42ba-8602-bc48c8513179', '78c61465-c76e-4d2c-b126-2f35bd8dae2d', '7542391e-7d8c-4818-99c0-cb1c6cb62483'),
('c39652e0-89da-40bf-97a0-1438852bea51', '1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '7542391e-7d8c-4818-99c0-cb1c6cb62483'),
('bcebead8-2522-4d32-a296-8e665f531433', '1cce3954-c33d-45ce-a6dc-c4067f01fc15', '7542391e-7d8c-4818-99c0-cb1c6cb62483'),
('0bd658c3-db7e-43f7-880c-b887bbc42c05', '18d89b70-8326-46bc-a46c-767d3dd3dfa5', '7542391e-7d8c-4818-99c0-cb1c6cb62483'),
('2e24f89d-8b5e-4989-8ec7-e64410bf20b8', 'cbb75dc2-181b-442a-ae0c-4a03f289fe49', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('e3d59509-71c9-4cb7-9681-b6f11f90fbb6', '53696a6b-97b9-48d1-872c-b1330e81da60', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('7d949b0c-2bb5-4a86-a7cc-8afa099bc7c6', '996b0547-6d63-4de4-ae67-ae620ef8d5ee', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('e6a1cedc-e70f-4699-ac3c-87174d94b0bb', 'db6ea44a-7bee-4c57-9951-389c39163903', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('7dc68d91-0d7d-438d-b32d-f9064d72ea3d', 'c9e168fc-5e84-4c12-bb13-c7e48a6d7627', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('82147ceb-d3e6-4774-a190-9b8d4cdd3f4d', '53887cee-292d-49e9-8541-98f63a03acce', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('ac86ea7d-d96a-4d52-8609-aeae7b257b88', 'a8cef7f9-d45f-493f-97fe-4e81c64498cc', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('6c479067-c629-414f-b50d-afa221367946', '388891ea-50d4-4863-8c29-b9d0475555c6', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6'),
('9a71a60a-da6d-4f73-9efc-2866dbf7240c', 'a7e8833f-d0ff-451e-8fea-2e10a6688852', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('331ad1a7-f0ba-48c8-8d6b-8616a048eee6', '352a131e-8d53-4a5f-ae70-897a12b65fc8', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('4722921e-7f86-47a2-bf83-dc168393544c', '01e47996-7488-49df-a79b-17d186fad7ce', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('e567c335-e984-4890-b2b3-9db40eb83e6b', '41804426-5fe1-44b9-b724-93496cb97400', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('f865a775-6f14-4c66-adf9-7dae71b37567', '19855ce9-308c-4822-968c-3016d48da869', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('a36b91ef-a49b-46cd-b8ee-35644da61766', '04d555f3-96f1-4716-b8fa-d1d379f87819', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('f51cd222-5bc8-4588-84e2-496e5edff0f4', '61e38941-9430-4f8a-80e0-311a3ac3b439', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('bb1eb458-3617-4782-b66a-a4c659ede5bd', '048d14b5-08c8-4909-9a5e-22dac851baa6', '2820eca0-e63c-47af-b265-e89c16d3b582'),
('315a9579-e81f-45e3-9f41-40d2f0821264', '7143d7db-f514-4623-8725-cca5f4b91555', '7542391e-7d8c-4818-99c0-cb1c6cb62483');

-- --------------------------------------------------------

--
-- Struktura tabulky `course_location`
--

CREATE TABLE IF NOT EXISTS `course_location` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `name` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `course_location`
--

INSERT INTO `course_location` (`uuid`, `name`, `description`) VALUES
('fd1a8052-b288-4f2f-b1c6-4fc1a8eca3b8', 'Bazén Ostrava- Poruba', 'Gen. Sochora 1378\nOstrava- Poruba \n708 00'),
('2043c750-01f7-4205-94bd-4717b01f1431', 'Bazén- Ještěrka', 'Za Ještěrkou 629/1\nOstrava- Bartovice\n717 00'),
('aec9a3f4-b8bb-460d-87f8-6368655ea510', 'Bazén- Hrabůvka', 'A. Kučery 1276/20\nOstrava- Hrabůvka\n700 30'),
('afdcab28-febc-4e1c-bbfd-57c466661c79', 'Bazén- Čapkárna', 'Sokolská tř. 2590/44\nOstrava- Moravská Ostrava a Přívoz\n702 00');

-- --------------------------------------------------------

--
-- Struktura tabulky `course_participant`
--

CREATE TABLE IF NOT EXISTS `course_participant` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `birthdate` date DEFAULT NULL,
  `personal_number` varchar(12) COLLATE utf8_czech_ci DEFAULT NULL,
  `health_insurance` varchar(240) COLLATE utf8_czech_ci DEFAULT NULL,
  `health_info` varchar(524) COLLATE utf8_czech_ci DEFAULT NULL,
  `user_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `contact_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `course_participant`
--

INSERT INTO `course_participant` (`uuid`, `birthdate`, `personal_number`, `health_insurance`, `health_info`, `user_uuid`, `contact_uuid`, `modif_at`, `modif_by`) VALUES
('048d14b5-08c8-4909-9a5e-22dac851baa6', '1999-01-02', '990102/5553', 'VZP', NULL, '06720764-bbc8-4326-ae0d-c098e3a04926', '8ee15fbc-cff4-4b23-b3b9-78c78d09af03', '2018-06-25 17:40:49', 'anonymousUser'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', '2001-02-03', '010203/5554', '211', NULL, 'e219e225-bdd3-4ed8-ae61-21096b34d3bd', 'e700debe-9ad9-4ba1-8e29-2137a28fb5d1', '2018-06-25 17:43:36', 'anonymousUser'),
('04d555f3-96f1-4716-b8fa-d1d379f87819', '2005-01-02', '050102/5552', 'VZP', NULL, '6452301a-46ad-4f1d-894c-125e19866dde', '26e3174b-134b-4239-8756-161ab3d970bc', '2018-06-25 17:46:19', 'anonymousUser'),
('19855ce9-308c-4822-968c-3016d48da869', '2007-01-02', '070102/5552', '211', NULL, '2bf2e6e4-2893-4720-81b3-5939cfb5ba5c', '49e08947-53e4-4113-9f84-991f67e478e9', '2018-06-25 17:48:26', 'anonymousUser'),
('41804426-5fe1-44b9-b724-93496cb97400', '2010-01-02', '100102/5551', 'VZP', NULL, '49f498d1-d557-4b0c-8690-76dcec7f3c33', '02d03b5c-c2aa-4c38-b15b-f2498b94d4b5', '2018-06-25 17:50:13', 'anonymousUser'),
('01e47996-7488-49df-a79b-17d186fad7ce', '2012-01-03', '120103/5551', 'VZP', NULL, '7e3aa524-13f1-4396-a798-4ba070e37909', '80985d99-c056-48e2-a68b-06a7fcbdb8a2', '2018-06-25 17:55:55', 'anonymousUser'),
('352a131e-8d53-4a5f-ae70-897a12b65fc8', '2014-03-01', '140102/5551', 'Hornická', NULL, '7b02f7f5-0175-4578-8a73-f243fb5bdadb', 'f2709982-aeff-4303-9d28-201742493e2e', '2018-06-25 17:58:20', 'anonymousUser'),
('388891ea-50d4-4863-8c29-b9d0475555c6', '2010-03-07', '100307/5552', 'VZP', NULL, '1158b89b-a79b-4e4e-8bd4-b79264ddedab', 'd9d74a56-aa5b-40d2-803b-1e6b3d3b73ed', '2018-06-25 17:59:57', 'anonymousUser'),
('a8cef7f9-d45f-493f-97fe-4e81c64498cc', '2012-01-02', '120102/6543', '211', NULL, '57b669fe-6c56-4ec1-b665-523a769b2e4f', '6e87cf89-9ca8-471f-a0cc-1597d1787117', '2018-06-25 18:02:11', 'anonymousUser'),
('53887cee-292d-49e9-8541-98f63a03acce', '2012-05-12', '110512/5552', '211', NULL, 'af05d616-82c6-4602-9db2-26b0561c4bbf', 'b2b5b497-f776-4e25-9d2d-ca913d92599f', '2018-06-25 18:06:23', 'anonymousUser'),
('c9e168fc-5e84-4c12-bb13-c7e48a6d7627', '2012-09-08', '120908/5551', 'VZP', NULL, '3c9ff9d4-9651-499f-98af-39fb1b219725', 'a5d23323-4eeb-4b54-bd36-8686e0833d45', '2018-06-25 18:12:20', 'anonymousUser'),
('db6ea44a-7bee-4c57-9951-389c39163903', '2010-02-03', '100203/5556', 'VZP', NULL, '43f2ac4a-3049-423a-8876-46ed39eef7d7', 'd91edb1b-13bb-4f94-bd93-f5874591b246', '2018-06-25 18:14:10', 'anonymousUser'),
('996b0547-6d63-4de4-ae67-ae620ef8d5ee', '2010-09-08', '100908/5552', '211', NULL, 'ec8132b7-1ca1-47a6-88ce-4ba1129b6fc3', 'add3eec0-6022-4547-b003-8ebda55d77fa', '2018-06-25 18:16:45', 'anonymousUser'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', '2010-07-05', '100705/6543', '211', NULL, '13f2f2b7-692f-4a7b-967b-362e36d28edb', '879a4671-11e9-48ec-801d-07effd5d953c', '2018-06-25 18:19:41', 'anonymousUser'),
('7143d7db-f514-4623-8725-cca5f4b91555', '2010-02-03', '100203/5552', 'VZP', NULL, '45edf303-08d2-42db-aeea-a802a8953466', '9b6e998d-d6f1-4b76-baae-37050a3e4b21', '2018-06-25 18:21:25', 'anonymousUser'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '2012-07-03', '120703/5552', '211', NULL, '859a498a-bf02-4c5d-844a-c44b78c59bb6', '5d6a6d0c-dae6-4c09-b06d-109d32cff373', '2018-06-25 18:22:45', 'anonymousUser'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', '2008-01-01', '080101/5552', 'Ministerstva vnitra', NULL, '1073b7cc-0dbb-44de-ad14-e4a828a4a11e', '5217f32d-63d4-46c4-8fc6-5a4443b1c29e', '2018-06-25 18:24:46', 'anonymousUser'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', '2010-03-06', '100306/5552', 'VZP', NULL, 'd828cb35-c174-4548-868a-aa880bd92303', 'fda4d2df-3386-499c-9d2d-3d69d6493bb8', '2018-06-25 18:26:50', 'anonymousUser'),
('261dc4e2-f751-43cb-b19f-b09cf6aa9178', '2012-01-02', '120102/5454', 'VZP', NULL, 'ec8132b7-1ca1-47a6-88ce-4ba1129b6fc3', 'c736384c-637a-4d3e-8d16-fa5bef237bfa', '2018-06-25 18:37:28', 'jaroslav.ondracek@nomail.cz'),
('53696a6b-97b9-48d1-872c-b1330e81da60', '2012-03-04', '120304/5552', 'VZP', NULL, 'ec8132b7-1ca1-47a6-88ce-4ba1129b6fc3', '64dfca15-16b5-442e-aa7a-58326769820c', '2018-06-25 19:19:25', 'jaroslav.ondracek@nomail.cz'),
('a7e8833f-d0ff-451e-8fea-2e10a6688852', '1999-06-27', '810507/5559', 'VZP', NULL, '8edb5311-8a58-45a3-afa6-de5e22275880', '922ca06a-e419-438a-aa8e-3a7d7696184f', '2018-07-17 15:22:27', 'anonymousUser'),
('78c61465-c76e-4d2c-b126-2f35bd8dae2d', '1981-05-04', '878878/7878', 'VZP', NULL, '71f3e5f1-acea-4130-93f7-e8863579df1f', '1b12a14f-7c24-4ea3-b4da-5c93dfa70925', '2018-07-17 15:58:14', 'anonymousUser');

-- --------------------------------------------------------

--
-- Struktura tabulky `learning_lesson`
--

CREATE TABLE IF NOT EXISTS `learning_lesson` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `lesson_date` date NOT NULL,
  `time_from` time NOT NULL,
  `time_to` time NOT NULL,
  `description` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `additional_column_int` int(11) DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  `lesson_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `learning_lesson`
--

INSERT INTO `learning_lesson` (`uuid`, `lesson_date`, `time_from`, `time_to`, `description`, `additional_column_int`, `modif_at`, `modif_by`, `lesson_uuid`) VALUES
('61190fa1-3172-4776-aacf-c7655601ea68', '2018-06-01', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:04', 'pkadmin', '793f49b0-fdf9-4bf5-8f16-4955257ef51d'),
('a380fe4e-aa64-43ae-a29e-1d556c28b630', '2018-06-04', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:11', 'pkadmin', 'c0845eee-df17-46a0-b840-ebfaf4fc6e96'),
('4be8a45e-2523-4a70-8b60-af62e3d7c8a5', '2018-06-08', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:16', 'pkadmin', '793f49b0-fdf9-4bf5-8f16-4955257ef51d'),
('13ad9409-94f8-4b85-88ed-053aefd4a853', '2018-06-11', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:19', 'pkadmin', 'c0845eee-df17-46a0-b840-ebfaf4fc6e96'),
('69cd90d0-5d1e-41f4-8872-c410ba56e02f', '2018-06-15', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:24', 'pkadmin', '793f49b0-fdf9-4bf5-8f16-4955257ef51d'),
('ce22546c-12bd-4ab6-b2fe-8a0e7a0a26d0', '2018-06-18', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:28', 'pkadmin', 'c0845eee-df17-46a0-b840-ebfaf4fc6e96'),
('8262e8fd-61e8-4e68-84bd-5cbe6a5bf465', '2018-06-22', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:34', 'pkadmin', '793f49b0-fdf9-4bf5-8f16-4955257ef51d'),
('6984bc4a-8e7f-4de6-9cdc-2979d39f841a', '2018-06-25', '15:30:00', '16:30:00', NULL, NULL, '2018-06-26 04:32:39', 'pkadmin', 'c0845eee-df17-46a0-b840-ebfaf4fc6e96'),
('e7037443-c157-4e8c-abf1-0ff5a0847c7e', '2018-06-01', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:09', 'pkadmin', 'a1258184-867e-4a21-aecb-7c1587321e54'),
('f932f72f-eb2b-4d24-9028-814bfb02f3df', '2018-06-05', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:15', 'pkadmin', '5263d809-8f3a-4824-8e59-47205bff3ca6'),
('b68f61fe-9aa8-45e1-856b-d76b372b364b', '2018-06-08', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:19', 'pkadmin', 'a1258184-867e-4a21-aecb-7c1587321e54'),
('12c1bf19-3022-435b-a86b-de4291ac4b66', '2018-06-12', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:23', 'pkadmin', '5263d809-8f3a-4824-8e59-47205bff3ca6'),
('9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4', '2018-06-15', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:28', 'pkadmin', 'a1258184-867e-4a21-aecb-7c1587321e54'),
('1d7ab92d-0f26-4deb-a2ac-689f5146506b', '2018-06-19', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:32', 'pkadmin', '5263d809-8f3a-4824-8e59-47205bff3ca6'),
('b4c128d4-3619-4161-be28-093fa2af4867', '2018-06-22', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:36', 'pkadmin', 'a1258184-867e-4a21-aecb-7c1587321e54'),
('4c2a1b57-53ff-48e4-ad33-d06ed75d9dea', '2018-06-26', '17:00:00', '18:30:00', NULL, NULL, '2018-06-26 04:33:40', 'pkadmin', '5263d809-8f3a-4824-8e59-47205bff3ca6'),
('f51b8532-8e04-476f-bc66-fb693548998e', '2018-06-04', '15:00:00', '16:00:00', NULL, NULL, '2018-06-26 04:33:57', 'pkadmin', '6c6361ca-67c2-4dd8-b5d3-b6e3aec7cb08'),
('fedca5a0-520a-4d2a-a2f8-991ee3ca661c', '2018-06-07', '15:00:00', '16:00:00', NULL, NULL, '2018-06-26 04:34:01', 'pkadmin', '2aade5d2-9d6a-4de6-899e-f0e43c4fbbcd'),
('4f1d030f-6f5f-47e8-9e90-2a8c902b6b95', '2018-06-11', '15:00:00', '16:00:00', NULL, NULL, '2018-06-26 04:34:05', 'pkadmin', '6c6361ca-67c2-4dd8-b5d3-b6e3aec7cb08'),
('12102b6f-9700-4eda-b1e9-5af64175106d', '2018-06-14', '15:00:00', '16:00:00', NULL, NULL, '2018-06-26 04:34:10', 'pkadmin', '2aade5d2-9d6a-4de6-899e-f0e43c4fbbcd'),
('acac9ad7-5534-4864-bd57-f386783a5d0a', '2018-06-18', '15:00:00', '16:00:00', NULL, NULL, '2018-06-26 04:34:14', 'pkadmin', '6c6361ca-67c2-4dd8-b5d3-b6e3aec7cb08'),
('0589ab41-6099-4723-b23e-698840b8dd70', '2018-06-21', '15:00:00', '16:00:00', NULL, NULL, '2018-06-26 04:34:18', 'pkadmin', '2aade5d2-9d6a-4de6-899e-f0e43c4fbbcd'),
('e372934a-1c0b-46d0-9e52-34543b2eb679', '2018-06-25', '15:00:00', '16:00:00', NULL, NULL, '2018-06-26 04:34:21', 'pkadmin', '6c6361ca-67c2-4dd8-b5d3-b6e3aec7cb08'),
('b4b346cd-334d-4906-9c95-6fec5c6923cf', '2018-07-02', '15:30:00', '16:30:00', NULL, NULL, '2018-07-09 06:37:51', 'pktrener', 'c0845eee-df17-46a0-b840-ebfaf4fc6e96'),
('b35b5254-e659-4b86-b4b2-a0ab2af62b4a', '2018-07-02', '17:00:00', '18:30:00', NULL, 0, '2018-07-26 09:45:30', 'pktrener', '2e40e4d9-0282-454a-90da-5f96ddca17a9'),
('d28eb99e-cd37-4db4-bc64-e899b0dc0d50', '2018-07-02', '17:00:00', '18:00:00', NULL, NULL, '2018-07-26 09:04:51', 'pktrener', '6c6361ca-67c2-4dd8-b5d3-b6e3aec7cb08'),
('12cb9fd2-bfe7-43ec-bf2c-a0547fd81352', '2018-07-03', '17:00:00', '18:30:00', NULL, NULL, '2018-07-26 09:45:40', 'pktrener', '722a3494-f611-4f63-a5b7-c3d4906fff3b');

-- --------------------------------------------------------

--
-- Struktura tabulky `lesson`
--

CREATE TABLE IF NOT EXISTS `lesson` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `time_from` time NOT NULL,
  `time_to` time NOT NULL,
  `day_of_week` enum('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY') COLLATE utf8_czech_ci DEFAULT NULL,
  `course_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `lesson`
--

INSERT INTO `lesson` (`uuid`, `time_from`, `time_to`, `day_of_week`, `course_uuid`, `modif_at`, `modif_by`) VALUES
('6c6361ca-67c2-4dd8-b5d3-b6e3aec7cb08', '17:00:00', '18:00:00', 'MONDAY', '2820eca0-e63c-47af-b265-e89c16d3b582', '2018-07-20 12:08:45', 'pkadmin'),
('2aade5d2-9d6a-4de6-899e-f0e43c4fbbcd', '16:00:00', '17:00:00', 'WEDNESDAY', '2820eca0-e63c-47af-b265-e89c16d3b582', '2018-07-20 12:09:08', 'pkadmin'),
('5263d809-8f3a-4824-8e59-47205bff3ca6', '17:00:00', '18:00:00', 'TUESDAY', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6', '2018-07-20 14:05:42', 'pkadmin'),
('a1258184-867e-4a21-aecb-7c1587321e54', '17:00:00', '18:00:00', 'THURSDAY', '475c81e6-cf75-4fdc-b888-d572a1a0a7f6', '2018-07-20 14:05:35', 'pkadmin'),
('722a3494-f611-4f63-a5b7-c3d4906fff3b', '17:00:00', '18:30:00', 'TUESDAY', '7542391e-7d8c-4818-99c0-cb1c6cb62483', '2018-07-20 12:02:10', 'pkadmin'),
('2e40e4d9-0282-454a-90da-5f96ddca17a9', '17:00:00', '18:30:00', 'MONDAY', '7542391e-7d8c-4818-99c0-cb1c6cb62483', '2018-07-20 12:03:51', 'pkadmin'),
('fbeb9dec-2409-4fb4-a23d-6a83be6d253c', '18:00:00', '19:00:00', 'TUESDAY', '2369ab2a-c6ba-4d04-a73d-bbecc9dc5bba', '2018-07-20 14:07:19', 'pkadmin'),
('ee95864a-dea7-44b7-a039-ab2bde855255', '16:00:00', '17:00:00', 'TUESDAY', '335deeb7-9259-4267-a361-0345c9be5336', '2018-07-20 14:11:18', 'pkadmin'),
('c26beaa1-c2dc-4577-ae6f-5679dac2ce57', '17:00:00', '18:30:00', 'WEDNESDAY', '7542391e-7d8c-4818-99c0-cb1c6cb62483', '2018-07-20 12:02:29', 'pkadmin'),
('aa7d7ec3-c20f-49d5-accf-d9fc43c1b61d', '17:00:00', '18:30:00', 'THURSDAY', '7542391e-7d8c-4818-99c0-cb1c6cb62483', '2018-07-20 12:02:50', 'pkadmin'),
('5d55cc30-639a-4ba5-993d-53668bbf3652', '15:00:00', '16:30:00', 'FRIDAY', '7542391e-7d8c-4818-99c0-cb1c6cb62483', '2018-07-20 12:03:15', 'pkadmin'),
('7a7dcb95-0dec-4bcd-96fd-4a72057afe92', '16:00:00', '17:00:00', 'MONDAY', '108e9c7c-f6ba-41f0-ae8b-65e63f144a5b', '2018-07-20 12:35:22', 'pkadmin'),
('d1440f32-fa75-4daa-90aa-3e14983411c7', '16:00:00', '17:00:00', 'WEDNESDAY', '108e9c7c-f6ba-41f0-ae8b-65e63f144a5b', '2018-07-20 12:39:33', 'pkadmin'),
('4ef3d41d-656e-4dcd-8618-542ac4987df0', '18:00:00', '19:00:00', 'THURSDAY', '2369ab2a-c6ba-4d04-a73d-bbecc9dc5bba', '2018-07-20 14:07:52', 'pkadmin'),
('1904a59b-3100-4704-ab78-df63eec92376', '16:00:00', '17:00:00', 'THURSDAY', '335deeb7-9259-4267-a361-0345c9be5336', '2018-07-20 14:11:43', 'pkadmin'),
('ecded1dc-2f90-4168-a2d0-8e9dd01feda7', '17:00:00', '18:00:00', 'MONDAY', '7dd2efc4-d674-4945-9609-963f4a7bd7b6', '2018-07-20 14:18:51', 'pkadmin'),
('50b579a9-e4e5-4979-a9ee-f04dee9112fb', '17:00:00', '18:00:00', 'WEDNESDAY', '7dd2efc4-d674-4945-9609-963f4a7bd7b6', '2018-07-20 14:21:48', 'pkadmin'),
('db16b8c8-cde5-47f3-9e74-d5e5498394f1', '17:00:00', '18:00:00', 'TUESDAY', '01781a89-8c87-4c93-a4e2-7205c50e4d07', '2018-07-20 14:23:49', 'pkadmin'),
('219e553d-1214-492e-ad2a-b98ee9029791', '18:00:00', '19:00:00', 'MONDAY', 'ccd27aab-ccf6-4f4c-8321-5c4ee6238752', '2018-07-20 14:24:59', 'pkadmin'),
('c1d726b4-69ad-4e7e-af46-78a12e615a59', '18:00:00', '19:00:00', 'WEDNESDAY', 'ccd27aab-ccf6-4f4c-8321-5c4ee6238752', '2018-07-20 14:25:17', 'pkadmin'),
('e8f5bece-cab2-478e-b431-906d3c698b81', '18:00:00', '19:00:00', 'TUESDAY', '36cf953f-b9c6-4e39-96fc-9e73360ed322', '2018-07-20 14:30:56', 'pkadmin'),
('fc79fabe-af2c-41eb-8dc7-3e9bd39a2ae7', '18:00:00', '19:00:00', 'THURSDAY', '36cf953f-b9c6-4e39-96fc-9e73360ed322', '2018-07-20 14:31:17', 'pkadmin');

-- --------------------------------------------------------

--
-- Struktura tabulky `participant_learning_lesson`
--

CREATE TABLE IF NOT EXISTS `participant_learning_lesson` (
  `course_participant_uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `learning_lesson_uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`course_participant_uuid`,`learning_lesson_uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `participant_learning_lesson`
--

INSERT INTO `participant_learning_lesson` (`course_participant_uuid`, `learning_lesson_uuid`) VALUES
('01e47996-7488-49df-a79b-17d186fad7ce', '4f1d030f-6f5f-47e8-9e90-2a8c902b6b95'),
('01e47996-7488-49df-a79b-17d186fad7ce', 'f51b8532-8e04-476f-bc66-fb693548998e'),
('01e47996-7488-49df-a79b-17d186fad7ce', 'fedca5a0-520a-4d2a-a2f8-991ee3ca661c'),
('048d14b5-08c8-4909-9a5e-22dac851baa6', '12102b6f-9700-4eda-b1e9-5af64175106d'),
('048d14b5-08c8-4909-9a5e-22dac851baa6', '4f1d030f-6f5f-47e8-9e90-2a8c902b6b95'),
('048d14b5-08c8-4909-9a5e-22dac851baa6', 'd28eb99e-cd37-4db4-bc64-e899b0dc0d50'),
('048d14b5-08c8-4909-9a5e-22dac851baa6', 'e372934a-1c0b-46d0-9e52-34543b2eb679'),
('048d14b5-08c8-4909-9a5e-22dac851baa6', 'f51b8532-8e04-476f-bc66-fb693548998e'),
('048d14b5-08c8-4909-9a5e-22dac851baa6', 'fedca5a0-520a-4d2a-a2f8-991ee3ca661c'),
('04d555f3-96f1-4716-b8fa-d1d379f87819', '0589ab41-6099-4723-b23e-698840b8dd70'),
('04d555f3-96f1-4716-b8fa-d1d379f87819', '4f1d030f-6f5f-47e8-9e90-2a8c902b6b95'),
('04d555f3-96f1-4716-b8fa-d1d379f87819', 'acac9ad7-5534-4864-bd57-f386783a5d0a'),
('04d555f3-96f1-4716-b8fa-d1d379f87819', 'f51b8532-8e04-476f-bc66-fb693548998e'),
('04d555f3-96f1-4716-b8fa-d1d379f87819', 'fedca5a0-520a-4d2a-a2f8-991ee3ca661c'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', '13ad9409-94f8-4b85-88ed-053aefd4a853'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', '61190fa1-3172-4776-aacf-c7655601ea68'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', '69cd90d0-5d1e-41f4-8872-c410ba56e02f'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', '8262e8fd-61e8-4e68-84bd-5cbe6a5bf465'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', 'a380fe4e-aa64-43ae-a29e-1d556c28b630'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', 'b35b5254-e659-4b86-b4b2-a0ab2af62b4a'),
('18d89b70-8326-46bc-a46c-767d3dd3dfa5', 'b4b346cd-334d-4906-9c95-6fec5c6923cf'),
('19855ce9-308c-4822-968c-3016d48da869', '4f1d030f-6f5f-47e8-9e90-2a8c902b6b95'),
('19855ce9-308c-4822-968c-3016d48da869', 'f51b8532-8e04-476f-bc66-fb693548998e'),
('19855ce9-308c-4822-968c-3016d48da869', 'fedca5a0-520a-4d2a-a2f8-991ee3ca661c'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', '12cb9fd2-bfe7-43ec-bf2c-a0547fd81352'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', '4be8a45e-2523-4a70-8b60-af62e3d7c8a5'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', '61190fa1-3172-4776-aacf-c7655601ea68'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', '8262e8fd-61e8-4e68-84bd-5cbe6a5bf465'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', 'a380fe4e-aa64-43ae-a29e-1d556c28b630'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', 'b35b5254-e659-4b86-b4b2-a0ab2af62b4a'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', 'b4b346cd-334d-4906-9c95-6fec5c6923cf'),
('1cce3954-c33d-45ce-a6dc-c4067f01fc15', 'ce22546c-12bd-4ab6-b2fe-8a0e7a0a26d0'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '12cb9fd2-bfe7-43ec-bf2c-a0547fd81352'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '13ad9409-94f8-4b85-88ed-053aefd4a853'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '61190fa1-3172-4776-aacf-c7655601ea68'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '6984bc4a-8e7f-4de6-9cdc-2979d39f841a'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '69cd90d0-5d1e-41f4-8872-c410ba56e02f'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', '8262e8fd-61e8-4e68-84bd-5cbe6a5bf465'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', 'a380fe4e-aa64-43ae-a29e-1d556c28b630'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', 'b35b5254-e659-4b86-b4b2-a0ab2af62b4a'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', 'b4b346cd-334d-4906-9c95-6fec5c6923cf'),
('1e76abbb-4d0f-49e2-b2d2-1b999e86ed0f', 'ce22546c-12bd-4ab6-b2fe-8a0e7a0a26d0'),
('261dc4e2-f751-43cb-b19f-b09cf6aa9178', '61190fa1-3172-4776-aacf-c7655601ea68'),
('261dc4e2-f751-43cb-b19f-b09cf6aa9178', '8262e8fd-61e8-4e68-84bd-5cbe6a5bf465'),
('261dc4e2-f751-43cb-b19f-b09cf6aa9178', 'a380fe4e-aa64-43ae-a29e-1d556c28b630'),
('261dc4e2-f751-43cb-b19f-b09cf6aa9178', 'b4b346cd-334d-4906-9c95-6fec5c6923cf'),
('352a131e-8d53-4a5f-ae70-897a12b65fc8', '4f1d030f-6f5f-47e8-9e90-2a8c902b6b95'),
('352a131e-8d53-4a5f-ae70-897a12b65fc8', 'f51b8532-8e04-476f-bc66-fb693548998e'),
('352a131e-8d53-4a5f-ae70-897a12b65fc8', 'fedca5a0-520a-4d2a-a2f8-991ee3ca661c'),
('388891ea-50d4-4863-8c29-b9d0475555c6', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('388891ea-50d4-4863-8c29-b9d0475555c6', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('388891ea-50d4-4863-8c29-b9d0475555c6', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('388891ea-50d4-4863-8c29-b9d0475555c6', 'f932f72f-eb2b-4d24-9028-814bfb02f3df'),
('41804426-5fe1-44b9-b724-93496cb97400', '4f1d030f-6f5f-47e8-9e90-2a8c902b6b95'),
('41804426-5fe1-44b9-b724-93496cb97400', 'f51b8532-8e04-476f-bc66-fb693548998e'),
('41804426-5fe1-44b9-b724-93496cb97400', 'fedca5a0-520a-4d2a-a2f8-991ee3ca661c'),
('53696a6b-97b9-48d1-872c-b1330e81da60', '1d7ab92d-0f26-4deb-a2ac-689f5146506b'),
('53696a6b-97b9-48d1-872c-b1330e81da60', '4c2a1b57-53ff-48e4-ad33-d06ed75d9dea'),
('53696a6b-97b9-48d1-872c-b1330e81da60', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('53696a6b-97b9-48d1-872c-b1330e81da60', 'b4c128d4-3619-4161-be28-093fa2af4867'),
('53696a6b-97b9-48d1-872c-b1330e81da60', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('53696a6b-97b9-48d1-872c-b1330e81da60', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('53696a6b-97b9-48d1-872c-b1330e81da60', 'f932f72f-eb2b-4d24-9028-814bfb02f3df'),
('53887cee-292d-49e9-8541-98f63a03acce', '12c1bf19-3022-435b-a86b-de4291ac4b66'),
('53887cee-292d-49e9-8541-98f63a03acce', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('53887cee-292d-49e9-8541-98f63a03acce', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('53887cee-292d-49e9-8541-98f63a03acce', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('53887cee-292d-49e9-8541-98f63a03acce', 'f932f72f-eb2b-4d24-9028-814bfb02f3df'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', '0589ab41-6099-4723-b23e-698840b8dd70'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', '12102b6f-9700-4eda-b1e9-5af64175106d'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', '4f1d030f-6f5f-47e8-9e90-2a8c902b6b95'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', 'acac9ad7-5534-4864-bd57-f386783a5d0a'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', 'd28eb99e-cd37-4db4-bc64-e899b0dc0d50'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', 'f51b8532-8e04-476f-bc66-fb693548998e'),
('61e38941-9430-4f8a-80e0-311a3ac3b439', 'fedca5a0-520a-4d2a-a2f8-991ee3ca661c'),
('7143d7db-f514-4623-8725-cca5f4b91555', '4be8a45e-2523-4a70-8b60-af62e3d7c8a5'),
('7143d7db-f514-4623-8725-cca5f4b91555', '61190fa1-3172-4776-aacf-c7655601ea68'),
('7143d7db-f514-4623-8725-cca5f4b91555', '8262e8fd-61e8-4e68-84bd-5cbe6a5bf465'),
('7143d7db-f514-4623-8725-cca5f4b91555', 'a380fe4e-aa64-43ae-a29e-1d556c28b630'),
('7143d7db-f514-4623-8725-cca5f4b91555', 'b35b5254-e659-4b86-b4b2-a0ab2af62b4a'),
('7143d7db-f514-4623-8725-cca5f4b91555', 'b4b346cd-334d-4906-9c95-6fec5c6923cf'),
('78c61465-c76e-4d2c-b126-2f35bd8dae2d', '12cb9fd2-bfe7-43ec-bf2c-a0547fd81352'),
('78c61465-c76e-4d2c-b126-2f35bd8dae2d', 'b35b5254-e659-4b86-b4b2-a0ab2af62b4a'),
('996b0547-6d63-4de4-ae67-ae620ef8d5ee', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('996b0547-6d63-4de4-ae67-ae620ef8d5ee', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('996b0547-6d63-4de4-ae67-ae620ef8d5ee', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('996b0547-6d63-4de4-ae67-ae620ef8d5ee', 'f932f72f-eb2b-4d24-9028-814bfb02f3df'),
('a8cef7f9-d45f-493f-97fe-4e81c64498cc', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('a8cef7f9-d45f-493f-97fe-4e81c64498cc', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('a8cef7f9-d45f-493f-97fe-4e81c64498cc', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('a8cef7f9-d45f-493f-97fe-4e81c64498cc', 'f932f72f-eb2b-4d24-9028-814bfb02f3df'),
('c9e168fc-5e84-4c12-bb13-c7e48a6d7627', '12c1bf19-3022-435b-a86b-de4291ac4b66'),
('c9e168fc-5e84-4c12-bb13-c7e48a6d7627', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('c9e168fc-5e84-4c12-bb13-c7e48a6d7627', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('c9e168fc-5e84-4c12-bb13-c7e48a6d7627', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('c9e168fc-5e84-4c12-bb13-c7e48a6d7627', 'f932f72f-eb2b-4d24-9028-814bfb02f3df'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', '1d7ab92d-0f26-4deb-a2ac-689f5146506b'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', '4c2a1b57-53ff-48e4-ad33-d06ed75d9dea'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', 'b4c128d4-3619-4161-be28-093fa2af4867'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('cbb75dc2-181b-442a-ae0c-4a03f289fe49', 'f932f72f-eb2b-4d24-9028-814bfb02f3df'),
('db6ea44a-7bee-4c57-9951-389c39163903', '12c1bf19-3022-435b-a86b-de4291ac4b66'),
('db6ea44a-7bee-4c57-9951-389c39163903', '9aae0e2f-6dfe-4053-8f4c-b00dc3de35b4'),
('db6ea44a-7bee-4c57-9951-389c39163903', 'b68f61fe-9aa8-45e1-856b-d76b372b364b'),
('db6ea44a-7bee-4c57-9951-389c39163903', 'e7037443-c157-4e8c-abf1-0ff5a0847c7e'),
('db6ea44a-7bee-4c57-9951-389c39163903', 'f932f72f-eb2b-4d24-9028-814bfb02f3df');

-- --------------------------------------------------------

--
-- Struktura tabulky `payment`
--

CREATE TABLE IF NOT EXISTS `payment` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `amount` int(11) DEFAULT NULL,
  `type` enum('CASH','BANK_TRANS','DONATE','OTHER') COLLATE utf8_czech_ci NOT NULL,
  `process_type` enum('AUTOMATIC','MANUAL','PAIRED') COLLATE utf8_czech_ci NOT NULL,
  `description` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `payment_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  `course_participant_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `course_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `bank_transaction_id_pohybu` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- --------------------------------------------------------

--
-- Struktura tabulky `result`
--

CREATE TABLE IF NOT EXISTS `result` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `result_time` mediumtext COLLATE utf8_czech_ci NOT NULL,
  `result_date` date NOT NULL,
  `style_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `distance` mediumint(9) NOT NULL,
  `description` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `course_participant_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

-- --------------------------------------------------------

--
-- Struktura tabulky `user`
--

CREATE TABLE IF NOT EXISTS `user` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `username` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `password` varchar(100) COLLATE utf8_czech_ci NOT NULL,
  `password_generated` enum('0','1') COLLATE utf8_czech_ci NOT NULL,
  `role` enum('USER','ADMIN','TRAINER') COLLATE utf8_czech_ci NOT NULL,
  `contact_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `username` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `user`
--

INSERT INTO `user` (`uuid`, `username`, `password`, `password_generated`, `role`, `contact_uuid`, `modif_at`, `modif_by`) VALUES
('56f26d38-e100-4505-ac74-ec65bf6869ab', 'kosatky', 'kosatky', '0', 'ADMIN', '62225052-4dd2-4150-91c3-8ebf26fd1572', '2018-06-02 13:34:28', 'SYSTEM'),
('06720764-bbc8-4326-ae0d-c098e3a04926', 'petr.adamus@nomail.cz', '49jQI6', '1', 'USER', 'a8ea29da-2929-4c49-8157-93200161d6d6', '2018-06-25 17:40:50', 'anonymousUser'),
('e219e225-bdd3-4ed8-ae61-21096b34d3bd', 'pavel.bar@nomail.com', 'otqkZ7', '1', 'USER', '33e6dfd3-64d7-41d8-8d12-e81a7583252e', '2018-06-25 17:43:36', 'anonymousUser'),
('6452301a-46ad-4f1d-894c-125e19866dde', 'ondrej.cizek@nomail.cz', 'MU5NX1', '1', 'USER', '4034c63a-d025-4432-97ae-fab50258a4aa', '2018-06-25 17:46:19', 'anonymousUser'),
('2bf2e6e4-2893-4720-81b3-5939cfb5ba5c', 'martin.drozd@nomail.cz', 'ILVYJ2', '1', 'USER', '7cea7cfc-4d76-415e-ac3b-936023d7b35d', '2018-06-25 17:48:26', 'anonymousUser'),
('49f498d1-d557-4b0c-8690-76dcec7f3c33', 'tomas.ehl@nomail.com', 'VuPwD7', '1', 'USER', '0030739b-5715-493d-b37f-1b5502ddc5e4', '2018-06-25 17:50:13', 'anonymousUser'),
('7e3aa524-13f1-4396-a798-4ba070e37909', 'petr.fiala@nomail.cz', 'OWLFC8', '1', 'USER', 'b6a1def8-51d7-4216-b8f7-e2d6c8092ade', '2018-06-25 17:55:55', 'anonymousUser'),
('7b02f7f5-0175-4578-8a73-f243fb5bdadb', 'david.gyna@nomail.cz', 'YpynA3', '1', 'USER', 'd3a358b3-6d83-455d-ad0e-6ece252d229e', '2018-06-25 17:58:20', 'anonymousUser'),
('1158b89b-a79b-4e4e-8bd4-b79264ddedab', 'tereza.honova@nomail.cz', 'iBWyu7', '1', 'USER', '1326620c-de30-4336-b346-a0b6499c42c8', '2018-06-25 17:59:57', 'anonymousUser'),
('57b669fe-6c56-4ec1-b665-523a769b2e4f', 'miluse.janova@nomail.com', 'KcvsI0', '1', 'USER', '64560c69-2f5b-4cf3-b4fe-9a2d7f3783c6', '2018-06-25 18:02:11', 'anonymousUser'),
('af05d616-82c6-4602-9db2-26b0561c4bbf', 'lucie.smekalova@nomail.cz', 'ZnpGS6', '1', 'USER', '50f30982-c291-4243-a10b-240879e553ef', '2018-06-25 18:06:23', 'anonymousUser'),
('3c9ff9d4-9651-499f-98af-39fb1b219725', 'lukas.macourek@nomail.com', 'NvS7I7', '1', 'USER', '69dbfe88-98cc-4fef-9d7a-102c9e1cb4bf', '2018-06-25 18:12:20', 'anonymousUser'),
('43f2ac4a-3049-423a-8876-46ed39eef7d7', 'david.novacek@nomail.com', 'Q9P1x9', '1', 'USER', '7c34badc-0961-4eeb-b7b4-e2021b010e5b', '2018-06-25 18:14:10', 'anonymousUser'),
('ec8132b7-1ca1-47a6-88ce-4ba1129b6fc3', 'jaroslav.ondracek@nomail.cz', 'jO8Ri3', '1', 'USER', 'd903b079-e664-4b29-8dfe-20b26964df03', '2018-06-25 19:19:25', 'jaroslav.ondracek@nomail.cz'),
('13f2f2b7-692f-4a7b-967b-362e36d28edb', 'vratislav.pykal@nomail.cz', 'aVtI01', '1', 'USER', '1d9e89a7-75f9-4704-960c-94868489c455', '2018-06-25 18:19:41', 'anonymousUser'),
('45edf303-08d2-42db-aeea-a802a8953466', 'ludek.rampula@nomail.cz', 'aUxou3', '1', 'USER', '798ab432-4d13-4a5e-8d61-3c782aa44fdc', '2018-06-25 18:21:25', 'anonymousUser'),
('859a498a-bf02-4c5d-844a-c44b78c59bb6', 'martina.stachova@nomail.cz', 'keX3q8', '1', 'USER', '162abf4b-b6aa-47c9-b8cb-27d4d501d658', '2018-06-25 18:22:45', 'anonymousUser'),
('1073b7cc-0dbb-44de-ad14-e4a828a4a11e', 'radka.tokanova@nomail.cz', 'jM5iM7', '1', 'USER', 'adc485f6-39b6-4e4e-a054-c5d6e12aa5cb', '2018-06-25 18:24:46', 'anonymousUser'),
('d828cb35-c174-4548-868a-aa880bd92303', 'petr.uhlir@nomail.cz', 'Zf2zP4', '1', 'USER', '3e8fe8fc-fc06-4575-8d66-8637e9639222', '2018-06-25 18:26:50', 'anonymousUser'),
('c521a2b3-93ad-431b-b5b6-bb13578f92b5', 'pkadmin', 'KnLNE5', '1', 'ADMIN', '82f7e963-3c66-43ff-b910-e5c5001b4964', '2018-06-25 18:30:32', 'kosatky'),
('fd81133f-54f0-4437-a98d-3584bcf0ab43', 'pktrener', 'TuquQ3', '1', 'TRAINER', 'bd4cf3f4-f81e-47f3-8dba-ffd585743b62', '2018-06-25 18:32:03', 'kosatky'),
('8edb5311-8a58-45a3-afa6-de5e22275880', 'jakub.zaoralek@gmail.com', 'kCcWm2', '1', 'USER', '88c14bfd-ee37-4479-ba1e-2b13d75e0843', '2018-07-17 15:22:27', 'anonymousUser'),
('71f3e5f1-acea-4130-93f7-e8863579df1f', 'jakub.zaoralek@seznam.cz', '06xUN3', '1', 'USER', '3d265bfc-3bdd-401b-b55c-b1b832fe7a37', '2018-07-17 15:58:15', 'anonymousUser');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
