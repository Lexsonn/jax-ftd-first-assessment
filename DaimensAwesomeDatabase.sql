CREATE DATABASE  IF NOT EXISTS `user_file_database` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `user_file_database`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: user_file_database
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `file` (
  `file_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `filepath` varchar(256) NOT NULL,
  `data` longblob NOT NULL,
  PRIMARY KEY (`file_id`),
  UNIQUE KEY `filepath_UNIQUE` (`filepath`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file`
--

LOCK TABLES `file` WRITE;
/*!40000 ALTER TABLE `file` DISABLE KEYS */;
INSERT INTO `file` VALUES (1,'C:/poo/code/TextFiles/output.txt','ZWxpdGU='),(2,'C:/poo/output.txt','ZWxpdGU='),(3,'C:/user/userOutput.txt','ZWxpdGU='),(4,'C:/user/object.json','eyAiU2V2aWNlTWVzc2FnZSI6IHsgImpzb24iOiBbMSwyLDNdIH19');
/*!40000 ALTER TABLE `file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` char(60) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username_UNIQUE` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'user','$2a$10$P73cC8161L1ni/ZkQsKHM.TTJXfhi3Hy0V5aSW3HbyI0aV/j5.mR6'),(2,'usee','$2a$10$aPJaY2sF.HxCq.2u4U9smO2qHemWZ4AowHExkyOhrj59gRtdtj1Mm'),(3,'pass','$2a$10$JaA2WLn0mPuHvz8Aj7cm8.Uctq2Huz29vkzCA9hdZG4cQCWgrSKI2'),(4,'usss','$2a$10$nETSAx4UIcxfcYKUKX6bR.uwZieOYSdhZloMGyEgReeLRChaWquRO'),(5,'poop','$2a$10$OvsTA/ITXpLFEyaemTFIOeI0HyIqplAQ1Nm2M2mBjyKSfT9rLn8Cq'),(6,'tree','$2a$10$xvx9V6IZGrgQ.rw5wnAJFOzngVYmZwdxKFzuOdIwb/ZWwxIxxZ2Sy'),(7,'username','$2a$10$G36j8m7KhL.3Yfgj8bRnBOB11VigznxgroHno9LGVXLQDeQQGlGCm'),(8,'aaaa','$2a$10$BuLZZ0aKRA77evy75Kte8O.ruWh/gIRRIQHa2oSCHz.pBPdGphRKW'),(9,'cccc','$2a$10$88WdYKh0lROf9oTjGU1zIeGd2htaajCCcLjlGwVgEm2az5xF8ox9i'),(10,'poo','$2a$10$cvrMbtWjCzBLBqwiv88UDO36D5ZT46hOypBIKfOwkucZjU..J8awi'),(11,'poopie','$2a$10$x2YpjCCEcSsezqcvjcUDkuUVH1HcmWdqiJkD6PcfPsPvTzrQHljZS'),(12,'bbbb','$2a$10$dGMYiduba2HHbhjuNpFf0ewjpQMW7WhJKcIgJeM57d6/oKjZRLPbO'),(13,'dddd','$2a$10$6h3QEXlQIS0tGxtyUAJPbeX0/lnvtmvUMEhnQG2vuMebHUNr/FL5O'),(14,'eeee','$2a$10$9m9MeB2Z5bcRZkowpvqNWO9uYYd8jksF7eCmJLITz8H.SP52Um6ra');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_file`
--

DROP TABLE IF EXISTS `user_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_file` (
  `user_id` int(10) unsigned NOT NULL,
  `file_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`user_id`,`file_id`),
  KEY `file_user_file_fk_idx` (`file_id`),
  CONSTRAINT `file_user_file_fk` FOREIGN KEY (`file_id`) REFERENCES `file` (`file_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user_user_file_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_file`
--

LOCK TABLES `user_file` WRITE;
/*!40000 ALTER TABLE `user_file` DISABLE KEYS */;
INSERT INTO `user_file` VALUES (10,1),(10,2),(1,3),(1,4);
/*!40000 ALTER TABLE `user_file` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-07-01 12:19:47
