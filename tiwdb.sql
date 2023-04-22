CREATE DATABASE  IF NOT EXISTS `tiwproject` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `tiwproject`;
-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: tiwproject
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `calls`
--

DROP TABLE IF EXISTS `calls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `calls` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Date` date NOT NULL,
  `Time` time NOT NULL,
  `ID_Course` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `ID_Course_idx` (`ID_Course`),
  CONSTRAINT `Calls->Course` FOREIGN KEY (`ID_Course`) REFERENCES `courses` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calls`
--

LOCK TABLES `calls` WRITE;
/*!40000 ALTER TABLE `calls` DISABLE KEYS */;
/*!40000 ALTER TABLE `calls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(64) NOT NULL,
  `Description` varchar(255) NOT NULL DEFAULT 'No description',
  `ID_Lecturer` int NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Name_UNIQUE` (`Name`),
  KEY `ID_Docente_idx` (`ID_Lecturer`),
  CONSTRAINT `Courses->Lecturers` FOREIGN KEY (`ID_Lecturer`) REFERENCES `lecturers` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `degree_courses`
--

DROP TABLE IF EXISTS `degree_courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `degree_courses` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Name` varchar(64) NOT NULL,
  `Description` varchar(255) NOT NULL DEFAULT 'No description',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Nome_UNIQUE` (`Name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `degree_courses`
--

LOCK TABLES `degree_courses` WRITE;
/*!40000 ALTER TABLE `degree_courses` DISABLE KEYS */;
/*!40000 ALTER TABLE `degree_courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturers`
--

DROP TABLE IF EXISTS `lecturers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lecturers` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Surname` varchar(64) NOT NULL,
  `Name` varchar(64) NOT NULL,
  `Email` varchar(64) NOT NULL DEFAULT 'Non indicata',
  `Username` varchar(64) NOT NULL,
  `Password` varchar(64) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Email_UNIQUE` (`Email`),
  UNIQUE KEY `Username_UNIQUE` (`Username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturers`
--

LOCK TABLES `lecturers` WRITE;
/*!40000 ALTER TABLE `lecturers` DISABLE KEYS */;
/*!40000 ALTER TABLE `lecturers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registrations_calls`
--

DROP TABLE IF EXISTS `registrations_calls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registrations_calls` (
  `ID_Student` int NOT NULL,
  `ID_Call` int NOT NULL,
  `Mark` enum('Assente','Rimandato','Riprovato','18','20','21','22','23','24','25','26','27','28','29','30','30 e lode') NOT NULL,
  `EvaluationStatus` enum('Non inserito','Inserito','Pubblicato','Rifiutato','Verbalizzato') NOT NULL,
  PRIMARY KEY (`ID_Student`,`ID_Call`),
  KEY `ID_Call_idx` (`ID_Call`),
  CONSTRAINT `Registrations_calls->Calls` FOREIGN KEY (`ID_Call`) REFERENCES `calls` (`ID`) ON UPDATE CASCADE,
  CONSTRAINT `Registrations_calls->Students` FOREIGN KEY (`ID_Student`) REFERENCES `students` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registrations_calls`
--

LOCK TABLES `registrations_calls` WRITE;
/*!40000 ALTER TABLE `registrations_calls` DISABLE KEYS */;
/*!40000 ALTER TABLE `registrations_calls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `registrations_courses`
--

DROP TABLE IF EXISTS `registrations_courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registrations_courses` (
  `ID_Student` int NOT NULL,
  `ID_Course` int NOT NULL,
  PRIMARY KEY (`ID_Student`,`ID_Course`),
  KEY `Registrations_courses->Courses_idx` (`ID_Course`),
  CONSTRAINT `Registrations_courses->Courses` FOREIGN KEY (`ID_Course`) REFERENCES `courses` (`ID`) ON UPDATE CASCADE,
  CONSTRAINT `Registrations_courses->Students` FOREIGN KEY (`ID_Student`) REFERENCES `students` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registrations_courses`
--

LOCK TABLES `registrations_courses` WRITE;
/*!40000 ALTER TABLE `registrations_courses` DISABLE KEYS */;
/*!40000 ALTER TABLE `registrations_courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Surname` varchar(64) NOT NULL,
  `Name` varchar(64) NOT NULL,
  `Email` varchar(64) NOT NULL DEFAULT 'Non indicata',
  `Username` varchar(64) NOT NULL,
  `Password` varchar(64) NOT NULL,
  `ID_DegreeCourse` int NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Email_UNIQUE` (`Email`),
  UNIQUE KEY `Username_UNIQUE` (`Username`),
  KEY `Students->DegreeCourses_idx` (`ID_DegreeCourse`),
  CONSTRAINT `Students->DegreeCourses` FOREIGN KEY (`ID_DegreeCourse`) REFERENCES `degree_courses` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students_verbals`
--

DROP TABLE IF EXISTS `students_verbals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students_verbals` (
  `ID_Student` int NOT NULL,
  `ID_Verbal` int NOT NULL,
  PRIMARY KEY (`ID_Student`,`ID_Verbal`),
  KEY `students_verbals->Verbals_idx` (`ID_Verbal`),
  CONSTRAINT `students_verbals->Students` FOREIGN KEY (`ID_Student`) REFERENCES `students` (`ID`) ON UPDATE CASCADE,
  CONSTRAINT `students_verbals->Verbals` FOREIGN KEY (`ID_Verbal`) REFERENCES `verbals` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students_verbals`
--

LOCK TABLES `students_verbals` WRITE;
/*!40000 ALTER TABLE `students_verbals` DISABLE KEYS */;
/*!40000 ALTER TABLE `students_verbals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verbals`
--

DROP TABLE IF EXISTS `verbals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verbals` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `CreationDate` date NOT NULL,
  `CreationTime` time NOT NULL,
  `ID_Call` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `Verbals->Calls_idx` (`ID_Call`),
  CONSTRAINT `Verbals->Calls` FOREIGN KEY (`ID_Call`) REFERENCES `calls` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verbals`
--

LOCK TABLES `verbals` WRITE;
/*!40000 ALTER TABLE `verbals` DISABLE KEYS */;
/*!40000 ALTER TABLE `verbals` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-04-22 11:51:59
