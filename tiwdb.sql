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
CREATE DATABASE IF NOT EXISTS tiwproject;
USE tiwproject;

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
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calls`
--

LOCK TABLES `calls` WRITE;
/*!40000 ALTER TABLE `calls` DISABLE KEYS */;
INSERT INTO `calls` VALUES (1,'2023-05-01','15:00:00',1),(2,'2023-05-01','18:00:00',2),(3,'2023-05-05','11:00:00',3),(4,'2023-05-08','14:00:00',4),(5,'2023-05-10','13:30:00',5),(6,'2023-05-15','17:00:00',6),(7,'2023-04-29','08:30:00',7),(8,'2023-05-21','09:25:00',9),(9,'2023-06-02','08:30:00',10),(10,'2023-06-03','13:30:00',11),(11,'2023-06-05','11:20:00',1),(12,'2023-05-27','16:00:00',4),(13,'2023-05-31','12:00:00',3),(14,'2023-04-18','17:30:00',6),(15,'2023-02-06','13:00:00',10),(16,'2023-04-30','10:00:00',7),(17,'2023-05-17','16:30:00',11),(18,'2023-05-10','15:20:00',2),(19,'2023-05-22','18:15:00',1),(20,'2023-05-05','16:30:00',3),(21,'2023-05-26','09:15:00',5),(22,'2023-04-23','18:15:00',11),(23,'2023-01-24','14:00:00',11),(24,'2023-01-07','15:45:00',10),(25,'2023-02-01','11:00:00',11);
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'INGEGNERIA DEL SOFTWARE','Progettazione Del Software: Progettazione orientata agli oggetti. Architetture client-server e progettazione distribuita. Interazione tra componenti attraverso eventi e progettazione delle interfacce utente. ',1),(2,'PROVA FINALE (INGEGNERIA DEL SOFTWARE)','Lo scopo della prova finale è di realizzare un\'applicazione Java, sfruttando i principi della programmazione ad oggetti.',1),(3,'TECNOLOGIE INFORMATICHE PER IL WEB','Obiettivo del corso e l\'introduzione delle metodologie, tecniche e architetture per l\'analisi, progettazione e realizzazione di sistemi informativi fruibili via Web. Il corso affronta lo sviluppo delle applicazioni web da tre punti di vista: architetture, processo di sviluppo e tecniche di realizzazione.',2),(4,'DATA BASES 2','The course aims to prepare software designers on the effective development of database applications. First, the course presents the fundamental features of current database architectures, with a specific emphasis on the concept of transaction and its realization in centralized and distributed systems.',2),(5,'ALGORITMI E PRINCIPI DELL\'INFORMATICA','I modelli dell`informatica. Automi (a stati finiti, a pila, Macchine di Turing); Grammatiche; Modelli nondeterministici; reti di Petrii; Uso della logica matematica per modellare sistemi descriverne proprieta`. Teoria della computazione. Potenza dei modelli di calcolo;',3),(6,'RETI LOGICHE','Il corso ha lo scopo di condurre gli studenti al progetto logico dei sistemi digitali, introducendone problemi e metodologie risolutive. A questo fine, si affrontera\' il problema della sintesi logica e della sua ottimizzazione, partendo dai sistemi piu\' semplici - quelli combinatori - per passare ai piu\' complessi sistemi sequenziali.',1),(7,'ECONOMIA','Impresa: obiettivi, forme, proprieta\' e controllo. Contabilita\' esterna: funzione, contenuto e riclassificazione dei documenti di bilancio; analisi di liquidita\' e redditivita\'. Decisioni di lungo periodo (analisi degli investimenti): costo del capitale, flussi di cassa, Valore Attuale Netto, altri criteri di valutazione.',2),(9,'BASI DI DATI 1','Il corso si pone come obiettivo primario l`acquisizione da parte degli studenti di due abilita` di base: saper progettare basi di dati e saper estrarre informazioni da esse.',1),(10,'FONDAMENTI DI AUTOMATICA','Introduzione ai problemi di controllo. Sistemi fisici e modelli matematici. Sistemi orientati (sistemi dinamici e non dinamici, tempo-invarianti e tempo-varianti, lineari e non lineari; variabili di stato e forma normale, condizioni di equilibrio, modello lineare tangente a un sistema in un punto di lavoro).',1),(11,'BIOINFORMATICS ALGORITHMS','La Bioinformatica rappresenta un sempre più importante e interessante settore di applicazione dell\'informatica. Questa disciplina è nata dalla crescente necessità nell\'ambito della Biologia Molecolare di sviluppare adeguati strumenti computazionali per la soluzione di molteplici problemi, tra cui quelli derivanti dall\'analisi di sequenze biologiche',1),(12,'MECCANICA (PER ING. INFORMATICA)','La prima parte del corso e` dedicata a un rapido riepilogo del calcolo vettoriale; della cinematica del punto e del corpo rigido nel piano. Particolare riguardo e` dato ai moti relativi. Sono proposti metodi risolutivi quali il metodo grafico e la notazione complessa.',1);
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
INSERT INTO `registrations_calls` VALUES (4,1,'','Non inserito'),(4,2,'','Non inserito'),(4,11,'','Non inserito'),(4,18,'','Non inserito'),(4,19,'','Non inserito'),(5,2,'','Non inserito'),(5,4,'','Non inserito'),(5,8,'','Non inserito'),(5,12,'','Non inserito'),(5,18,'','Non inserito'),(6,9,'','Non inserito'),(6,10,'','Non inserito'),(6,15,'','Non inserito'),(6,17,'','Non inserito'),(6,24,'','Non inserito'),(6,25,'','Non inserito'),(7,14,'','Non inserito');
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
INSERT INTO `registrations_courses` VALUES (4,1),(4,2),(4,3),(4,11),(5,2),(5,4),(5,9),(6,10),(6,11),(7,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Cugola','Gianpaolo','cugolagianpaolo@mail.com','lecturer1','lecturer1','Lecturer',NULL),(2,'Fraternali','Piero','fraternalipiero@mail.com','lecturer2','lecturer2','Lecturer',NULL),(3,'Martinenghi','Davide','martinenghidavide@mail.com','lecturer3','lecturer3','Lecturer',NULL),(4,'Rossi','Mario','rossimario@mail.com','student1','student1','Student',2),(5,'Geltrude','Amelia','geltrudeamelia@gmail.com','student2','student2','Student',4),(6,'Tosetti','Luca','tosettiluca@mail.com','student3','student3','Student',1),(7,'Doe','Jhon','doejhon@mail.com','student4','student4','Student',5),(8,'George','Farrell','g.farrell@mail.com','student5','student5','Student',1),(9,'Roman','Johnston','r.jhonston@mail.com','student6','student6','Student',1),(10,'Leonardo','Carroll','l.carrol@mail.com','student7','student7','Student',1),(11,'Reid','Ryan','r.ryan@mail.com','student8','student8','Student',2),(12,'Sarah','Reed','s.reed@mail.com','student9','student9','Student',2),(13,'Brad','Martin','b.martin@mail.com','student10','student10','Student',3),(14,'Oscar','Scott','o.scott@mail.com','student11','student11','Student',3),(15,'Oscar','Morris','o.morris@mail.com','student12','student12','Student',4),(16,'Max','Gibson','m.gibson@mail.com','student13','student13','Student',4),(17,'Miranda','Perkins','m.perkins@mail.com','student14','student14','Student',5),(18,'Dexter','Martin','d.martin@mail.com','student15','student15','Student',5),(19,'Spike','Spencer','s.spencer@mail.com','student16','student16','Student',1),(20,'Oliver','Jhonson','o.jhonsoj@mail.com','student17','student17','Student',2),(21,'Kimberly','Casey','k.casey@mail.com','student18','student18','Student',5),(22,'Jack','Crawford','j.crawford@mail.com','student19','student19','Student',3);
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

-- Dump completed on 2023-06-02 22:51:23
