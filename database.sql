CREATE DATABASE  IF NOT EXISTS `vms_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `vms_db`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: vms_db
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activity_logs`
--

DROP TABLE IF EXISTS `activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activity_logs` (
  `id` int NOT NULL AUTO_INCREMENT,
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `username` varchar(100) NOT NULL,
  `action` varchar(100) NOT NULL,
  `details` text,
  `ip_address` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity_logs`
--

LOCK TABLES `activity_logs` WRITE;
/*!40000 ALTER TABLE `activity_logs` DISABLE KEYS */;
INSERT INTO `activity_logs` VALUES (1,'2024-12-09 19:30:50','dagim','Logout','User logged out','0:0:0:0:0:0:0:1'),(2,'2024-12-09 19:36:55','dagim','Logout','User logged out','0:0:0:0:0:0:0:1'),(3,'2024-12-09 20:19:58','dagim','Logout','User logged out','0:0:0:0:0:0:0:1'),(4,'2024-12-10 00:10:59','dagim','Logout','User logged out','0:0:0:0:0:0:0:1'),(5,'2024-12-10 00:53:35','dagim','Logout','User logged out','0:0:0:0:0:0:0:1'),(6,'2024-12-10 00:54:21','dagim','Logout','User logged out','0:0:0:0:0:0:0:1'),(7,'2024-12-10 22:57:03','saka','Logout','User logged out','0:0:0:0:0:0:0:1'),(8,'2024-12-10 22:57:21','dagim','Logout','User logged out','0:0:0:0:0:0:0:1'),(9,'2024-12-20 17:11:33','saka','Logout','User logged out','127.0.0.1'),(10,'2024-12-23 16:53:55','miki','Logout','User logged out','127.0.0.1'),(11,'2024-12-23 17:00:57','miki','Logout','User logged out','127.0.0.1'),(12,'2024-12-24 10:40:32','miki','Logout','User logged out','127.0.0.1'),(13,'2024-12-26 22:13:58','saka','Logout','User logged out','127.0.0.1'),(14,'2024-12-26 22:36:15','saka','Logout','User logged out','127.0.0.1'),(15,'2024-12-26 22:43:41','saka','Logout','User logged out','127.0.0.1'),(16,'2024-12-27 11:10:33','saka','Logout','User logged out','127.0.0.1'),(17,'2024-12-27 11:11:51','saka','Logout','User logged out','127.0.0.1'),(18,'2024-12-27 13:44:45','saka','Logout','User logged out','127.0.0.1'),(19,'2024-12-27 13:45:35','mikiyas','Logout','User logged out','127.0.0.1'),(20,'2024-12-27 14:07:43','mikiyas','Logout','User logged out','127.0.0.1'),(21,'2024-12-27 15:22:03','mikiyas','Logout','User logged out','127.0.0.1'),(22,'2024-12-27 15:27:08','saka','Logout','User logged out','127.0.0.1'),(23,'2024-12-27 15:29:12','mikiyas','Logout','User logged out','127.0.0.1'),(24,'2024-12-27 15:47:20','mikiyas','Logout','User logged out','127.0.0.1');
/*!40000 ALTER TABLE `activity_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `births`
--

DROP TABLE IF EXISTS `births`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `births` (
  `birthId` varchar(255) NOT NULL,
  `childName` varchar(255) DEFAULT NULL,
  `dateOfBirth` date DEFAULT NULL,
  `parent1Name` varchar(255) DEFAULT NULL,
  `parent2Name` varchar(255) DEFAULT NULL,
  `gender` varchar(50) DEFAULT NULL,
  `birthCertificateNumber` varchar(255) DEFAULT NULL,
  `childPhoto` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`birthId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `births`
--

LOCK TABLES `births` WRITE;
/*!40000 ALTER TABLE `births` DISABLE KEYS */;
INSERT INTO `births` VALUES ('11','gebre','2024-12-12','bety','biruk','Male','11','upload_files\\11_b3.jpg'),('12','mikias','2024-12-05','shimeles','abebech','Male','12','upload_files\\12_b1.jpg'),('13','saka','2024-12-29','y/work','tigist','Male','13','upload_files\\13_b2.jpg'),('14','bety','2024-12-18','belay','bezawit','Male','14','upload_files\\14_b4.jpg'),('15','monica','2024-12-25','angel','tigistu','Female','15','upload_files\\15_b2.jpg'),('20','abebe','2024-12-04','dani','ayele','Male','56','upload_files\\20_b4.jpg'),('77','mm','2024-12-19','kk','gg','Male','09876','upload_files\\77_b5.jpg'),('b54','dagi','2000-04-18','abebe','kebedech','Male','1234','upload_files\\b54_b1.jpg');
/*!40000 ALTER TABLE `births` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deaths`
--

DROP TABLE IF EXISTS `deaths`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deaths` (
  `deathId` varchar(255) NOT NULL,
  `deceasedName` varchar(255) DEFAULT NULL,
  `dateOfDeath` date DEFAULT NULL,
  `placeOfDeath` varchar(255) DEFAULT NULL,
  `causeOfDeath` varchar(255) DEFAULT NULL,
  `deathCertificateNumber` varchar(255) DEFAULT NULL,
  `deceasedPhoto` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`deathId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deaths`
--

LOCK TABLES `deaths` WRITE;
/*!40000 ALTER TABLE `deaths` DISABLE KEYS */;
INSERT INTO `deaths` VALUES ('73','helen mulatu','2024-12-10','adiss ababa','car accident','73','upload_files\\73_g4.jpg'),('76','abebe mulatu','2024-12-18','bahirdar','civil war','76','upload_files\\76_m1.jpg'),('760','mola haile','2024-12-18','bahirdar','900','747','upload_files\\760_b3.jpg'),('764','sara mola','2024-12-27','mekele','killed by her boyfriend','74','upload_files\\764_g1.jpg'),('77','abreham damtew','2024-12-08','ethiopia','shortage of necessary resource.','4545','upload_files\\77_m2.jpg'),('82','eyob tariku','2024-12-18','uganda','not known','748','upload_files\\82_m5.jpg'),('877','muluneh mola','2024-12-25','bahirdar','unknown','412','upload_files\\877_b5.jpg'),('88','abiy ahmed','2024-12-20','adama','bomb accident','66','upload_files\\88_m4.jpg');
/*!40000 ALTER TABLE `deaths` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `divorces`
--

DROP TABLE IF EXISTS `divorces`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `divorces` (
  `divorceId` varchar(255) NOT NULL,
  `party1Name` varchar(255) DEFAULT NULL,
  `party2Name` varchar(255) DEFAULT NULL,
  `dateOfDivorce` date DEFAULT NULL,
  `legalDocuments` varchar(255) DEFAULT NULL,
  `party1Photo` varchar(255) DEFAULT NULL,
  `party2Photo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`divorceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `divorces`
--

LOCK TABLES `divorces` WRITE;
/*!40000 ALTER TABLE `divorces` DISABLE KEYS */;
INSERT INTO `divorces` VALUES ('22','muluneh mola','mulu haile','2024-12-18','55','upload_files\\22_party1_m3.jpg','upload_files\\22_party2_g2.jpg'),('31','matiwos mola','helen mulatu','2024-12-27','313','upload_files\\31_party1_m2.jpg','upload_files\\31_party2_g3.jpg'),('34','eyob seleshi','tibeb assaye','2024-12-18','555','upload_files\\34_party1_m2.jpg','upload_files\\34_party2_g2.jpg'),('45','mola haile','bety tadesse','2024-12-18','123456','upload_files\\45_party1_m2.jpg','upload_files\\45_party2_g1.jpg'),('54','getenet tariku','tsega mola','2024-12-19','879','upload_files\\54_party1_m1.jpg','upload_files\\54_party2_g1.jpg'),('8858','milion tariku','kidist haile','2024-12-18','678','upload_files\\8858_party1_m5.jpg','upload_files\\8858_party2_g4.jpg'),('99','ayele tariku','bety tadesse','2024-12-24','555','upload_files\\99_party1_m4.jpg','upload_files\\99_party2_g4.jpg');
/*!40000 ALTER TABLE `divorces` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marriages`
--

DROP TABLE IF EXISTS `marriages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `marriages` (
  `marriageId` varchar(255) NOT NULL,
  `brideName` varchar(255) DEFAULT NULL,
  `groomName` varchar(255) DEFAULT NULL,
  `dateOfMarriage` date DEFAULT NULL,
  `placeOfMarriage` varchar(255) DEFAULT NULL,
  `witness1Name` varchar(255) DEFAULT NULL,
  `witness2Name` varchar(255) DEFAULT NULL,
  `marriageCertificateNumber` varchar(255) DEFAULT NULL,
  `bridePhoto` varchar(255) DEFAULT NULL,
  `groomPhoto` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`marriageId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marriages`
--

LOCK TABLES `marriages` WRITE;
/*!40000 ALTER TABLE `marriages` DISABLE KEYS */;
INSERT INTO `marriages` VALUES ('01','edel mola','saka seleshi','2024-12-10','Hungary','eyob','chombe','01','upload_files\\01_bride_g3.jpg','upload_files\\01_groom_m3.jpg'),('12','hareg mulu','mola dessie','2024-12-25','ethiopia','eyob','miki','255','upload_files\\12_bride_g4.jpg','upload_files\\12_groom_m1.jpg'),('21','merron haile','girum arega','2024-12-12','Debremarkos','beza','haile','21','upload_files\\21_bride_g1.jpg','upload_files\\21_groom_m5.jpg'),('22','fikir haile','getnet mola','2024-12-21','bahirdar','belay','bety','22','upload_files\\22_bride_g2.jpg','upload_files\\22_groom_m4.jpg'),('24','hana tariku','getachew fasil','2024-12-13','bahirdar','sara','eyuel','24','upload_files\\24_bride_g5.jpg','upload_files\\24_groom_m2.jpg'),('253','konjit arega','kumelachew hune','2024-12-17','Debremarkos','yared','mola','545','upload_files\\253_bride_g1.jpg','upload_files\\253_groom_m2.jpg'),('26','tibeb muluneh','eyob mesfine','2024-12-04','Diredawa','chombe','saka','26','upload_files\\26_bride_g1.jpg','upload_files\\26_groom_m1.jpg');
/*!40000 ALTER TABLE `marriages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notices`
--

DROP TABLE IF EXISTS `notices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notices` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `notice_date` date NOT NULL,
  `priority` varchar(10) DEFAULT 'medium',
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notices`
--

LOCK TABLES `notices` WRITE;
/*!40000 ALTER TABLE `notices` DISABLE KEYS */;
INSERT INTO `notices` VALUES (1,'System Maintenance Notice','The system will undergo scheduled maintenance on December 25, 2024, from 2:00 AM to 4:00 AM. During this time, the system may be temporarily unavailable.','2024-12-23','high',1,'2024-12-23 14:08:07'),(2,'New Feature Update','We have added new reporting features to the Vital Management System. Users can now generate detailed monthly reports with enhanced visualization options.','2024-12-22','medium',1,'2024-12-23 14:08:07'),(3,'Holiday Schedule','Please note that our support services will be operating with limited staff during the upcoming holiday season (December 24-26, 2024).','2024-12-20','medium',1,'2024-12-23 14:08:07'),(4,'Security Update','A security update has been implemented to enhance system protection. All users are required to change their passwords within the next 7 days.','2024-12-19','high',1,'2024-12-23 14:08:07'),(5,'Training Session Announcement','A training session for new system features will be conducted on December 28, 2024. All department heads are requested to attend.','2024-12-18','low',1,'2024-12-23 14:08:07');
/*!40000 ALTER TABLE `notices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(50) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('gebre','TkmuVFFuIEPymn6TrppmHkkGPzeARhHuAiQOGBuJaz4=','vms Officer','2024-12-27 07:37:22','2024-12-27 12:26:55','Inactive'),('kidist','mRkXnY7MOFn/Kt5cDtvewLftL8sJa5d9YSnQZ56WtIE=','VMS Officer','2024-12-27 07:41:11','2024-12-27 07:41:11','Active'),('miki','01N6b3qt6yLYapJBi6WdFLWVSRGMDcrw2dKIvD33sp8=','vms Officer','2024-12-23 13:23:13','2024-12-27 11:08:59','Active'),('mikiyas','NbdcpAuDB6C4/jUoR9vHRszeKG9GYsUdqMUy2Fkc3WI=','VMS Officer','2024-12-27 08:11:30','2024-12-27 08:11:30','Active'),('saka','2LUCxN5geVuoz2LXlXnlBo/Y+kXYQSlzHHOdbIclXNY=','Admin','2024-12-07 14:14:07','2024-12-07 14:31:50','Active');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-05 21:56:57
