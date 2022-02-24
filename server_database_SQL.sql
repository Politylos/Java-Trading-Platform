-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.5.9-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for tradingplatform
CREATE DATABASE IF NOT EXISTS `tradingplatform` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `tradingplatform`;

-- Dumping structure for table tradingplatform.assets
CREATE TABLE IF NOT EXISTS `assets` (
  `Asset_id` int(11) NOT NULL AUTO_INCREMENT,
  `Name` tinytext NOT NULL,
  `Description` text DEFAULT NULL,
  PRIMARY KEY (`Asset_id`),
  UNIQUE KEY `Asset_id` (`Asset_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=latin1;

-- Dumping data for table tradingplatform.assets: ~10 rows (approximately)
/*!40000 ALTER TABLE `assets` DISABLE KEYS */;
INSERT INTO `assets` (`Asset_id`, `Name`, `Description`) VALUES
	(4, 'shiny Glass', 'Very sexy shiny glass. made in veina'),
	(5, 'Gold', '1ou of Gold'),
	(6, 'Server', 'Buy a server module to run your own server off of'),
	(7, 'Bear', 'Not a toy!!! This is a real bear, buy at your own risk'),
	(8, 'Chips', 'Just plain old corn chips'),
	(9, 'Wood', 'a peice of a tree'),
	(10, 'Silver', 'like gold just not as good'),
	(11, 'Pot plant', 'Place these qround your house'),
	(12, 'magnets ', 'fun to play with but not to eat'),
	(13, 'wires', 'place these in a circuit and watch it go'),
	(14, 'paper', 'like wood but thinner');
/*!40000 ALTER TABLE `assets` ENABLE KEYS */;

-- Dumping structure for table tradingplatform.organisationassets
CREATE TABLE IF NOT EXISTS `organisationassets` (
  `OA_id` int(11) NOT NULL AUTO_INCREMENT,
  `Organisation_id` int(11) NOT NULL,
  `Asset_id` int(11) NOT NULL,
  `Amount` int(11) NOT NULL,
  PRIMARY KEY (`OA_id`),
  UNIQUE KEY `OA_id` (`OA_id`),
  KEY `FK_organisationassets_organisations` (`Organisation_id`),
  KEY `FK_organisationassets_assets` (`Asset_id`),
  CONSTRAINT `FK_organisationassets_assets` FOREIGN KEY (`Asset_id`) REFERENCES `assets` (`Asset_id`),
  CONSTRAINT `FK_organisationassets_organisations` FOREIGN KEY (`Organisation_id`) REFERENCES `organisations` (`Organisation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;

-- Dumping data for table tradingplatform.organisationassets: ~11 rows (approximately)
/*!40000 ALTER TABLE `organisationassets` DISABLE KEYS */;
INSERT INTO `organisationassets` (`OA_id`, `Organisation_id`, `Asset_id`, `Amount`) VALUES
	(14, 1, 12, 1000),
	(16, 2, 5, 999970),
	(17, 2, 10, 1000),
	(19, 2, 8, 10),
	(20, 1, 9, 1834),
	(21, 2, 9, 100),
	(22, 1, 11, 100),
	(23, 1, 5, 140),
	(24, 1, 7, 12),
	(25, 1, 8, 100);
/*!40000 ALTER TABLE `organisationassets` ENABLE KEYS */;

-- Dumping structure for table tradingplatform.organisations
CREATE TABLE IF NOT EXISTS `organisations` (
  `Organisation_id` int(11) NOT NULL AUTO_INCREMENT,
  `Organisation_Name` tinytext NOT NULL,
  `Credits` double NOT NULL DEFAULT 0,
  PRIMARY KEY (`Organisation_id`),
  UNIQUE KEY `Organisation_id` (`Organisation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;

-- Dumping data for table tradingplatform.organisations: ~4 rows (approximately)
/*!40000 ALTER TABLE `organisations` DISABLE KEYS */;
INSERT INTO `organisations` (`Organisation_id`, `Organisation_Name`, `Credits`) VALUES
	(1, 'Snup', 920),
	(2, 'MegaCorp', 1480),
	(3, 'M&P', 100),
	(4, 'QLW', 11000);
/*!40000 ALTER TABLE `organisations` ENABLE KEYS */;

-- Dumping structure for table tradingplatform.tradehistory
CREATE TABLE IF NOT EXISTS `tradehistory` (
  `TH_id` int(11) NOT NULL AUTO_INCREMENT,
  `Trade_id` int(11) NOT NULL,
  `Trade_type` varchar(4) NOT NULL DEFAULT '',
  `Asset_id` int(11) NOT NULL,
  `Organisation_id` int(11) NOT NULL,
  `Amount` int(11) NOT NULL,
  `Cost` double NOT NULL DEFAULT 0,
  `Post_date` datetime NOT NULL,
  PRIMARY KEY (`TH_id`),
  UNIQUE KEY `TH_id` (`TH_id`),
  KEY `FK_tradehistory_assets` (`Asset_id`),
  KEY `FK_tradehistory_organisations` (`Organisation_id`),
  CONSTRAINT `FK_tradehistory_assets` FOREIGN KEY (`Asset_id`) REFERENCES `assets` (`Asset_id`),
  CONSTRAINT `FK_tradehistory_organisations` FOREIGN KEY (`Organisation_id`) REFERENCES `organisations` (`Organisation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1;

-- Dumping data for table tradingplatform.tradehistory: ~20 rows (approximately)
/*!40000 ALTER TABLE `tradehistory` DISABLE KEYS */;
INSERT INTO `tradehistory` (`TH_id`, `Trade_id`, `Trade_type`, `Asset_id`, `Organisation_id`, `Amount`, `Cost`, `Post_date`) VALUES
	(1, 10, '1', 7, 3, 100, 100, '2021-05-19 16:01:43'),
	(2, 11, '2', 7, 4, 100, 100, '2021-05-19 16:02:35'),
	(3, 12, '1', 5, 1, 10, 200, '2021-05-19 16:03:21'),
	(4, 13, '2', 5, 2, 10, 200, '2021-05-19 16:03:43'),
	(5, 14, '1', 7, 2, 30, 200, '2021-05-19 16:04:30'),
	(6, 15, '2', 7, 4, 30, 200, '2021-05-19 16:04:36'),
	(7, 25, '2', 4, 2, 2, 13, '2021-06-03 01:16:16'),
	(8, 29, '2', 4, 2, 2, 13, '2021-06-03 01:26:33'),
	(9, 29, '2', 4, 2, 2, 13, '2021-06-03 01:26:33'),
	(10, 31, '2', 4, 2, 2, 13, '2021-06-03 01:28:30'),
	(11, 103, '2', 4, 2, 10, 10, '2021-06-05 23:29:17'),
	(12, 152, '2', 5, 1, 5, 10, '2021-06-05 23:43:24'),
	(13, 158, '2', 5, 1, 10, 10, '2021-06-06 09:05:39'),
	(14, 159, '2', 5, 1, 10, 10, '2021-06-06 09:07:35'),
	(15, 160, '2', 5, 1, 10, 10, '2021-06-06 09:08:31'),
	(16, 162, '2', 5, 1, 10, 10, '2021-06-06 09:10:13'),
	(17, 163, '2', 5, 1, 10, 10, '2021-06-06 09:11:06'),
	(18, 170, '2', 5, 1, 1, 10, '2021-06-06 10:36:14'),
	(19, 172, '2', 9, 1, 100, 5, '2021-06-06 10:42:26'),
	(20, 180, '2', 5, 2, 10, 10, '2021-06-06 14:43:23'),
	(21, 180, '2', 5, 2, 10, 10, '2021-06-06 14:43:23'),
	(22, 202, '1', 5, 2, 10, 2, '2021-06-06 19:30:38');
/*!40000 ALTER TABLE `tradehistory` ENABLE KEYS */;

-- Dumping structure for table tradingplatform.trades
CREATE TABLE IF NOT EXISTS `trades` (
  `Trade_id` int(11) NOT NULL AUTO_INCREMENT,
  `Trade_type` int(11) NOT NULL,
  `Asset_id` int(11) NOT NULL,
  `Organisation_id` int(11) NOT NULL,
  `Amount` int(11) NOT NULL,
  `Cost` double NOT NULL,
  `Post_date` datetime NOT NULL,
  PRIMARY KEY (`Trade_id`),
  UNIQUE KEY `Trade_id` (`Trade_id`),
  KEY `FK_trades_organisations` (`Organisation_id`),
  KEY `FK_trades_assets` (`Asset_id`),
  CONSTRAINT `FK_trades_assets` FOREIGN KEY (`Asset_id`) REFERENCES `assets` (`Asset_id`),
  CONSTRAINT `FK_trades_organisations` FOREIGN KEY (`Organisation_id`) REFERENCES `organisations` (`Organisation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=latin1;

-- Dumping data for table tradingplatform.trades: ~4 rows (approximately)
/*!40000 ALTER TABLE `trades` DISABLE KEYS */;
INSERT INTO `trades` (`Trade_id`, `Trade_type`, `Asset_id`, `Organisation_id`, `Amount`, `Cost`, `Post_date`) VALUES
	(194, 2, 5, 1, 90, 2, '2021-06-06 19:00:09'),
	(195, 2, 5, 3, 1, 21, '2021-03-06 19:08:06'),
	(196, 2, 5, 3, 2, 100, '2020-06-06 19:08:35'),
	(197, 2, 5, 1, 23, 21, '2021-06-02 19:09:04');
/*!40000 ALTER TABLE `trades` ENABLE KEYS */;

-- Dumping structure for table tradingplatform.users
CREATE TABLE IF NOT EXISTS `users` (
  `User_id` int(11) NOT NULL AUTO_INCREMENT,
  `FirstName` char(64) NOT NULL,
  `LastName` char(64) NOT NULL,
  `email` char(126) NOT NULL,
  `Username` char(126) NOT NULL,
  `Role` int(11) NOT NULL DEFAULT 0,
  `Organisation_id` int(11) NOT NULL DEFAULT 0,
  `Password` longblob NOT NULL,
  `Active` bit(1) NOT NULL DEFAULT b'0',
  `Last_Active` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`User_id`),
  UNIQUE KEY `User_id` (`User_id`),
  KEY `OrgUser` (`Organisation_id`),
  CONSTRAINT `OrgUser` FOREIGN KEY (`Organisation_id`) REFERENCES `organisations` (`Organisation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1 COMMENT='Table that stores all the users login and personal information';

-- Dumping data for table tradingplatform.users: ~3 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`User_id`, `FirstName`, `LastName`, `email`, `Username`, `Role`, `Organisation_id`, `Password`, `Active`, `Last_Active`) VALUES
	(1, 'Sophia', 'Politylo', 'n10489045@qut.edu.au', 'politylos', 1, 1, _binary 0x243261243130244636584B43353339317776516B6A7654356F42365A654C465A56694F3677583667626572726D64616A584C6C6470656B42736E356D, b'0', '0000-00-00 00:00:00'),
	(8, 'John', 'Smith', 'email', 'John', 2, 3, _binary 0x24326124313024473762555869714D776A2F587965646E4573754C382E4878726F62425A397759534A2F533737306B4A50343777696F744A75677947, b'0', '2021-06-05 21:10:21'),
	(11, 'bob', 'joe', 'tee', 'user', 2, 2, _binary 0x243261243130246F4447423463336E4F3934487270786E6E6576484D4F61784D4F6F42786A5630712E4249446D716C6C625964424A46736D2E6D5175, b'0', '2021-06-06 14:37:17');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
