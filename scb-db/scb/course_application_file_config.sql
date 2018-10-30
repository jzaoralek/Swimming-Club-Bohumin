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
-- Struktura tabulky `course_application_file_config`
--

CREATE TABLE IF NOT EXISTS `course_application_file_config` (
  `uuid` varchar(36) COLLATE utf8_czech_ci NOT NULL DEFAULT '',
  `type` enum('GDPR','HEALTH_INFO','HEALTH_EXAM','CLUB_RULES') COLLATE utf8_czech_ci NOT NULL,
  `file_uuid` varchar(36) COLLATE utf8_czech_ci DEFAULT NULL,
  `page_text` enum('0','1') COLLATE utf8_czech_ci NOT NULL,
  `page_attachment` enum('0','1') COLLATE utf8_czech_ci NOT NULL,
  `email_attachment` enum('0','1') COLLATE utf8_czech_ci NOT NULL,
  `description` varchar(1000) CHARACTER SET utf8 DEFAULT NULL,
  `modif_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `modif_by` varchar(36) COLLATE utf8_czech_ci NOT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `type` (`type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COLLATE=utf8_czech_ci;

--
-- Vypisuji data pro tabulku `course_application_file_config`
--

INSERT INTO `course_application_file_config` (`uuid`, `type`, `file_uuid`, `page_text`, `page_attachment`, `email_attachment`, `description`, `modif_at`, `modif_by`) VALUES
('fd33a4d4-7e99-11e6-ae22-56b6b6499629', 'GDPR', 'fd33a4d4-7e99-11e6-ae22-56b6b6499628', '1', '0', '0', 'popis gdpr souboru', '2018-08-18 20:20:33', 'SYSTEM'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499631', 'HEALTH_INFO', 'fd33a4d4-7e99-11e6-ae22-56b6b6499630', '1', '0', '0', 'popis health info souboru', '2018-08-18 19:49:59', 'SYSTEM'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499633', 'HEALTH_EXAM', 'fd33a4d4-7e99-11e6-ae22-56b6b6499632', '0', '0', '0', 'popis health exam souboru', '2018-08-18 20:20:43', 'SYSTEM'),
('fd33a4d4-7e99-11e6-ae22-56b6b6499635', 'CLUB_RULES', 'fd33a4d4-7e99-11e6-ae22-56b6b6499634', '0', '0', '0', 'popis club rules souboru', '2018-08-18 19:50:16', 'SYSTEM');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
