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
  KEY `calls->courses_idx` (`ID_Course`),
  CONSTRAINT `calls->courses` FOREIGN KEY (`ID_Course`) REFERENCES `courses` (`ID`) ON UPDATE CASCADE
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
  `Description` varchar(1024) NOT NULL DEFAULT 'No description',
  `ID_Lecturer` int NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Name_UNIQUE` (`Name`),
  KEY `Courses->Lecturers_idx` (`ID_Lecturer`),
  CONSTRAINT `Courses->Lecturers` FOREIGN KEY (`ID_Lecturer`) REFERENCES `users` (`ID`) ON UPDATE CASCADE
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
  `Description` varchar(1024) NOT NULL DEFAULT 'No description',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Nome_UNIQUE` (`Name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `degree_courses`
--

LOCK TABLES `degree_courses` WRITE;
/*!40000 ALTER TABLE `degree_courses` DISABLE KEYS */;
INSERT INTO `degree_courses` VALUES (1,'Ingegneria Informatica','Gli sviluppi dell\'informatica, e in generale delle tecnologie dell’informazione, hanno avuto uno straordinario impatto sulla realtà produttiva, sociale ed economica degli ultimi anni. Queste discipline sono infatti divenute fattori determinanti della cultura e dell\'organizzazione delle moderne imprese e di molte attività sociali, stimolandone la trasformazione e l’innovazione. In questo scenario, in continua e velocissima evoluzione per la nascita di nuove tecnologie e l\'emergere di nuove esigenze, si colloca il corso di studi in Ingegneria Informatica che si propone di formare ingegneri dotati di un\'ampia e solida preparazione scientifica e tecnologica, capaci di sviluppare e utilizzare i metodi e gli strumenti dell\'informatica con sensibilità ingegneristica, per affrontare problematiche comuni a un amplissimo spettro di applicazioni. I profili professionali che il corso di studi in Ingegneria Informatica consente di costruire sono fra i più richiesti sul mercato del lavoro'),(2,'Ingegneria Elettrica','L\'Ingegneria Elettrica è quel ramo dell\'Ingegneria che si occupa delle applicazioni dei fenomeni elettrici attraverso lo studio dell\'elettromagnetismo e della teoria dei circuiti. Sono di interesse per questo settore i sistemi elettrici per l’energia, le macchine elettriche e il loro controllo, i convertitori elettronici di potenza, i sistemi elettrici per il trasporto, le misure elettriche ed elettroniche, la compatibilità elettromagnetica.\nQuesto Corso di Studio organizza e fornisce gli insegnamenti necessari per la formazione di laureati di primo e di secondo livello in Ingegneria Elettrica.'),(3,'Design della Comunicazione','Lo sviluppo rapido e continuo del sistema dei media, della rete, della comunicazione digitale e mobile, l’ampliamento dei servizi che presiedono alla loro produzione e gestione, l’aumento dei dispositivi e delle occasioni per comunicare e interagire, fanno della comunicazione un settore professionale complesso, in continua espansione e articolazione.\n\nIl sistema della comunicazione e dell\'informazione mostra una presenza generalizzata, una diffusione capillare, un assetto robusto e potente. L\'industria della comunicazione e dell\'informazione è uno degli assi portanti dello sviluppo sociale, economico e culturale nello scenario contemporaneo. Gli artefatti e i sistemi progettati dai designer della comunicazione sono una presenza costante e trasversale.'),(4,'Ingegneria Biomedica','Attualmente, molte delle aree più significative dello sviluppo scientifico e tecnologico ricadono nei settori delle scienze mediche e biologiche, con particolare riferimento alla salute, alla nutrizione, all’ambiente, alla terapia genica e alle biotecnologie. Tutti questi temi richiedono didattica di elevata qualità e innovazione, ricerca e supporto alle aziende.\n\nL’Ingegneria Biomedica utilizza le metodologie e le tecnologie dell’ingegneria per descrivere, comprendere e risolvere problemi di interesse medico-biologico tramite una stretta cooperazione interdisciplinare tra ingegneri, medici e biologi.'),(5,'Progettazione dell\'Architettura','Il Corso di Laurea in Progettazione dell’architettura ha come scopo la formazione fondamentale basata sulla conoscenza dell\'architettura nei suoi aspetti storici, formali, estetici, funzionali, costruttivi, tecnologici e di rappresentazione. Il progetto di architettura si riferisce a diversi ambiti e scale di applicazione: la città, il paesaggio, l\'edificio, l\'ambiente costruito, gli interni.\n\nIl Corso di Laurea ha come obiettivo la formazione di una professionalità capace di rispondere in modo adeguato alla crescente complessità dei problemi connessi alla progettazione dell\'architettura e ai nuovi compiti e responsabilità richiesti dal mondo professionale. Il laureato, attraverso una adeguata preparazione critica accompagnata da competenze tecniche definite, è in grado di svolgere le attività professionali previste dagli ordinamenti vigenti.');
/*!40000 ALTER TABLE `degree_courses` ENABLE KEYS */;
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
  `Mark` enum('','Assente','Rimandato','Riprovato','18','19','20','21','22','23','24','25','26','27','28','29','30','30L') NOT NULL,
  `EvaluationStatus` enum('Non inserito','Inserito','Pubblicato','Rifiutato','Verbalizzato') NOT NULL,
  PRIMARY KEY (`ID_Student`,`ID_Call`),
  KEY `Registrations_calls->Students_idx` (`ID_Student`),
  KEY `Registrations_calls->Calls_idx` (`ID_Call`),
  CONSTRAINT `Registrations_calls->Calls` FOREIGN KEY (`ID_Call`) REFERENCES `calls` (`ID`) ON UPDATE CASCADE,
  CONSTRAINT `Registrations_calls->Students` FOREIGN KEY (`ID_Student`) REFERENCES `users` (`ID`) ON UPDATE CASCADE
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
  KEY `Registrations_courses->Students_idx` (`ID_Student`),
  KEY `Registrations_courses->Courses_idx` (`ID_Course`),
  CONSTRAINT `Registrations_courses->Courses` FOREIGN KEY (`ID_Course`) REFERENCES `courses` (`ID`) ON UPDATE CASCADE,
  CONSTRAINT `Registrations_courses->Students` FOREIGN KEY (`ID_Student`) REFERENCES `users` (`ID`) ON UPDATE CASCADE
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
-- Table structure for table `students_verbals`
--

DROP TABLE IF EXISTS `students_verbals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students_verbals` (
  `ID_Student` int NOT NULL,
  `ID_Verbal` int NOT NULL,
  PRIMARY KEY (`ID_Student`,`ID_Verbal`),
  KEY `students_verbals->Students_idx` (`ID_Student`),
  KEY `students_verbals->Verbals_idx` (`ID_Verbal`),
  CONSTRAINT `students_verbals->Students` FOREIGN KEY (`ID_Student`) REFERENCES `users` (`ID`) ON UPDATE CASCADE,
  CONSTRAINT `students_verbals->verbals` FOREIGN KEY (`ID_Verbal`) REFERENCES `verbals` (`ID`) ON UPDATE CASCADE
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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `Surname` varchar(64) NOT NULL,
  `Name` varchar(64) NOT NULL,
  `Email` varchar(64) NOT NULL,
  `Username` varchar(64) NOT NULL,
  `Password` varchar(64) NOT NULL,
  `Role` varchar(64) NOT NULL,
  `ID_DegreeCourse` int DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Username_UNIQUE` (`Username`),
  KEY `Students->DegreeCourses_idx` (`ID_DegreeCourse`),
  CONSTRAINT `users->degreeCourses` FOREIGN KEY (`ID_DegreeCourse`) REFERENCES `degree_courses` (`ID`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Cugola','Gianpaolo','cugolagianpaolo@mail.com','lecturer1','lecturer1','Lecturer',NULL),(2,'Fraternali','Piero','fraternalipiero@mail.com','lecturer2','lecturer2','Lecturer',NULL),(3,'Martinenghi','Davide','martinenghidavide@mail.com','lecturer3','lecturer3','Lecturer',NULL),(4,'Rossi','Mario','rossimario@mail.com','student1','student1','Student',2),(5,'Geltrude','Amelia','geltrudeamelia@gmail.com','student2','student2','Student',4),(6,'Tosetti','Luca','tosettiluca@mail.com','student3','student3','Student',1),(7,'Doe','Jhon','doejhon@mail.com','student4','student4','Student',5);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
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

-- Dump completed on 2023-05-31 21:37:55
