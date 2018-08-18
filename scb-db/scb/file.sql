-- phpMyAdmin SQL Dump
-- version 3.4.11.1deb2+deb7u8
-- http://www.phpmyadmin.net
--
-- Počítač: localhost
-- Vygenerováno: Sob 18. srp 2018, 22:21
-- Verze MySQL: 5.6.39
-- Verze PHP: 5.3.29-1~dotdeb.0

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Databáze: `scb`
--

-- --------------------------------------------------------

--
-- Struktura tabulky `file`
--

CREATE TABLE IF NOT EXISTS `file` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `name` varchar(240) CHARACTER SET utf8 DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  `content` longblob,
  `content_type` varchar(128) COLLATE utf8_czech_ci DEFAULT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `file`
--

INSERT INTO `file` (`uuid`, `name`, `description`, `content`, `content_type`) VALUES
('fd33a4d4-7e99-11e6-ae22-56b6b6499628', 'gdpr.docx', 'gdpr', NULL, 'application/file'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499630', 'health-info.docx', 'health info', NULL, 'application/file'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499632', 'lekarska_prohlidka.docx', 'health exam', NULL, 'application/file'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499634', 'zasady-pro-prijeti-do-klubu.pdf', 'club rules', NULL, 'application/file');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
