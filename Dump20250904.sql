-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ghostnet
-- ------------------------------------------------------
-- Server version	9.4.0

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
-- Table structure for table `nets`
--

DROP TABLE IF EXISTS `nets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `location_lat` double NOT NULL,
  `location_long` double NOT NULL,
  `net_id` bigint NOT NULL,
  `size` enum('L','M','S','XL') NOT NULL,
  `state` enum('LOST','RECOVERED','RECOVERY_PENDING','REPORTED') NOT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlsyskp0yfdqmx7sd6gvkwinpy` (`net_id`),
  KEY `FK4ccjjbpbi8avx4fnb57xs3pyq` (`user_id`),
  CONSTRAINT `FK4ccjjbpbi8avx4fnb57xs3pyq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nets`
--

LOCK TABLES `nets` WRITE;
/*!40000 ALTER TABLE `nets` DISABLE KEYS */;
INSERT INTO `nets` VALUES (1,22.6842,55.5556,7135175023618510821,'L','REPORTED',NULL),(2,-2.5681,122.6578,-4574317537408694928,'S','RECOVERED',NULL),(3,55.6891,-80.521,-5946655227414230948,'XL','REPORTED',NULL),(4,8.6811,-51.6845,7135175063618516821,'M','RECOVERY_PENDING',3),(15,30.6361,153.2679,615877549867224595,'L','REPORTED',NULL),(16,-68.9571,92.7061,753398782508091004,'XL','REPORTED',NULL),(17,53.2421,-28.0211,7182397839151549291,'L','LOST',NULL),(18,-34.357,-32.2138,6164626600732278812,'S','REPORTED',NULL),(19,-48.4252,-124.4156,748001407766971816,'M','RECOVERY_PENDING',4),(20,75.1213,43.6746,9967492507715273,'XL','REPORTED',NULL),(21,31.9302,-71.0239,70289435439155751,'XL','RECOVERY_PENDING',3),(22,1.1707,-105.2548,999360679760970722,'S','RECOVERY_PENDING',2),(23,-45.5491,90.4121,6966186506831548269,'M','REPORTED',NULL),(24,64.218,-59.0764,1015340779179167701,'M','REPORTED',NULL);
/*!40000 ALTER TABLE `nets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role` enum('ADMIN','RECOVERER','STANDARD') DEFAULT NULL,
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,'STANDARD'),(2,'STANDARD'),(2,'RECOVERER'),(3,'STANDARD'),(3,'RECOVERER'),(4,'STANDARD'),(4,'RECOVERER');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6efs5vmce86ymf5q7lmvn2uuf` (`user_id`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,_binary '','$2a$10$45PojfG72Z6kcWvfPHDvC.LZJXppKc2pJbPQ2J3podAhlhjhYjS82',NULL,1,'standard'),(2,_binary '','$2a$10$45PojfG72Z6kcWvfPHDvC.LZJXppKc2pJbPQ2J3podAhlhjhYjS82','+4900000000000',2,'recoverer'),(3,_binary '','$2a$10$45PojfG72Z6kcWvfPHDvC.LZJXppKc2pJbPQ2J3podAhlhjhYjS82','+4900000000000',3,'recoverer2'),(4,_binary '','$2a$10$45PojfG72Z6kcWvfPHDvC.LZJXppKc2pJbPQ2J3podAhlhjhYjS82','+4900000000000',4,'recoverer3');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'ghostnet'
--

--
-- Dumping routines for database 'ghostnet'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-04 16:27:43
