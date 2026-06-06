-- MySQL dump 10.13  Distrib 9.2.0, for Win64 (x86_64)
--
-- Host: localhost    Database: furniture_db
-- ------------------------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `address_id` int NOT NULL AUTO_INCREMENT,
  `address_line` varchar(255) DEFAULT NULL,
  `city` varchar(50) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `district` varchar(50) NOT NULL,
  `is_default` bit(1) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `recipient_name` varchar(100) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  `ward` varchar(50) NOT NULL,
  PRIMARY KEY (`address_id`),
  KEY `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id`),
  CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'100/1chưoounh duong','Vietnam','2026-04-02 01:34:38.473647','District',_binary '\0','0334074017','an nguyen','2026-04-02 01:34:38.473647',5,'Ward'),(2,'100/1chưoounh duong','Vietnam','2026-04-02 02:34:09.940971','District',_binary '\0','0334074017','an nguyen','2026-04-02 02:34:09.940971',5,'Ward'),(3,'á','Vietnam','2026-05-05 08:32:34.243312','District',_binary '\0','0334074018','Nguyen An','2026-05-15 13:42:18.199735',6,'Ward'),(4,'á','Vietnam','2026-05-05 08:45:46.497826','District',_binary '\0','0334074018','Nguyen An','2026-05-15 14:25:58.244943',6,'Ward'),(5,'a','Vietnam','2026-05-05 14:02:10.648291','District',_binary '\0','0334074018','Nguyen An','2026-05-05 14:02:10.648291',6,'Ward'),(6,'a','Vietnam','2026-05-05 14:11:31.469387','District',_binary '\0','0334074018','Nguyen An','2026-05-05 14:11:31.469387',6,'Ward'),(7,'a','Vietnam','2026-05-06 04:07:54.817236','District',_binary '\0','0334074018','Nguyen An','2026-05-06 04:07:54.817236',6,'Ward'),(8,'a','Vietnam','2026-05-06 06:07:12.612752','District',_binary '\0','0334074018','Nguyen An','2026-05-06 06:07:12.612752',6,'Ward'),(9,'a','Vietnam','2026-05-06 06:10:27.222333','District',_binary '\0','0334074018','Nguyen An','2026-05-06 06:10:27.222333',6,'Ward'),(10,'a','Vietnam','2026-05-06 06:12:17.472703','District',_binary '\0','0334074018','Nguyen An','2026-05-06 06:12:17.472703',6,'Ward'),(11,'a','Vietnam','2026-05-06 06:24:17.036740','District',_binary '\0','0334074018','Nguyen An','2026-05-06 06:24:17.036740',6,'Ward'),(12,'a','Vietnam','2026-05-12 10:07:37.383212','District',_binary '\0','0334074018','Nguyen An','2026-05-12 10:07:37.383212',6,'Ward'),(13,'a','Vietnam','2026-05-12 10:38:41.803162','District',_binary '\0','0334074018','Nguyen An','2026-05-12 10:38:41.803162',6,'Ward'),(14,'a','Vietnam','2026-05-14 17:03:44.032925','District',_binary '\0','0334074018','Nguyen An','2026-05-14 17:03:44.032925',6,'Ward'),(15,'a, Ward, District, Vietnam','Vietnam','2026-05-15 14:25:09.585623','District',_binary '\0','0334074018','Nguyen An','2026-05-15 14:25:09.585623',6,'Ward'),(16,'á, Ward, District, Vietnam','Vietnam','2026-05-15 14:44:51.515531','District',_binary '\0','0334074018','Nguyen An','2026-05-15 14:44:51.515531',6,'Ward'),(17,'á, Ward, District, Vietnam','Vietnam','2026-05-16 01:36:57.949091','District',_binary '\0','0334074018','Nguyen An','2026-05-16 01:36:57.949091',6,'Ward'),(18,'hay','Tỉnh Hà Giang','2026-05-16 02:50:39.188338','Huyện Đồng Văn',_binary '\0','02020202','Nguyen An','2026-05-16 02:50:39.188338',6,'Xã Thài Phìn Tủng'),(19,'hay, Xã Thài Phìn Tủng, Huyện Đồng Văn, Tỉnh Hà Giang','Vietnam','2026-05-16 02:50:55.171675','District',_binary '\0','02020202','Nguyen An','2026-05-16 02:50:55.171675',6,'Ward'),(20,'á, Ward, District, Vietnam','Vietnam','2026-05-16 03:25:17.752421','District',_binary '\0','0334074018','Nguyen An','2026-05-16 03:25:17.752421',6,'Ward'),(21,'á, Ward, District, Vietnam','Vietnam','2026-05-16 04:23:39.783388','District',_binary '\0','0334074018','Nguyen An','2026-05-16 04:23:39.783388',6,'Ward'),(22,'a, Ward, District, Vietnam','Vietnam','2026-05-16 06:53:56.398967','District',_binary '\0','0334074018','Nguyen An','2026-05-16 06:53:56.398967',6,'Ward'),(23,'á, Ward, District, Vietnam','Vietnam','2026-05-16 06:54:09.264459','District',_binary '\0','0334074018','Nguyen An','2026-05-16 06:54:09.264459',6,'Ward'),(24,'2','Thành phố Hà Nội','2026-05-16 07:10:53.554505','Quận Hoàn Kiếm',_binary '\0','2','Nguyen An','2026-05-16 07:10:53.554505',6,'Phường Cửa Đông'),(25,'','Thành phố Hồ Chí Minh','2026-05-16 07:12:17.715907','Quận Bình Thạnh',_binary '\0','0','Nguyen An','2026-05-16 07:12:17.715907',6,'Phường 26'),(26,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-16 07:13:16.956610','District',_binary '\0','0','Nguyen An','2026-05-16 07:13:16.956610',6,'Ward'),(27,'Duong so 4','Thành phố Hà Nội','2026-05-16 09:59:46.762518','Quận Ba Đình',_binary '','0334074016','nghia le','2026-05-16 09:59:46.762518',12,'Phường Ngọc Hà'),(28,'Duong so 4, Phường Ngọc Hà, Quận Ba Đình, Thành phố Hà Nội','Vietnam','2026-05-16 09:59:53.520021','District',_binary '\0','0334074016','nghia le','2026-05-16 09:59:53.520021',12,'Ward'),(29,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-17 02:14:03.336314','District',_binary '\0','0','Nguyen An','2026-05-17 02:14:03.336314',6,'Ward'),(30,'hay, Xã Thài Phìn Tủng, Huyện Đồng Văn, Tỉnh Hà Giang','Vietnam','2026-05-17 02:17:56.925992','District',_binary '\0','02020202','Nguyen An','2026-05-17 02:17:56.925992',6,'Ward'),(31,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-17 02:18:37.767922','District',_binary '\0','0','Nguyen An','2026-05-17 02:18:37.767922',6,'Ward'),(32,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-17 02:22:07.053671','District',_binary '\0','0','Nguyen An','2026-05-17 02:22:07.053671',6,'Ward'),(33,'á, Ward, District, Vietnam','Vietnam','2026-05-17 11:00:16.674415','District',_binary '\0','0334074018','Nguyen An','2026-05-17 11:00:16.674415',6,'Ward'),(34,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-17 11:00:50.089736','District',_binary '\0','0','Nguyen An','2026-05-17 11:00:50.089736',6,'Ward'),(35,'á, Ward, District, Vietnam','Vietnam','2026-05-17 13:49:39.523506','District',_binary '\0','0334074018','Nguyen An','2026-05-17 13:49:39.523506',6,'Ward'),(36,'a, Ward, District, Vietnam','Vietnam','2026-05-17 14:35:09.545460','District',_binary '\0','0334074018','Nguyen An','2026-05-17 14:35:09.545460',6,'Ward'),(37,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-17 15:00:01.766283','District',_binary '\0','0','Nguyen An','2026-05-17 15:00:01.766283',6,'Ward'),(38,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 04:03:13.696237','District',_binary '\0','0','Nguyen An','2026-05-21 04:03:13.696237',6,'Ward'),(39,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 05:04:31.688714','District',_binary '\0','0','Nguyen An','2026-05-21 05:04:31.688714',6,'Ward'),(40,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 05:09:31.872368','District',_binary '\0','0','Nguyen An','2026-05-21 05:09:31.872368',6,'Ward'),(41,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 05:15:30.420640','District',_binary '\0','0','Nguyen An','2026-05-21 05:15:30.420640',6,'Ward'),(42,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 06:17:39.899929','District',_binary '\0','0','Nguyen An','2026-05-21 06:17:39.899929',6,'Ward'),(43,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 07:24:27.928068','District',_binary '\0','0','Nguyen An','2026-05-21 07:24:27.928068',6,'Ward'),(44,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 07:26:49.680965','District',_binary '\0','0','Nguyen An','2026-05-21 07:26:49.680965',6,'Ward'),(45,', Phường 26, Quận Bình Thạnh, Thành phố Hồ Chí Minh','Vietnam','2026-05-21 07:27:02.038409','District',_binary '\0','0','Nguyen An','2026-05-21 07:27:02.038409',6,'Ward'),(47,'Bsbsb','Tỉnh Bạc Liêu','2026-05-21 07:28:02.440963','Huyện Hoà Bình',_binary '','0334074016','Nguyen An','2026-05-21 07:28:02.440963',6,'Xã Vĩnh Hậu'),(48,'Bsbsb, Xã Vĩnh Hậu, Huyện Hoà Bình, Tỉnh Bạc Liêu','Vietnam','2026-05-21 07:28:03.739106','District',_binary '\0','0334074016','Nguyen An','2026-05-21 07:28:03.739106',6,'Ward'),(49,'Bsbsb, Xã Vĩnh Hậu, Huyện Hoà Bình, Tỉnh Bạc Liêu','Vietnam','2026-05-21 07:40:08.539005','District',_binary '\0','0334074016','Nguyen An','2026-05-21 07:40:08.539005',6,'Ward'),(50,'Bsbsb, Xã Vĩnh Hậu, Huyện Hoà Bình, Tỉnh Bạc Liêu','Vietnam','2026-05-21 07:46:55.426554','District',_binary '\0','0334074016','Nguyen An','2026-05-21 07:46:55.426554',6,'Ward'),(51,'100/19','Tỉnh An Giang','2026-06-06 04:57:43.621641','Huyện An Phú',_binary '','0334074016','Khách Hàng','2026-06-06 04:57:43.621641',10,'Xã Phú Hữu'),(52,'100/19, Xã Phú Hữu, Huyện An Phú, Tỉnh An Giang','Vietnam','2026-06-06 04:57:46.807282','District',_binary '\0','0334074016','Khách Hàng','2026-06-06 04:57:46.807282',10,'Ward');
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `cart_item_id` int NOT NULL AUTO_INCREMENT,
  `cart_id` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `product_id` int NOT NULL,
  `product_variant_id` int DEFAULT NULL,
  `quantity` int NOT NULL,
  `shop_id` int NOT NULL,
  `total_price` decimal(19,2) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `variant_info` text,
  PRIMARY KEY (`cart_item_id`),
  KEY `FKpcttvuq4mxppo8sxggjtn5i2c` (`cart_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  KEY `FK3f6l7u09wl1xfglv4453mkrw8` (`shop_id`),
  KEY `FKn1s4l7h0vm4o259wpu7ft0y2y` (`product_variant_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FK3f6l7u09wl1xfglv4453mkrw8` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`shop_id`),
  CONSTRAINT `FKn1s4l7h0vm4o259wpu7ft0y2y` FOREIGN KEY (`product_variant_id`) REFERENCES `product_variants` (`variant_id`),
  CONSTRAINT `FKpcttvuq4mxppo8sxggjtn5i2c` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (1,1,'2026-04-02 01:36:15.089490',12990000.00,1,1,8,1,103920000.00,'2026-04-02 02:40:18.116598','Beige - 3 chỗ - Vải'),(16,4,'2026-05-16 09:58:45.931597',12990000.00,1,1,1,1,12990000.00,'2026-05-16 09:58:45.931597','Beige - 3 chỗ - Vải'),(17,4,'2026-05-16 10:01:03.319914',2590000.00,4,3,1,1,2590000.00,'2026-05-16 10:01:03.319914','Nâu - 80cm - Gỗ walnut');
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `cart_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `total_price` decimal(15,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`cart_id`),
  KEY `FKb5o626f86h46m4s7ms6ginnop` (`user_id`),
  CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,'2026-04-02 01:34:48.565110',103920000.00,'2026-04-02 02:40:18.154764',5),(2,'2026-05-05 08:32:04.034843',0.00,'2026-05-21 07:28:04.785425',6),(3,'2026-05-06 06:35:01.808024',0.00,'2026-05-06 06:35:01.808024',9),(4,'2026-05-16 09:58:22.224695',15580000.00,'2026-05-16 10:01:03.327480',12);
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`category_id`),
  KEY `FKsaok720gsu4u2wrgbk10b5n8d` (`parent_id`),
  CONSTRAINT `FKsaok720gsu4u2wrgbk10b5n8d` FOREIGN KEY (`parent_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Sofa & Armchair','2026-01-20 11:19:29.290315','Ghế sofa và ghế bành','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400',NULL,'2026-01-20 11:19:29.290315'),(2,'Bàn trà / Coffee Table','2026-01-20 11:19:29.290315','Bàn trà phòng khách','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=400',NULL,'2026-01-20 11:19:29.290315'),(3,'Kệ TV / TV Console','2026-01-20 11:19:29.290315','Kệ tivi các loại','https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=400',NULL,'2026-01-20 11:19:29.290315'),(4,'Bàn ăn / Dining Table','2026-01-20 11:19:29.290315','Bàn ăn gia đình','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=400',NULL,'2026-01-20 11:19:29.290315'),(5,'Ghế ăn / Dining Chair','2026-01-20 11:19:29.290315','Ghế ăn các loại','https://images.unsplash.com/photo-1503602642458-232111445657?w=400',NULL,'2026-01-20 11:19:29.290315'),(6,'Giường ngủ / Bed','2026-01-20 11:19:29.290315','Giường ngủ các kích thước','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=400',NULL,'2026-01-20 11:19:29.290315'),(7,'Tủ áo / Wardrobe','2026-01-20 11:19:29.290315','Tủ quần áo','https://images.unsplash.com/photo-1558997519-83ea9252edf8?w=400',NULL,'2026-01-20 11:19:29.290315'),(8,'Táp đầu giường / Nightstand','2026-01-20 11:19:29.290315','Tủ đầu giường','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=400',NULL,'2026-01-20 11:19:29.290315'),(9,'Kệ sách / Bookcase','2026-01-20 11:19:29.290315','Kệ sách các loại','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=400',NULL,'2026-01-20 11:19:29.290315'),(10,'Đèn / Lighting','2026-01-20 11:19:29.290315','Đèn trang trí','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400',NULL,'2026-01-20 11:19:29.290315'),(11,'Gương / Mirror','2026-01-20 11:19:29.290315','Gương trang trí','https://images.unsplash.com/photo-1618220179428-22790b461013?w=400',NULL,'2026-01-20 11:19:29.290315'),(12,'Bàn làm việc / Desk','2026-01-20 11:19:29.290315','Bàn làm việc văn phòng','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400',NULL,'2026-01-20 11:19:29.290315'),(13,'Ghế văn phòng / Office Chair','2026-01-20 11:19:29.290315','Ghế văn phòng công thái học','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=400',NULL,'2026-01-20 11:19:29.290315'),(14,'Thảm / Rug','2026-01-20 11:19:29.290315','Thảm trải sàn','https://images.unsplash.com/photo-1600166898405-da9535204843?w=400',NULL,'2026-01-20 11:19:29.290315'),(15,'Ngoài trời / Outdoor','2026-01-20 11:19:29.290315','Nội thất ngoài trời','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=400',NULL,'2026-01-20 11:19:29.290315');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_messages`
--

DROP TABLE IF EXISTS `chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `chat_id` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `message` text NOT NULL,
  `receiver_id` int NOT NULL,
  `receiver_type` enum('USER','SHOP') NOT NULL,
  `sender_id` int NOT NULL,
  `sender_type` enum('USER','SHOP') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `media_public_id` varchar(255) DEFAULT NULL,
  `media_url` varchar(1000) DEFAULT NULL,
  `message_type` enum('TEXT','IMAGE') NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_messages`
--

LOCK TABLES `chat_messages` WRITE;
/*!40000 ALTER TABLE `chat_messages` DISABLE KEYS */;
INSERT INTO `chat_messages` VALUES (1,'6-1','2026-05-14 17:07:05.348522',_binary '','a',1,'SHOP',6,'USER','2026-05-14 17:07:05.348522',NULL,NULL,'TEXT'),(2,'6-1','2026-05-14 17:07:13.395673',_binary '','chao\nho tro toi dc ko',1,'SHOP',6,'USER','2026-05-14 17:07:13.395673',NULL,NULL,'TEXT'),(3,'6-1','2026-05-14 17:07:16.616765',_binary '','alo',1,'SHOP',6,'USER','2026-05-14 17:07:16.616765',NULL,NULL,'TEXT'),(4,'6-1','2026-05-14 17:08:52.234293',_binary '','đc cứu',6,'USER',1,'SHOP','2026-05-14 17:08:52.234293',NULL,NULL,'TEXT'),(5,'11-1','2026-05-14 17:25:35.377323',_binary '','chao shop',1,'SHOP',11,'USER','2026-05-14 17:25:35.377323',NULL,NULL,'TEXT'),(6,'6-1','2026-05-16 01:37:22.760350',_binary '','heppo',1,'SHOP',6,'USER','2026-05-16 01:37:22.760350',NULL,NULL,'TEXT'),(7,'6-1','2026-05-16 07:29:34.852228',_binary '','rrrrrr',1,'SHOP',6,'USER','2026-05-16 07:29:34.852228',NULL,NULL,'TEXT'),(8,'6-1','2026-05-21 06:15:28.069301',_binary '','jhh',1,'SHOP',6,'USER','2026-05-21 06:15:28.069301',NULL,NULL,'TEXT'),(9,'6-1','2026-05-21 06:15:29.075797',_binary '','',1,'SHOP',6,'USER','2026-05-21 06:15:29.075797','furniture/chat/mt8awqreu3goh9fttfh8','https://res.cloudinary.com/dyxotav3k/image/upload/v1779344128/furniture/chat/mt8awqreu3goh9fttfh8.jpg','IMAGE'),(10,'6-1','2026-05-21 07:11:37.324390',_binary '','nhan cai dau buoi',6,'USER',1,'SHOP','2026-05-21 07:11:37.324390',NULL,NULL,'TEXT'),(11,'6-1','2026-05-21 07:11:50.155845',_binary '','g vcl',6,'USER',1,'SHOP','2026-05-21 07:11:50.155845',NULL,NULL,'TEXT'),(12,'6-1','2026-05-21 07:11:57.168075',_binary '','sao thichs gif noi luoon',1,'SHOP',6,'USER','2026-05-21 07:11:57.168075',NULL,NULL,'TEXT'),(13,'6-1','2026-05-21 08:09:48.274034',_binary '\0','solo ko',1,'SHOP',6,'USER','2026-05-21 08:09:48.274034',NULL,NULL,'TEXT');
/*!40000 ALTER TABLE `chat_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coupons`
--

DROP TABLE IF EXISTS `coupons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coupons` (
  `coupon_id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(20) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `discount_percent` decimal(5,2) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `max_discount_amount` decimal(10,2) DEFAULT NULL,
  `min_order_value` decimal(10,2) DEFAULT NULL,
  `shop_id` int DEFAULT NULL,
  `start_date` datetime(6) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`coupon_id`),
  UNIQUE KEY `UK_eplt0kkm9yf2of2lnx6c1oy9b` (`code`),
  KEY `FKdwk5xko4gfgul9hv8q13o56bx` (`shop_id`),
  CONSTRAINT `FKdwk5xko4gfgul9hv8q13o56bx` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coupons`
--

LOCK TABLES `coupons` WRITE;
/*!40000 ALTER TABLE `coupons` DISABLE KEYS */;
/*!40000 ALTER TABLE `coupons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_read` bit(1) DEFAULT NULL,
  `message` text NOT NULL,
  `title` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `order_item_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `discount` decimal(5,2) DEFAULT NULL,
  `price` decimal(15,2) NOT NULL,
  `product_id` int NOT NULL,
  `quantity` int NOT NULL,
  `sub_order_id` int NOT NULL,
  `total` decimal(15,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `variant_id` int DEFAULT NULL,
  `variant_info` text,
  PRIMARY KEY (`order_item_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  KEY `FK5dv8t9ns7l4he5eqdos8idf4x` (`sub_order_id`),
  KEY `FKemq71edpbn9wsxnxncfn1algp` (`variant_id`),
  CONSTRAINT `FK5dv8t9ns7l4he5eqdos8idf4x` FOREIGN KEY (`sub_order_id`) REFERENCES `sub_orders` (`sub_order_id`),
  CONSTRAINT `FKemq71edpbn9wsxnxncfn1algp` FOREIGN KEY (`variant_id`) REFERENCES `product_variants` (`variant_id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,'2026-04-02 01:34:38.616286',0.00,12990000.00,1,1,1,12990000.00,'2026-04-02 01:34:38.616286',1,'Beige - 3 chỗ - Vải'),(2,'2026-04-02 02:34:10.028542',0.00,12990000.00,1,1,2,12990000.00,'2026-04-02 02:34:10.028542',1,'Beige - 3 chỗ - Vải'),(3,'2026-05-05 08:32:34.314493',0.00,2590000.00,4,1,3,2590000.00,'2026-05-05 08:32:34.314493',3,'Nâu - 80cm - Gỗ walnut'),(4,'2026-05-05 08:45:46.589394',0.00,12990000.00,1,1,4,12990000.00,'2026-05-05 08:45:46.589394',1,'Beige - 3 chỗ - Vải'),(5,'2026-05-05 14:02:10.710538',0.00,12990000.00,1,1,5,12990000.00,'2026-05-05 14:02:10.710538',1,'Beige - 3 chỗ - Vải'),(6,'2026-05-05 14:11:31.551197',0.00,12990000.00,1,1,6,12990000.00,'2026-05-05 14:11:31.551197',1,'Beige - 3 chỗ - Vải'),(7,'2026-05-06 04:07:54.915746',0.00,2590000.00,4,2,7,5180000.00,'2026-05-06 04:07:54.915746',3,'Nâu - 80cm - Gỗ walnut'),(8,'2026-05-06 06:07:12.652021',0.00,12990000.00,1,1,8,12990000.00,'2026-05-06 06:07:12.652021',1,'Beige - 3 chỗ - Vải'),(9,'2026-05-06 06:10:27.240516',0.00,12990000.00,1,1,9,12990000.00,'2026-05-06 06:10:27.240516',1,'Beige - 3 chỗ - Vải'),(10,'2026-05-06 06:12:17.496103',0.00,2590000.00,4,2,10,5180000.00,'2026-05-06 06:12:17.496103',3,'Nâu - 80cm - Gỗ walnut'),(11,'2026-05-06 06:24:17.079950',0.00,2590000.00,4,1,11,2590000.00,'2026-05-06 06:24:17.079950',3,'Nâu - 80cm - Gỗ walnut'),(12,'2026-05-12 10:07:37.417487',0.00,2590000.00,4,1,12,2590000.00,'2026-05-12 10:07:37.417487',3,'Nâu - 80cm - Gỗ walnut'),(13,'2026-05-12 10:38:41.839892',0.00,2590000.00,4,1,13,2590000.00,'2026-05-12 10:38:41.839892',3,'Nâu - 80cm - Gỗ walnut'),(14,'2026-05-14 17:03:44.080353',0.00,2590000.00,4,1,14,2590000.00,'2026-05-14 17:03:44.080353',3,'Nâu - 80cm - Gỗ walnut'),(15,'2026-05-15 14:25:09.662484',0.00,12990000.00,1,1,15,12990000.00,'2026-05-15 14:25:09.662484',1,'Beige - 3 chỗ - Vải'),(16,'2026-05-15 14:44:51.549948',0.00,12990000.00,1,1,16,12990000.00,'2026-05-15 14:44:51.549948',1,'Beige - 3 chỗ - Vải'),(17,'2026-05-16 01:36:58.018616',0.00,2590000.00,4,1,17,2590000.00,'2026-05-16 01:36:58.018616',3,'Nâu - 80cm - Gỗ walnut'),(18,'2026-05-16 02:50:55.198592',0.00,12990000.00,1,1,18,12990000.00,'2026-05-16 02:50:55.198592',1,'Beige - 3 chỗ - Vải'),(19,'2026-05-16 03:25:17.771100',0.00,12990000.00,1,1,19,12990000.00,'2026-05-16 03:25:17.771100',1,'Beige - 3 chỗ - Vải'),(20,'2026-05-16 04:23:39.797891',0.00,12990000.00,1,1,20,12990000.00,'2026-05-16 04:23:39.797891',1,'Beige - 3 chỗ - Vải'),(21,'2026-05-16 06:53:56.432435',0.00,12990000.00,1,1,21,12990000.00,'2026-05-16 06:53:56.432435',2,'Xám - 3 chỗ - Vải'),(22,'2026-05-16 06:54:09.269967',0.00,2590000.00,4,1,22,2590000.00,'2026-05-16 06:54:09.269967',3,'Nâu - 80cm - Gỗ walnut'),(23,'2026-05-16 07:13:16.981568',0.00,2590000.00,4,1,23,2590000.00,'2026-05-16 07:13:16.981568',3,'Nâu - 80cm - Gỗ walnut'),(24,'2026-05-16 09:59:53.550243',0.00,12990000.00,1,1,24,12990000.00,'2026-05-16 09:59:53.550243',1,'Beige - 3 chỗ - Vải'),(25,'2026-05-17 02:14:03.378635',0.00,16990000.00,11,1,25,16990000.00,'2026-05-17 02:14:03.378635',8,'Walnut - Queen 1m6 - Gỗ walnut'),(26,'2026-05-17 02:17:56.949807',0.00,2590000.00,4,1,26,2590000.00,'2026-05-17 02:17:56.949807',3,'Nâu - 80cm - Gỗ walnut'),(27,'2026-05-17 02:17:56.952801',0.00,16990000.00,11,1,26,16990000.00,'2026-05-17 02:17:56.952801',8,'Walnut - Queen 1m6 - Gỗ walnut'),(28,'2026-05-17 02:18:37.778034',0.00,12990000.00,1,1,27,12990000.00,'2026-05-17 02:18:37.778034',1,'Beige - 3 chỗ - Vải'),(29,'2026-05-17 02:22:07.064083',0.00,12990000.00,1,1,28,12990000.00,'2026-05-17 02:22:07.064083',1,'Beige - 3 chỗ - Vải'),(30,'2026-05-17 11:00:16.743599',0.00,6990000.00,7,1,29,6990000.00,'2026-05-17 11:00:16.743599',5,'Gỗ tự nhiên - 120cm - Gỗ sồi'),(31,'2026-05-17 11:00:50.109207',0.00,12990000.00,1,1,30,12990000.00,'2026-05-17 11:00:50.109207',1,'Beige - 3 chỗ - Vải'),(32,'2026-05-17 13:49:39.629268',0.00,12990000.00,1,1,31,12990000.00,'2026-05-17 13:49:39.629268',1,'Beige - 3 chỗ - Vải'),(33,'2026-05-17 14:35:09.615790',0.00,12990000.00,1,1,32,12990000.00,'2026-05-17 14:35:09.615790',1,'Beige - 3 chỗ - Vải'),(34,'2026-05-17 15:00:01.838549',0.00,12990000.00,1,20,33,259800000.00,'2026-05-17 15:00:01.838549',1,'Beige - 3 chỗ - Vải'),(35,'2026-05-21 04:03:13.739189',0.00,1.00,1,1,34,1.00,'2026-05-21 04:03:13.739189',11,'a - a - a'),(36,'2026-05-21 05:04:31.869400',0.00,12990000.00,1,1,35,12990000.00,'2026-05-21 05:04:31.869400',1,'Beige - 3 chỗ - Vải'),(37,'2026-05-21 05:09:31.892248',0.00,12990000.00,1,1,36,12990000.00,'2026-05-21 05:09:31.892248',1,'Beige - 3 chỗ - Vải'),(38,'2026-05-21 05:15:30.489812',0.00,12990000.00,1,1,37,12990000.00,'2026-05-21 05:15:30.489812',1,'Beige - 3 chỗ - Vải'),(39,'2026-05-21 06:17:39.917933',0.00,12990000.00,1,19,38,246810000.00,'2026-05-21 06:17:39.917933',1,'Beige - 3 chỗ - Vải'),(40,'2026-05-21 07:24:27.946970',0.00,1000.00,1,1,39,1000.00,'2026-05-21 07:24:27.946970',12,'den - s - goo'),(41,'2026-05-21 07:24:27.951971',0.00,12990000.00,1,2,39,25980000.00,'2026-05-21 07:24:27.951971',2,'Xám - 3 chỗ - Vải'),(42,'2026-05-21 07:26:49.693745',0.00,12990000.00,1,1,40,12990000.00,'2026-05-21 07:26:49.693745',2,'Xám - 3 chỗ - Vải'),(43,'2026-05-21 07:27:02.048321',0.00,12990000.00,1,1,41,12990000.00,'2026-05-21 07:27:02.048321',2,'Xám - 3 chỗ - Vải'),(44,'2026-05-21 07:28:03.762026',0.00,2590000.00,4,1,42,2590000.00,'2026-05-21 07:28:03.762026',3,'Nâu - 80cm - Gỗ walnut'),(45,'2026-05-21 07:40:08.589430',0.00,2590000.00,4,1,43,2590000.00,'2026-05-21 07:40:08.589430',3,'Nâu - 80cm - Gỗ walnut'),(46,'2026-05-21 07:46:55.475343',0.00,12990000.00,1,1,44,12990000.00,'2026-05-21 07:46:55.475343',2,'Xám - 3 chỗ - Vải'),(47,'2026-06-06 04:57:46.864681',0.00,2590000.00,4,1,45,2590000.00,'2026-06-06 04:57:46.864681',3,'Nâu - 80cm - Gỗ walnut');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `payment_method` enum('COD','MOMO','VNPAY','BANK_TRANSFER') NOT NULL,
  `payment_status` enum('PENDING','PAID','FAILED','REFUNDED','CANCELLED') NOT NULL,
  `shipping_address_id` int NOT NULL,
  `shipping_fee` decimal(15,2) NOT NULL,
  `status` enum('PENDING','PROCESSING','SHIPPED','DELIVERED','CANCELLED') NOT NULL,
  `total_price` decimal(15,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`order_id`),
  KEY `FKmk6q95x8ffidq82wlqjaq7sqc` (`shipping_address_id`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKmk6q95x8ffidq82wlqjaq7sqc` FOREIGN KEY (`shipping_address_id`) REFERENCES `addresses` (`address_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'2026-04-02 01:34:38.558842','','COD','CANCELLED',1,30000.00,'CANCELLED',13020000.00,'2026-04-02 01:35:00.122571',5),(2,'2026-04-02 02:34:10.001601','','COD','PENDING',2,30000.00,'PENDING',13020000.00,'2026-04-02 02:34:10.001601',5),(3,'2026-05-05 08:32:34.275368','','COD','PENDING',3,30000.00,'PENDING',2620000.00,'2026-05-05 08:32:34.275368',6),(4,'2026-05-05 08:45:46.559406','','COD','CANCELLED',4,30000.00,'CANCELLED',13020000.00,'2026-05-05 08:45:52.319634',6),(5,'2026-05-05 14:02:10.678420','','COD','PENDING',5,30000.00,'PENDING',13020000.00,'2026-05-05 14:02:10.678420',6),(6,'2026-05-05 14:11:31.516326','','COD','CANCELLED',6,30000.00,'CANCELLED',13020000.00,'2026-05-05 14:26:57.792092',6),(7,'2026-05-06 04:07:54.867387','','COD','PENDING',7,30000.00,'PENDING',5210000.00,'2026-05-06 04:07:54.867387',6),(8,'2026-05-06 06:07:12.632479','','COD','PENDING',8,30000.00,'PENDING',13020000.00,'2026-05-06 06:07:12.632479',6),(9,'2026-05-06 06:10:27.231024','','COD','PENDING',9,30000.00,'PENDING',13020000.00,'2026-05-06 06:10:27.231024',6),(10,'2026-05-06 06:12:17.488822','','COD','PENDING',10,30000.00,'PENDING',5210000.00,'2026-05-06 06:12:17.488822',6),(11,'2026-05-06 06:24:17.057229','','COD','PENDING',11,30000.00,'SHIPPED',2620000.00,'2026-05-06 06:40:00.271621',6),(12,'2026-05-12 10:07:37.405333','','COD','CANCELLED',12,30000.00,'CANCELLED',2620000.00,'2026-05-12 12:37:29.789927',6),(13,'2026-05-12 10:38:41.825740','','COD','CANCELLED',13,30000.00,'CANCELLED',2620000.00,'2026-05-12 12:37:28.408520',6),(14,'2026-05-14 17:03:44.061881','','COD','PENDING',14,30000.00,'PENDING',2620000.00,'2026-05-14 17:03:44.061881',6),(15,'2026-05-15 14:25:09.637976','','COD','PENDING',15,30000.00,'DELIVERED',13020000.00,'2026-05-15 14:28:30.701851',6),(16,'2026-05-15 14:44:51.529944','','COD','PENDING',16,30000.00,'DELIVERED',13020000.00,'2026-05-16 01:40:57.514238',6),(17,'2026-05-16 01:36:57.996041','','COD','PENDING',17,30000.00,'DELIVERED',2620000.00,'2026-05-16 01:40:52.111607',6),(18,'2026-05-16 02:50:55.177214','','COD','PENDING',19,30000.00,'PENDING',13020000.00,'2026-05-16 02:50:55.177214',6),(19,'2026-05-16 03:25:17.763963','','COD','PENDING',20,30000.00,'PENDING',13020000.00,'2026-05-16 03:25:17.763963',6),(20,'2026-05-16 04:23:39.790478','','COD','PENDING',21,30000.00,'PENDING',13020000.00,'2026-05-16 04:23:39.790478',6),(21,'2026-05-16 06:53:56.414265','','COD','CANCELLED',22,30000.00,'CANCELLED',13020000.00,'2026-05-16 07:19:20.143559',6),(22,'2026-05-16 06:54:09.267465','','COD','PENDING',23,30000.00,'PENDING',2620000.00,'2026-05-16 06:54:09.267465',6),(23,'2026-05-16 07:13:16.965136','','COD','PENDING',26,30000.00,'PENDING',2620000.00,'2026-05-16 07:13:16.965136',6),(24,'2026-05-16 09:59:53.526552','','COD','PENDING',28,30000.00,'DELIVERED',13020000.00,'2026-05-16 10:05:40.564363',12),(25,'2026-05-17 02:14:03.351248','','COD','CANCELLED',29,30000.00,'CANCELLED',17020000.00,'2026-05-17 02:14:37.356848',6),(26,'2026-05-17 02:17:56.943265','','COD','CANCELLED',30,30000.00,'CANCELLED',19610000.00,'2026-05-17 02:18:04.792876',6),(27,'2026-05-17 02:18:37.774002','','COD','PENDING',31,30000.00,'DELIVERED',13020000.00,'2026-05-17 02:19:08.826014',6),(28,'2026-05-17 02:22:07.059679','','COD','PENDING',32,30000.00,'PENDING',13020000.00,'2026-05-17 02:22:07.059679',6),(29,'2026-05-17 11:00:16.715448','','COD','CANCELLED',33,30000.00,'CANCELLED',7020000.00,'2026-05-17 11:00:33.753257',6),(30,'2026-05-17 11:00:50.099542','','COD','PENDING',34,30000.00,'DELIVERED',13020000.00,'2026-05-17 11:01:17.619230',6),(31,'2026-05-17 13:49:39.567026','','COD','PENDING',35,30000.00,'DELIVERED',13020000.00,'2026-05-17 13:50:27.894585',6),(32,'2026-05-17 14:35:09.590327','','COD','PENDING',36,30000.00,'PENDING',13020000.00,'2026-05-17 14:35:09.590327',6),(33,'2026-05-17 15:00:01.812310','','COD','PENDING',37,30000.00,'DELIVERED',259830000.00,'2026-05-17 15:00:32.385202',6),(34,'2026-05-21 04:03:13.717277','','COD','PENDING',38,30000.00,'PENDING',30001.00,'2026-05-21 04:03:13.717277',6),(35,'2026-05-21 05:04:31.832497','','COD','CANCELLED',39,30000.00,'CANCELLED',13020000.00,'2026-05-21 06:05:27.543399',6),(36,'2026-05-21 05:09:31.885239','','COD','CANCELLED',40,30000.00,'CANCELLED',13020000.00,'2026-05-21 06:05:26.376963',6),(37,'2026-05-21 05:15:30.473151','','COD','PENDING',41,0.00,'DELIVERED',12990000.00,'2026-05-21 05:16:54.000013',6),(38,'2026-05-21 06:17:39.909556','g','COD','PENDING',42,0.00,'PENDING',246810000.00,'2026-05-21 06:17:39.909556',6),(39,'2026-05-21 07:24:27.935577','','COD','PENDING',43,0.00,'DELIVERED',25981000.00,'2026-05-21 07:25:29.280741',6),(40,'2026-05-21 07:26:49.689027','','COD','CANCELLED',44,0.00,'CANCELLED',12990000.00,'2026-05-21 07:26:54.265970',6),(41,'2026-05-21 07:27:02.044651','','COD','CANCELLED',45,0.00,'CANCELLED',12990000.00,'2026-05-21 07:40:00.337222',6),(42,'2026-05-21 07:28:03.760025','','COD','PENDING',48,0.00,'DELIVERED',2590000.00,'2026-05-21 07:28:52.433545',6),(43,'2026-05-21 07:40:08.569377','','COD','PENDING',49,0.00,'DELIVERED',2590000.00,'2026-05-21 07:40:22.087282',6),(44,'2026-05-21 07:46:55.449046','','COD','PENDING',50,0.00,'DELIVERED',12990000.00,'2026-05-21 07:47:06.040151',6),(45,'2026-06-06 04:57:46.830040','','COD','PENDING',52,0.00,'DELIVERED',2590000.00,'2026-06-06 05:13:52.077967',10);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `order_id` int NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `payment_method` enum('COD','MOMO','VNPAY','BANK_TRANSFER') NOT NULL,
  `status` enum('PENDING','PAID','FAILED','REFUNDED') DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `UK_lryndveuwa4k5qthti0pkmtlx` (`transaction_id`),
  KEY `FK81gagumt0r8y3rmudcgpbk42l` (`order_id`),
  CONSTRAINT `FK81gagumt0r8y3rmudcgpbk42l` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_reviews`
--

DROP TABLE IF EXISTS `product_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_at` datetime(6) DEFAULT NULL,
  `images` varchar(255) DEFAULT NULL,
  `is_verified` bit(1) DEFAULT NULL,
  `product_id` int NOT NULL,
  `rating` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  `order_id` int DEFAULT NULL,
  PRIMARY KEY (`review_id`),
  KEY `FK35kxxqe2g9r4mww80w9e3tnw9` (`product_id`),
  KEY `FK58i39bhws2hss3tbcvdmrm60f` (`user_id`),
  CONSTRAINT `FK35kxxqe2g9r4mww80w9e3tnw9` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FK58i39bhws2hss3tbcvdmrm60f` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_reviews`
--

LOCK TABLES `product_reviews` WRITE;
/*!40000 ALTER TABLE `product_reviews` DISABLE KEYS */;
INSERT INTO `product_reviews` VALUES (1,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-02 09:10:00.000000','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80',b'1',1,5,'2026-06-02 09:10:00.000000',6,NULL),(2,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-03 09:17:00.000000','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80',b'1',1,4,'2026-06-03 09:17:00.000000',10,NULL),(3,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-04 09:24:00.000000','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80',b'1',1,5,'2026-06-04 09:24:00.000000',11,NULL),(4,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-03 09:10:00.000000','https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&fit=crop&q=80',b'1',2,5,'2026-06-03 09:10:00.000000',6,NULL),(5,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-04 09:17:00.000000','https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&fit=crop&q=80',b'1',2,4,'2026-06-04 09:17:00.000000',10,NULL),(6,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-05 09:24:00.000000','https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&fit=crop&q=80',b'1',2,5,'2026-06-05 09:24:00.000000',11,NULL),(7,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-04 09:10:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',3,5,'2026-06-04 09:10:00.000000',6,NULL),(8,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-05 09:17:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',3,4,'2026-06-05 09:17:00.000000',10,NULL),(9,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-06 09:24:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',3,5,'2026-06-06 09:24:00.000000',11,NULL),(10,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-05 09:10:00.000000','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80',b'1',4,5,'2026-06-05 09:10:00.000000',6,NULL),(11,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-06 09:17:00.000000','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80',b'1',4,4,'2026-06-06 09:17:00.000000',10,NULL),(12,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-01 09:24:00.000000','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80',b'1',4,5,'2026-06-01 09:24:00.000000',11,NULL),(13,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-06 09:10:00.000000','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80',b'1',5,5,'2026-06-06 09:10:00.000000',6,NULL),(14,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-01 09:17:00.000000','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80',b'1',5,4,'2026-06-01 09:17:00.000000',10,NULL),(15,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-02 09:24:00.000000','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80',b'1',5,5,'2026-06-02 09:24:00.000000',11,NULL),(16,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-01 09:10:00.000000','https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80',b'1',6,5,'2026-06-01 09:10:00.000000',6,NULL),(17,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-02 09:17:00.000000','https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80',b'1',6,4,'2026-06-02 09:17:00.000000',10,NULL),(18,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-03 09:24:00.000000','https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80',b'1',6,5,'2026-06-03 09:24:00.000000',11,NULL),(19,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-02 09:10:00.000000','https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80',b'1',7,5,'2026-06-02 09:10:00.000000',6,NULL),(20,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-03 09:17:00.000000','https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80',b'1',7,4,'2026-06-03 09:17:00.000000',10,NULL),(21,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-04 09:24:00.000000','https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80',b'1',7,5,'2026-06-04 09:24:00.000000',11,NULL),(22,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-03 09:10:00.000000','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80',b'1',8,5,'2026-06-03 09:10:00.000000',6,NULL),(23,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-04 09:17:00.000000','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80',b'1',8,4,'2026-06-04 09:17:00.000000',10,NULL),(24,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-05 09:24:00.000000','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80',b'1',8,5,'2026-06-05 09:24:00.000000',11,NULL),(25,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-04 09:10:00.000000','https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80',b'1',9,5,'2026-06-04 09:10:00.000000',6,NULL),(26,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-05 09:17:00.000000','https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80',b'1',9,4,'2026-06-05 09:17:00.000000',10,NULL),(27,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-06 09:24:00.000000','https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80',b'1',9,5,'2026-06-06 09:24:00.000000',11,NULL),(28,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-05 09:10:00.000000','https://images.unsplash.com/photo-1559599189-fe84dea4eb79?w=800&fit=crop&q=80',b'1',10,5,'2026-06-05 09:10:00.000000',6,NULL),(29,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-06 09:17:00.000000','https://images.unsplash.com/photo-1559599189-fe84dea4eb79?w=800&fit=crop&q=80',b'1',10,4,'2026-06-06 09:17:00.000000',10,NULL),(30,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-01 09:24:00.000000','https://images.unsplash.com/photo-1559599189-fe84dea4eb79?w=800&fit=crop&q=80',b'1',10,5,'2026-06-01 09:24:00.000000',11,NULL),(31,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-06 09:10:00.000000','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80',b'1',11,5,'2026-06-06 09:10:00.000000',6,NULL),(32,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-01 09:17:00.000000','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80',b'1',11,4,'2026-06-01 09:17:00.000000',10,NULL),(33,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-02 09:24:00.000000','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80',b'1',11,5,'2026-06-02 09:24:00.000000',11,NULL),(34,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-01 09:10:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',12,5,'2026-06-01 09:10:00.000000',6,NULL),(35,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-02 09:17:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',12,4,'2026-06-02 09:17:00.000000',10,NULL),(36,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-03 09:24:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',12,5,'2026-06-03 09:24:00.000000',11,NULL),(37,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-02 09:10:00.000000','https://images.unsplash.com/photo-1631679706909-1bb2c20a2d6a?w=800&fit=crop&q=80',b'1',13,5,'2026-06-02 09:10:00.000000',6,NULL),(38,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-03 09:17:00.000000','https://images.unsplash.com/photo-1631679706909-1bb2c20a2d6a?w=800&fit=crop&q=80',b'1',13,4,'2026-06-03 09:17:00.000000',10,NULL),(39,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-04 09:24:00.000000','https://images.unsplash.com/photo-1631679706909-1bb2c20a2d6a?w=800&fit=crop&q=80',b'1',13,5,'2026-06-04 09:24:00.000000',11,NULL),(40,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-03 09:10:00.000000','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80',b'1',14,5,'2026-06-03 09:10:00.000000',6,NULL),(41,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-04 09:17:00.000000','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80',b'1',14,4,'2026-06-04 09:17:00.000000',10,NULL),(42,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-05 09:24:00.000000','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80',b'1',14,5,'2026-06-05 09:24:00.000000',11,NULL),(43,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-04 09:10:00.000000','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80',b'1',15,5,'2026-06-04 09:10:00.000000',6,NULL),(44,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-05 09:17:00.000000','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80',b'1',15,4,'2026-06-05 09:17:00.000000',10,NULL),(45,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-06 09:24:00.000000','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80',b'1',15,5,'2026-06-06 09:24:00.000000',11,NULL),(46,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-05 09:10:00.000000','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80',b'1',16,5,'2026-06-05 09:10:00.000000',6,NULL),(47,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-06 09:17:00.000000','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80',b'1',16,4,'2026-06-06 09:17:00.000000',10,NULL),(48,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-01 09:24:00.000000','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80',b'1',16,5,'2026-06-01 09:24:00.000000',11,NULL),(49,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-06 09:10:00.000000','https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=800&fit=crop&q=80',b'1',17,5,'2026-06-06 09:10:00.000000',6,NULL),(50,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-01 09:17:00.000000','https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=800&fit=crop&q=80',b'1',17,4,'2026-06-01 09:17:00.000000',10,NULL),(51,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-02 09:24:00.000000','https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=800&fit=crop&q=80',b'1',17,5,'2026-06-02 09:24:00.000000',11,NULL),(52,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-01 09:10:00.000000','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80',b'1',18,5,'2026-06-01 09:10:00.000000',6,NULL),(53,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-02 09:17:00.000000','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80',b'1',18,4,'2026-06-02 09:17:00.000000',10,NULL),(54,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-03 09:24:00.000000','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80',b'1',18,5,'2026-06-03 09:24:00.000000',11,NULL),(55,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-02 09:10:00.000000','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80',b'1',19,5,'2026-06-02 09:10:00.000000',6,NULL),(56,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-03 09:17:00.000000','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80',b'1',19,4,'2026-06-03 09:17:00.000000',10,NULL),(57,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-04 09:24:00.000000','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80',b'1',19,5,'2026-06-04 09:24:00.000000',11,NULL),(58,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-03 09:10:00.000000','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800&fit=crop&q=80',b'1',20,5,'2026-06-03 09:10:00.000000',6,NULL),(59,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-04 09:17:00.000000','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800&fit=crop&q=80',b'1',20,4,'2026-06-04 09:17:00.000000',10,NULL),(60,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-05 09:24:00.000000','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800&fit=crop&q=80',b'1',20,5,'2026-06-05 09:24:00.000000',11,NULL),(61,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-04 09:10:00.000000','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80',b'1',21,5,'2026-06-04 09:10:00.000000',6,NULL),(62,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-05 09:17:00.000000','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80',b'1',21,4,'2026-06-05 09:17:00.000000',10,NULL),(63,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-06 09:24:00.000000','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80',b'1',21,5,'2026-06-06 09:24:00.000000',11,NULL),(64,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-05 09:10:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',22,5,'2026-06-05 09:10:00.000000',6,NULL),(65,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-06 09:17:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',22,4,'2026-06-06 09:17:00.000000',10,NULL),(66,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-01 09:24:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',22,5,'2026-06-01 09:24:00.000000',11,NULL),(67,'San pham dung mo ta, mau sac ngoai thuc te dep va de phoi voi khong gian nha.','2026-06-06 09:10:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',23,5,'2026-06-06 09:10:00.000000',6,NULL),(68,'Dong goi ky, giao hang on, chat lieu nhin chac chan hon mong doi.','2026-06-01 09:17:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',23,4,'2026-06-01 09:17:00.000000',10,NULL),(69,'Minh dung vai ngay thay rat hai long, anh chup thuc te giong hinh tren app.','2026-06-02 09:24:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',23,5,'2026-06-02 09:24:00.000000',11,NULL);
/*!40000 ALTER TABLE `product_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_variants`
--

DROP TABLE IF EXISTS `product_variants`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_variants` (
  `variant_id` int NOT NULL AUTO_INCREMENT,
  `color` varchar(50) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `material` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `processor` varchar(100) DEFAULT NULL,
  `product_id` int NOT NULL,
  `ram` int DEFAULT NULL,
  `size` varchar(50) DEFAULT NULL,
  `stock` int NOT NULL,
  `storage` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `weight` decimal(6,2) DEFAULT NULL,
  PRIMARY KEY (`variant_id`),
  KEY `FKosqitn4s405cynmhb87lkvuau` (`product_id`),
  CONSTRAINT `FKosqitn4s405cynmhb87lkvuau` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_variants`
--

LOCK TABLES `product_variants` WRITE;
/*!40000 ALTER TABLE `product_variants` DISABLE KEYS */;
INSERT INTO `product_variants` VALUES (1,'Beige','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80','Vai boc cao cap + Khung go so',12990000.00,NULL,1,NULL,'3 cho (220x90x85cm)',8,NULL,'2026-05-21 06:17:39.922818',45.00),(2,'Xam tro','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=800&fit=crop&q=80','Vai boc cao cap + Khung go so',12990000.00,NULL,1,NULL,'3 cho (220x90x85cm)',12,NULL,'2026-05-21 07:46:55.494206',45.00),(3,'Nau walnut','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80','Go oc cho nguyen tam',2590000.00,NULL,4,NULL,'D80 x C45cm',30,NULL,'2026-06-06 04:57:46.924382',14.00),(4,'Den ebony','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1595428773296-63e47b9b7a28?w=800&fit=crop&q=80','Go oc cho son den',2790000.00,NULL,4,NULL,'D80 x C45cm',27,NULL,'2026-01-20 11:19:29.309659',14.00),(5,'Go tu nhien','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80','Go so nguyen khoi',6990000.00,NULL,7,NULL,'D120 x C75cm',10,NULL,'2026-01-20 11:19:29.309659',34.00),(6,'Go tu nhien','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80','Go so nguyen khoi',8490000.00,NULL,7,NULL,'D150 x C75cm',8,NULL,'2026-01-20 11:19:29.309659',38.00),(7,'Oak sang','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80','Go so my nguyen khoi',15990000.00,NULL,11,NULL,'Queen 160x200cm',12,NULL,'2026-01-20 11:19:29.309659',52.00),(8,'Walnut dam','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&fit=crop&q=80','Go oc cho my nguyen khoi',17490000.00,NULL,11,NULL,'Queen 160x200cm',8,NULL,'2026-01-20 11:19:29.309659',52.00),(9,'Xanh cong vit','2026-05-21 03:47:16.730055','https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&fit=crop&q=80','Nhung velvet + Khung go so',3990000.00,NULL,2,NULL,'1 cho (80x75x90cm)',15,NULL,'2026-05-21 03:47:16.730055',NULL),(11,'Vang nhat','2026-05-21 04:02:12.088866','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80','Vai boc cao cap + Khung go so',11990000.00,NULL,1,NULL,'3 cho (220x90x85cm)',0,NULL,'2026-05-21 04:03:13.756017',NULL),(12,'Xanh dust','2026-05-21 06:01:05.921169','https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=800&fit=crop&q=80','Vai boc cao cap + Khung go so',12490000.00,NULL,1,NULL,'3 cho (220x90x85cm)',0,NULL,'2026-05-21 07:24:27.956985',NULL),(13,'Xam',NULL,'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80','Vai ni premium + Khung go so',18500000.00,NULL,3,NULL,'Goc L (280x160x85cm)',10,NULL,NULL,NULL),(14,'Xanh navy',NULL,'https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=800&fit=crop&q=80','Vai ni premium + Khung go so',19500000.00,NULL,3,NULL,'Goc L (280x160x85cm)',10,NULL,NULL,NULL),(15,'Vang dong',NULL,'https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80','Kinh cuong luc 10mm + Khung thep ma vang',4200000.00,NULL,5,NULL,'D70 x C45cm',20,NULL,NULL,NULL),(16,'Bac Inox',NULL,'https://images.unsplash.com/photo-1488282637010-f054d0d02db6?w=800&fit=crop&q=80','Kinh cuong luc 10mm + Khung Inox',3800000.00,NULL,5,NULL,'D70 x C45cm',20,NULL,NULL,NULL),(17,'Trang sua',NULL,'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80','Go thong tu nhien',3800000.00,NULL,6,NULL,'180x35x50cm',20,NULL,NULL,NULL),(18,'Walnut',NULL,'https://images.unsplash.com/photo-1615874959474-d609969a20ed?w=800&fit=crop&q=80','Go oc cho MDF',4400000.00,NULL,6,NULL,'180x35x50cm',20,NULL,NULL,NULL),(19,'Nau tu nhien',NULL,'https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80','Go cao su nguyen khoi',15900000.00,NULL,8,NULL,'160x90x75cm + 6 ghe',12,NULL,NULL,NULL),(20,'Trang sua',NULL,'https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80','Go cao su son trang',16900000.00,NULL,8,NULL,'160x90x75cm + 6 ghe',13,NULL,NULL,NULL),(21,'Tu nhien',NULL,'https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80','Van ep uon cong + Chan go so',1450000.00,NULL,9,NULL,'45x52x80cm (1 ghe)',35,NULL,NULL,NULL),(22,'Den',NULL,'https://images.unsplash.com/photo-1581539250439-c96689b516dd?w=800&fit=crop&q=80','Van ep uon cong + Chan go so',1450000.00,NULL,9,NULL,'45x52x80cm (1 ghe)',35,NULL,NULL,NULL),(23,'Xam nhat',NULL,'https://images.unsplash.com/photo-1559599189-fe84dea4eb79?w=800&fit=crop&q=80','Nem vai + Chan go so',1490000.00,NULL,10,NULL,'48x55x82cm',40,NULL,NULL,NULL),(24,'Beige',NULL,'https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80','Nem vai + Chan go so',1490000.00,NULL,10,NULL,'48x55x82cm',40,NULL,NULL,NULL),(25,'Den huyen',NULL,'https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&fit=crop&q=80','Da PU cao cap + Khung go',14900000.00,NULL,12,NULL,'King 180x200cm',8,NULL,NULL,NULL),(26,'Nau dat',NULL,'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80','Da PU cao cap + Khung go',15900000.00,NULL,12,NULL,'King 180x200cm',7,NULL,NULL,NULL),(27,'Walnut',NULL,'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800&fit=crop&q=80','MDF phu Melamine van go',9800000.00,NULL,13,NULL,'200x60x220cm',5,NULL,NULL,NULL),(28,'Trang',NULL,'https://images.unsplash.com/photo-1631679706909-1bb2c20a2d6a?w=800&fit=crop&q=80','MDF phu Melamine trang',8900000.00,NULL,13,NULL,'200x60x220cm',5,NULL,NULL,NULL),(29,'Trang',NULL,'https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80','MDF son trang',1290000.00,NULL,14,NULL,'50x40x55cm',45,NULL,NULL,NULL),(30,'Walnut',NULL,'https://images.unsplash.com/photo-1616627052149-0a6b0b2d7c6e?w=800&fit=crop&q=80','MDF phu van go',1490000.00,NULL,14,NULL,'50x40x55cm',45,NULL,NULL,NULL),(31,'Den / Go oc cho',NULL,'https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80','Khung thep + Van go',2490000.00,NULL,15,NULL,'80x30x180cm',20,NULL,NULL,NULL),(32,'Trang / Go thong',NULL,'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&fit=crop&q=80','Khung thep trang + Van go',2290000.00,NULL,15,NULL,'80x30x180cm',20,NULL,NULL,NULL),(33,'Vang dong',NULL,'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80','Kim loai ma vang + Chao vai',2800000.00,NULL,16,NULL,'Cao 165cm',15,NULL,NULL,NULL),(34,'Den nham',NULL,'https://images.unsplash.com/photo-1513506003901-1e6a35d5e3a7?w=800&fit=crop&q=80','Kim loai son den + Chao vai',2600000.00,NULL,16,NULL,'Cao 165cm',15,NULL,NULL,NULL),(35,'Trang nga',NULL,'https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=800&fit=crop&q=80','Gom su + Chao vai linen',890000.00,NULL,17,NULL,'D20 x C38cm',30,NULL,NULL,NULL),(36,'Xanh sage',NULL,'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80','Gom su + Chao vai linen',990000.00,NULL,17,NULL,'D20 x C38cm',30,NULL,NULL,NULL),(37,'Den',NULL,'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80','Khung kim loai son den',2200000.00,NULL,18,NULL,'60x160cm',10,NULL,NULL,NULL),(38,'Vang dong',NULL,'https://images.unsplash.com/photo-1606170033648-5d55a3edf314?w=800&fit=crop&q=80','Khung kim loai ma vang',2500000.00,NULL,18,NULL,'60x160cm',10,NULL,NULL,NULL),(39,'Trang / Chan den',NULL,'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80','MDF + Khung thep',3490000.00,NULL,19,NULL,'120x60x75cm',15,NULL,NULL,NULL),(40,'Walnut / Chan den',NULL,'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800&fit=crop&q=80','MDF van go + Khung thep',3790000.00,NULL,19,NULL,'120x60x75cm',15,NULL,NULL,NULL),(41,'Den',NULL,'https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800&fit=crop&q=80','Luoi thoang khi + Khung nhom',5990000.00,NULL,20,NULL,'65x65x110-125cm',12,NULL,NULL,NULL),(42,'Xam',NULL,'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&fit=crop&q=80','Luoi thoang khi + Khung nhom',5990000.00,NULL,20,NULL,'65x65x110-125cm',13,NULL,NULL,NULL),(43,'Beige / Kem',NULL,'https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80','Soi polyester cao cap',1890000.00,NULL,21,NULL,'160x230cm',22,NULL,NULL,NULL),(44,'Xam / Trang',NULL,'https://images.unsplash.com/photo-1576185850227-1f72b7f8d483?w=800&fit=crop&q=80','Soi polyester cao cap',1890000.00,NULL,21,NULL,'160x230cm',23,NULL,NULL,NULL),(45,'Nau tu nhien',NULL,'https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80','May nhua PE + Khung nhom',8900000.00,NULL,22,NULL,'Ban D120 + 4 ghe',6,NULL,NULL,NULL),(46,'Xam trang',NULL,'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80','May nhua PE + Khung nhom',9200000.00,NULL,22,NULL,'Ban D120 + 4 ghe',6,NULL,NULL,NULL),(47,'Go tu nhien',NULL,'https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80','Go teak nguyen khoi',4500000.00,NULL,23,NULL,'180x60x90cm',5,NULL,NULL,NULL),(48,'Xam bac',NULL,'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80','Go teak + Dem ngoai troi',5200000.00,NULL,23,NULL,'180x60x90cm',5,NULL,NULL,NULL);
/*!40000 ALTER TABLE `product_variants` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` int NOT NULL AUTO_INCREMENT,
  `average_rating` decimal(3,2) DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `dimensions` varchar(50) DEFAULT NULL,
  `discount` decimal(5,2) DEFAULT NULL,
  `product_name` varchar(255) NOT NULL,
  `review_count` int DEFAULT NULL,
  `shop_id` int NOT NULL,
  `sold` int DEFAULT NULL,
  `status` enum('PENDING','ACTIVE','INACTIVE','PROCESSED') NOT NULL,
  `stock` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `weight` decimal(6,2) DEFAULT NULL,
  PRIMARY KEY (`product_id`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  KEY `FK7kp8sbhxboponhx3lxqtmkcoj` (`shop_id`),
  CONSTRAINT `FK7kp8sbhxboponhx3lxqtmkcoj` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`shop_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,4.60,1,'2026-01-20 11:19:29.306199','Sofa bọc vải màu trung tính, đệm ngồi êm, khung gỗ sồi tự nhiên, phù hợp phòng khách hiện đại','220 x 88 x 80 cm',10.00,'Sofa vải 3 chỗ Oakwood',128,1,320,'ACTIVE',10,'2026-05-21 07:46:55.490608',45.00),(2,4.40,1,'2026-01-20 11:19:29.306199','Armchair bọc nhung cao cấp, tựa lưng cao thoải mái, chân kim loại mạ vàng sang trọng','78 x 85 x 100 cm',12.00,'Ghế thư giãn Armchair Velvet',56,1,140,'ACTIVE',50,'2026-01-20 11:19:29.306199',16.00),(3,4.50,1,'2026-01-20 11:19:29.306199','Sofa vải chống bám bụi, khung gỗ tự nhiên chắc chắn, thiết kế hiện đại','250 x 160 x 90 cm',8.00,'Sofa góc chữ L Urban Grey',89,1,200,'ACTIVE',20,'2026-01-20 11:19:29.306199',48.00),(4,4.30,2,'2026-01-20 11:19:29.306199','Mặt gỗ veneer walnut chống trầy xước, chân gỗ đặc, thiết kế tối giản Bắc Âu','Ø80 x 40 cm',8.00,'Bàn trà tròn 80cm Walnut',75,1,210,'ACTIVE',57,'2026-06-06 04:57:46.924382',14.00),(5,4.30,2,'2026-01-20 11:19:29.306199','Mặt kính cường lực 8mm, khung thép phủ titan vàng, phong cách luxury','100 x 50 x 45 cm',6.00,'Bàn nước mặt kính khung vàng',42,1,150,'ACTIVE',40,'2026-01-20 11:19:29.306199',18.00),(6,4.50,3,'2026-01-20 11:19:29.306199','Thiết kế tối giản, 2 ngăn kéo + 1 khoang mở, gỗ công nghiệp cao cấp','180 x 40 x 45 cm',7.00,'Kệ TV 180cm phong cách Bắc Âu',38,1,95,'ACTIVE',40,'2026-01-20 11:19:29.306199',28.00),(7,4.70,4,'2026-01-20 11:19:29.306199','Gỗ sồi nguyên khối, phủ dầu lau an toàn thực phẩm, cho 4-6 người','Ø120 x 75 cm',9.00,'Bàn ăn tròn 120cm Solid Oak',62,1,120,'ACTIVE',18,'2026-01-20 11:19:29.306199',34.00),(8,4.40,4,'2026-01-20 11:19:29.306199','Gỗ cao su đã xử lý chống mối mọt, thiết kế hiện đại, bền đẹp','160 x 80 x 75 cm',10.00,'Bàn ăn 6 ghế gỗ cao su',85,1,180,'ACTIVE',25,'2026-01-20 11:19:29.306199',42.00),(9,4.20,5,'2026-01-20 11:19:29.306199','Ghế form công thái học, bề mặt veneer bền đẹp, thoải mái khi ngồi lâu','47 x 50 x 82 cm',6.00,'Bộ 2 ghế ăn uốn cong Plywood',95,1,260,'ACTIVE',70,'2026-01-20 11:19:29.306199',10.00),(10,4.30,5,'2026-01-20 11:19:29.306199','Khung gỗ sồi, nệm vải bố êm ái, phong cách Scandinavian','45 x 50 x 85 cm',7.00,'Ghế ăn bọc nệm Nordic',72,1,260,'ACTIVE',80,'2026-01-20 11:19:29.306199',7.00),(11,4.60,6,'2026-01-20 11:19:29.306199','Khung gỗ sồi, vạt phản chắc chắn, đầu giường bọc vải cao cấp','206 x 166 x 105 cm',11.00,'Giường ngủ Queen 1m6 Nordica',145,1,180,'ACTIVE',20,'2026-01-20 11:19:29.306199',52.00),(12,4.40,6,'2026-01-20 11:19:29.306199','Khung gỗ tự nhiên, đầu giường bọc da PU êm ái, thiết kế sang trọng','200 x 180 x 110 cm',9.00,'Giường ngủ bọc da PU King',78,1,150,'ACTIVE',15,'2026-01-20 11:19:29.306199',60.00),(13,4.50,7,'2026-01-20 11:19:29.306199','Ray trượt êm, chia khoang tối ưu, gương trong cánh, tiết kiệm diện tích','180 x 60 x 220 cm',13.00,'Tủ áo 3 cánh cửa lùa Walnut',52,1,75,'ACTIVE',10,'2026-01-20 11:19:29.306199',85.00),(14,4.10,8,'2026-01-20 11:19:29.306199','Gỗ cao su tiêu chuẩn, tay nắm âm, bo cạnh an toàn','45 x 40 x 50 cm',5.00,'Tủ đầu giường 2 ngăn kéo',120,1,230,'ACTIVE',90,'2026-01-20 11:19:29.306199',9.00),(15,4.30,9,'2026-01-20 11:19:29.306199','Khung sơn tĩnh điện chống gỉ, đợt MDF chống ẩm, lắp đặt dễ dàng','80 x 30 x 180 cm',10.00,'Kệ sách 5 tầng khung thép',88,1,190,'ACTIVE',40,'2026-01-20 11:19:29.306199',18.00),(16,4.40,10,'2026-01-20 11:19:29.306199','Chụp vải linen, cần cong kim loại mạ đồng, ánh sáng ấm','Ø40 x 170 cm',9.00,'Đèn sàn Scandinavian Arc',65,1,135,'ACTIVE',30,'2026-01-20 11:19:29.306199',7.50),(17,4.20,10,'2026-01-20 11:19:29.306199','Đế gốm thủ công, chao vải, công tắc xoay, ánh sáng dịu nhẹ','Ø28 x 45 cm',6.00,'Đèn bàn gốm men mờ',92,1,210,'ACTIVE',60,'2026-01-20 11:19:29.306199',2.80),(18,4.50,11,'2026-01-20 11:19:29.306199','Kính cường lực an toàn, viền mảnh sơn tĩnh điện, có thể treo hoặc dựng','60 x 160 x 3 cm',10.00,'Gương đứng viền kim loại 60x160',68,1,175,'ACTIVE',20,'2026-01-20 11:19:29.306199',12.00),(19,4.20,12,'2026-01-20 11:19:29.306199','Mặt gỗ công nghiệp chống xước, chân chữ U chắc chắn, thiết kế hiện đại','120 x 60 x 75 cm',9.00,'Bàn làm việc 120cm khung thép',75,1,160,'ACTIVE',30,'2026-01-20 11:19:29.306199',20.00),(20,4.50,13,'2026-01-20 11:19:29.306199','Tựa lưng lưới thoáng khí, đệm đúc, ngả khóa đa điểm, hỗ trợ cột sống','68 x 65 x 120 cm',12.00,'Ghế công thái học ErgoMesh',156,1,110,'ACTIVE',25,'2026-01-20 11:19:29.306199',14.00),(21,4.30,14,'2026-01-20 11:19:29.306199','Sợi tổng hợp chống bám bẩn, dễ vệ sinh, nhiều màu trung tính','160 x 230 x 1 cm',14.00,'Thảm trải sàn dệt phẳng Nordic',125,1,300,'ACTIVE',45,'2026-01-20 11:19:29.306199',6.00),(22,4.10,15,'2026-01-20 11:19:29.306199','Khung nhôm sơn tĩnh điện, sợi mây PE chống UV, chịu mọi thời tiết','150 x 90 x 74 cm',10.00,'Bàn ngoài trời giả mây 4 ghế',28,1,70,'ACTIVE',12,'2026-01-20 11:19:29.306199',32.00),(23,4.40,15,'2026-01-20 11:19:29.306199','Gỗ teak tự nhiên, chịu mưa nắng tốt, bền màu theo thời gian','150 x 60 x 90 cm',8.00,'Ghế băng ngoài trời gỗ teak',35,1,55,'ACTIVE',10,'2026-01-20 11:19:29.306199',24.00);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_requests`
--

DROP TABLE IF EXISTS `return_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_requests` (
  `return_id` int NOT NULL AUTO_INCREMENT,
  `admin_note` text,
  `created_at` datetime(6) DEFAULT NULL,
  `evidence_public_id` varchar(255) DEFAULT NULL,
  `evidence_type` enum('IMAGE','VIDEO') DEFAULT NULL,
  `evidence_url` varchar(500) DEFAULT NULL,
  `order_id` int NOT NULL,
  `order_item_id` int DEFAULT NULL,
  `reason` text NOT NULL,
  `status` enum('PENDING','APPROVED','REJECTED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`return_id`),
  KEY `FKbski88d6kewx0cbj5pk7nes01` (`order_id`),
  KEY `FKqmtolfa50ie1jxfsak1e8jnkb` (`order_item_id`),
  KEY `FK6pd9hi2rbbct43io2pgcma1sh` (`user_id`),
  CONSTRAINT `FK6pd9hi2rbbct43io2pgcma1sh` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKbski88d6kewx0cbj5pk7nes01` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKqmtolfa50ie1jxfsak1e8jnkb` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`order_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_requests`
--

LOCK TABLES `return_requests` WRITE;
/*!40000 ALTER TABLE `return_requests` DISABLE KEYS */;
INSERT INTO `return_requests` VALUES (1,NULL,'2026-05-16 10:09:14.285999',NULL,NULL,NULL,24,NULL,'k thich conmemay','REJECTED','2026-05-17 13:50:40.567833',12),(2,NULL,'2026-05-17 13:53:04.423104',NULL,NULL,NULL,31,NULL,'aaaaaaaaaaaaaaaaa','APPROVED','2026-05-17 14:33:44.359340',6),(3,NULL,'2026-05-21 06:05:15.155314','furniture/returns/u0vqtr0bdn7dgvmmuwkm','IMAGE','https://res.cloudinary.com/dyxotav3k/image/upload/v1779343514/furniture/returns/u0vqtr0bdn7dgvmmuwkm.jpg',37,NULL,'hhhhfgghhbb','APPROVED','2026-05-21 06:19:44.240551',6),(4,NULL,'2026-05-21 07:29:20.568453','furniture/returns/gt1mmjvwcgfroplv5zuj','IMAGE','https://res.cloudinary.com/dyxotav3k/image/upload/v1779348560/furniture/returns/gt1mmjvwcgfroplv5zuj.jpg',42,NULL,'san pham hu hai','APPROVED','2026-05-21 07:31:05.891785',6),(5,NULL,'2026-05-21 07:40:41.206828','furniture/returns/qxdpmxvrhbizxf2i5ya8','IMAGE','https://res.cloudinary.com/dyxotav3k/image/upload/v1779349240/furniture/returns/qxdpmxvrhbizxf2i5ya8.jpg',43,NULL,'dowrnsnsnssjsj','APPROVED','2026-05-21 07:40:47.847565',6),(6,NULL,'2026-05-21 07:47:22.638267','furniture/returns/awqpjaa6b1oieteaqare','IMAGE','https://res.cloudinary.com/dyxotav3k/image/upload/v1779349642/furniture/returns/awqpjaa6b1oieteaqare.jpg',44,NULL,'bsbsbdbdbxbxb','APPROVED','2026-05-21 07:47:42.976359',6);
/*!40000 ALTER TABLE `return_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(20) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `UK_716hgxp60ym1lifrdgp67xt5k` (`role_name`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (3,'ADMIN'),(1,'CUSTOMER'),(2,'SELLER'),(4,'SHIPPER'),(60,'VENDOR');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shipments`
--

DROP TABLE IF EXISTS `shipments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shipments` (
  `shipment_id` int NOT NULL AUTO_INCREMENT,
  `actual_delivery_date` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `estimated_delivery_date` datetime(6) DEFAULT NULL,
  `shipper_id` int NOT NULL,
  `status` enum('WAITING','IN_TRANSIT','DELIVERED','FAILED') DEFAULT NULL,
  `sub_order_id` int NOT NULL,
  `tracking_number` varchar(50) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`shipment_id`),
  UNIQUE KEY `UK_2980t5kjkkrwnjhwvit59x61k` (`tracking_number`),
  KEY `FKbwf3dx3xs0qpnp20spajeop2v` (`shipper_id`),
  KEY `FKto99ceis2eraun14d0g3kcmc` (`sub_order_id`),
  CONSTRAINT `FKbwf3dx3xs0qpnp20spajeop2v` FOREIGN KEY (`shipper_id`) REFERENCES `shippers` (`shipper_id`),
  CONSTRAINT `FKto99ceis2eraun14d0g3kcmc` FOREIGN KEY (`sub_order_id`) REFERENCES `sub_orders` (`sub_order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shipments`
--

LOCK TABLES `shipments` WRITE;
/*!40000 ALTER TABLE `shipments` DISABLE KEYS */;
/*!40000 ALTER TABLE `shipments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shippers`
--

DROP TABLE IF EXISTS `shippers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shippers` (
  `shipper_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `license_plate` varchar(20) NOT NULL,
  `phone` varchar(15) NOT NULL,
  `status` enum('PENDING','ACTIVE','INACTIVE','BANNED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  `vehicle_type` enum('BIKE','CAR','TRUCK','VAN') NOT NULL,
  PRIMARY KEY (`shipper_id`),
  UNIQUE KEY `UK_545gns2sqf6utlc5qfetlni81` (`license_plate`),
  UNIQUE KEY `UK_pb0e9s3vhmiw1j1wsxhxs78f1` (`phone`),
  UNIQUE KEY `UK_d1mghpc6axe2hl5b1mtmtq1pj` (`user_id`),
  CONSTRAINT `FK3aekq9ie8kebm7202pyqlsoqs` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shippers`
--

LOCK TABLES `shippers` WRITE;
/*!40000 ALTER TABLE `shippers` DISABLE KEYS */;
/*!40000 ALTER TABLE `shippers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_reviews`
--

DROP TABLE IF EXISTS `shop_reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_reviews` (
  `review_id` int NOT NULL AUTO_INCREMENT,
  `comment` text,
  `created_at` datetime(6) DEFAULT NULL,
  `rating` int NOT NULL,
  `shop_id` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`review_id`),
  KEY `FKbj0spep4e7ig4ku9ujw1nk9m8` (`shop_id`),
  KEY `FKaphabhqg6sks2ws2mst3g74c7` (`user_id`),
  CONSTRAINT `FKaphabhqg6sks2ws2mst3g74c7` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKbj0spep4e7ig4ku9ujw1nk9m8` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_reviews`
--

LOCK TABLES `shop_reviews` WRITE;
/*!40000 ALTER TABLE `shop_reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `shop_reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shops`
--

DROP TABLE IF EXISTS `shops`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shops` (
  `shop_id` int NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `banner` varchar(255) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `followers` int DEFAULT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `owner_id` int NOT NULL,
  `rating` decimal(3,2) DEFAULT NULL,
  `shop_name` varchar(100) NOT NULL,
  `status` enum('ACTIVE','INACTIVE','SUSPENDED') DEFAULT NULL,
  `total_products` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `views` int DEFAULT NULL,
  PRIMARY KEY (`shop_id`),
  UNIQUE KEY `UK_jbr0o7opga1xq2mv292onp42` (`shop_name`),
  KEY `FKrduswa89ayj0poad3l70nag19` (`owner_id`),
  CONSTRAINT `FKrduswa89ayj0poad3l70nag19` FOREIGN KEY (`owner_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shops`
--

LOCK TABLES `shops` WRITE;
/*!40000 ALTER TABLE `shops` DISABLE KEYS */;
INSERT INTO `shops` VALUES (1,'123 Nguyễn Huệ, Quận 1, TP.HCM','https://images.unsplash.com/photo-1615873968403-89e068629265?w=1200','2026-01-20 11:19:29.298916','Nội thất cao cấp cho ngôi nhà của bạn',1200,'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=200',1,4.60,'Furniture Store Vietnam','ACTIVE',23,'2026-01-20 11:19:29.298916',50000);
/*!40000 ALTER TABLE `shops` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sub_orders`
--

DROP TABLE IF EXISTS `sub_orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_orders` (
  `sub_order_id` int NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `order_id` int NOT NULL,
  `shipping_fee` decimal(10,2) DEFAULT NULL,
  `shop_id` int NOT NULL,
  `status` enum('PENDING','PROCESSING','SHIPPED','DELIVERED','CANCELLED') NOT NULL,
  `total_price` decimal(15,2) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`sub_order_id`),
  KEY `FKcwqdpqgm2o3ffe8ioqvb69vgd` (`order_id`),
  KEY `FKspmt38wfyf78g3wtblqjupp0o` (`shop_id`),
  CONSTRAINT `FKcwqdpqgm2o3ffe8ioqvb69vgd` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `FKspmt38wfyf78g3wtblqjupp0o` FOREIGN KEY (`shop_id`) REFERENCES `shops` (`shop_id`)
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_orders`
--

LOCK TABLES `sub_orders` WRITE;
/*!40000 ALTER TABLE `sub_orders` DISABLE KEYS */;
INSERT INTO `sub_orders` VALUES (1,'2026-04-02 01:34:38.589815',1,30000.00,1,'CANCELLED',12990000.00,'2026-04-02 01:35:00.123572'),(2,'2026-04-02 02:34:10.016570',2,30000.00,1,'PENDING',12990000.00,'2026-04-02 02:34:10.016570'),(3,'2026-05-05 08:32:34.294937',3,30000.00,1,'PENDING',2590000.00,'2026-05-05 08:32:34.294937'),(4,'2026-05-05 08:45:46.575985',4,30000.00,1,'CANCELLED',12990000.00,'2026-05-05 08:45:52.319634'),(5,'2026-05-05 14:02:10.691336',5,30000.00,1,'PENDING',12990000.00,'2026-05-05 14:02:10.691336'),(6,'2026-05-05 14:11:31.536130',6,30000.00,1,'CANCELLED',12990000.00,'2026-05-05 14:26:57.793093'),(7,'2026-05-06 04:07:54.892396',7,30000.00,1,'PENDING',5180000.00,'2026-05-06 04:07:54.892396'),(8,'2026-05-06 06:07:12.643507',8,30000.00,1,'PENDING',12990000.00,'2026-05-06 06:07:12.643507'),(9,'2026-05-06 06:10:27.236066',9,30000.00,1,'PENDING',12990000.00,'2026-05-06 06:10:27.236066'),(10,'2026-05-06 06:12:17.492079',10,30000.00,1,'PENDING',5180000.00,'2026-05-06 06:12:17.492079'),(11,'2026-05-06 06:24:17.073397',11,30000.00,1,'PENDING',2590000.00,'2026-05-06 06:24:17.073397'),(12,'2026-05-12 10:07:37.411342',12,30000.00,1,'CANCELLED',2590000.00,'2026-05-12 12:37:29.791077'),(13,'2026-05-12 10:38:41.834882',13,30000.00,1,'CANCELLED',2590000.00,'2026-05-12 12:37:28.410395'),(14,'2026-05-14 17:03:44.070232',14,30000.00,1,'PENDING',2590000.00,'2026-05-14 17:03:44.070232'),(15,'2026-05-15 14:25:09.653150',15,30000.00,1,'PENDING',12990000.00,'2026-05-15 14:25:09.653150'),(16,'2026-05-15 14:44:51.539959',16,30000.00,1,'PENDING',12990000.00,'2026-05-15 14:44:51.539959'),(17,'2026-05-16 01:36:58.005467',17,30000.00,1,'PENDING',2590000.00,'2026-05-16 01:36:58.005467'),(18,'2026-05-16 02:50:55.188261',18,30000.00,1,'PENDING',12990000.00,'2026-05-16 02:50:55.188261'),(19,'2026-05-16 03:25:17.767495',19,30000.00,1,'PENDING',12990000.00,'2026-05-16 03:25:17.767495'),(20,'2026-05-16 04:23:39.795567',20,30000.00,1,'PENDING',12990000.00,'2026-05-16 04:23:39.795567'),(21,'2026-05-16 06:53:56.424516',21,30000.00,1,'CANCELLED',12990000.00,'2026-05-16 07:19:20.143559'),(22,'2026-05-16 06:54:09.268463',22,30000.00,1,'PENDING',2590000.00,'2026-05-16 06:54:09.268463'),(23,'2026-05-16 07:13:16.972998',23,30000.00,1,'PENDING',2590000.00,'2026-05-16 07:13:16.972998'),(24,'2026-05-16 09:59:53.539899',24,30000.00,1,'PENDING',12990000.00,'2026-05-16 09:59:53.539899'),(25,'2026-05-17 02:14:03.372879',25,30000.00,1,'CANCELLED',16990000.00,'2026-05-17 02:14:37.356848'),(26,'2026-05-17 02:17:56.946275',26,30000.00,1,'CANCELLED',19580000.00,'2026-05-17 02:18:04.792876'),(27,'2026-05-17 02:18:37.776006',27,30000.00,1,'PENDING',12990000.00,'2026-05-17 02:18:37.776006'),(28,'2026-05-17 02:22:07.061753',28,30000.00,1,'PENDING',12990000.00,'2026-05-17 02:22:07.061753'),(29,'2026-05-17 11:00:16.729430',29,30000.00,1,'CANCELLED',6990000.00,'2026-05-17 11:00:33.754251'),(30,'2026-05-17 11:00:50.104186',30,30000.00,1,'PENDING',12990000.00,'2026-05-17 11:00:50.104186'),(31,'2026-05-17 13:49:39.602413',31,30000.00,1,'PENDING',12990000.00,'2026-05-17 13:49:39.602413'),(32,'2026-05-17 14:35:09.604741',32,30000.00,1,'PENDING',12990000.00,'2026-05-17 14:35:09.604741'),(33,'2026-05-17 15:00:01.828162',33,30000.00,1,'PENDING',259800000.00,'2026-05-17 15:00:01.828162'),(34,'2026-05-21 04:03:13.728870',34,30000.00,1,'PENDING',1.00,'2026-05-21 04:03:13.728870'),(35,'2026-05-21 05:04:31.852490',35,30000.00,1,'CANCELLED',12990000.00,'2026-05-21 06:05:27.543399'),(36,'2026-05-21 05:09:31.889244',36,30000.00,1,'CANCELLED',12990000.00,'2026-05-21 06:05:26.376963'),(37,'2026-05-21 05:15:30.482208',37,0.00,1,'PENDING',12990000.00,'2026-05-21 05:15:30.482208'),(38,'2026-05-21 06:17:39.912559',38,0.00,1,'PENDING',246810000.00,'2026-05-21 06:17:39.912559'),(39,'2026-05-21 07:24:27.942643',39,0.00,1,'PENDING',25981000.00,'2026-05-21 07:24:27.942643'),(40,'2026-05-21 07:26:49.691745',40,0.00,1,'CANCELLED',12990000.00,'2026-05-21 07:26:54.265970'),(41,'2026-05-21 07:27:02.047228',41,0.00,1,'CANCELLED',12990000.00,'2026-05-21 07:40:00.338843'),(42,'2026-05-21 07:28:03.761090',42,0.00,1,'PENDING',2590000.00,'2026-05-21 07:28:03.761090'),(43,'2026-05-21 07:40:08.579288',43,0.00,1,'PENDING',2590000.00,'2026-05-21 07:40:08.579288'),(44,'2026-05-21 07:46:55.462294',44,0.00,1,'PENDING',12990000.00,'2026-05-21 07:46:55.462294'),(45,'2026-06-06 04:57:46.846996',45,0.00,1,'PENDING',2590000.00,'2026-06-06 04:57:46.846996');
/*!40000 ALTER TABLE `sub_orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_coupons`
--

DROP TABLE IF EXISTS `user_coupons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_coupons` (
  `user_coupon_id` int NOT NULL AUTO_INCREMENT,
  `coupon_id` int NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `used_at` datetime(6) DEFAULT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`user_coupon_id`),
  KEY `FK9oi3p5xyfe4j32xs54nn7mi20` (`coupon_id`),
  KEY `FK654lvm2qu8l08pyg310mbd74h` (`user_id`),
  CONSTRAINT `FK654lvm2qu8l08pyg310mbd74h` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK9oi3p5xyfe4j32xs54nn7mi20` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_coupons`
--

LOCK TABLES `user_coupons` WRITE;
/*!40000 ALTER TABLE `user_coupons` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_coupons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (1,1),(5,1),(6,1),(10,1),(11,1),(12,1),(9,3);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `auth_provider` enum('LOCAL','GOOGLE') NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `gender` enum('MALE','FEMALE','OTHER') DEFAULT NULL,
  `google_id` varchar(255) DEFAULT NULL,
  `is_verified` bit(1) DEFAULT NULL,
  `last_failed_login` datetime(6) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `locked_until` datetime(6) DEFAULT NULL,
  `login_attempts` int DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `refresh_token` text,
  `reset_password_expires` datetime(6) DEFAULT NULL,
  `reset_password_token` varchar(128) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE','BANNED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK_ovh8xmu9ac27t18m56gri58i1` (`google_id`),
  UNIQUE KEY `UK_du5v5sr43g5bfnji4vb8hg5s3` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'LOCAL','2026-01-20 03:33:04.619540',NULL,'annguyen11012k4@gmail.com','An',NULL,NULL,_binary '\0',NULL,'Nguyen',NULL,0,'$2a$12$/OD8MC4g6CWqlelExpJMaehuUry9G3AZFfh0ZbERNZhCSa7IxLw7C','0334074016',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3Njg4OTk4NTAsImV4cCI6MTc2OTUwNDY1MH0.Da5nwO_iUQJzMflhC533DgxxlQpHReu5V7a2mJm4XRA',NULL,NULL,'ACTIVE','2026-01-20 09:04:10.877575','admin'),(5,'LOCAL','2026-04-02 01:09:08.916859',NULL,'annguyen@gmail.com','an',NULL,NULL,_binary '\0',NULL,'nguyen',NULL,NULL,'$2a$12$wgZkVkbGSflaximqIY2JRuLkvnk85p61YuiaFv9TPtX4yDoP3fzn.','0334074017',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI1IiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3NzUwOTIxNDksImV4cCI6MTc3NTY5Njk0OX0.HxHal2-biukmfu_4TxNHjouUWLyes9qnFUJNp01_NXg',NULL,NULL,'ACTIVE','2026-05-17 14:33:59.527653','annguyen'),(6,'LOCAL','2026-05-05 08:31:44.914858',NULL,'nva11012k4@gmail.com','Nguyen',NULL,NULL,_binary '\0',NULL,'An',NULL,0,'$2a$12$50DkKfehXd5UVANYSqvzL.J5qzcwGoeq2SoFiHDOQsk6uy39GR8m.','0334074018','https://res.cloudinary.com/dyxotav3k/image/upload/v1779348439/furniture/avatars/svfned8xsd941tehhfdm.jpg','eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI2IiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3NzkzNDc0MDIsImV4cCI6MTc3OTk1MjIwMn0.I6YPZ6Tcn17FcY799nQf_xg7qf-eBXuhXuvCCypDB2w',NULL,NULL,'ACTIVE','2026-05-21 07:27:19.324218','Annguyenax'),(9,'LOCAL','2026-05-06 06:34:25.755758',NULL,'admin@furniture.com','Admin',NULL,NULL,_binary '',NULL,'System',NULL,0,'$2b$12$b0f72y.YSTypj5GvLYWlbePwy6sIf8i26yjt7rfL1JSVQ0B06asVq',NULL,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5IiwidHlwZSI6InJlZnJlc2giLCJpYXQiOjE3ODA3MjI4MjUsImV4cCI6MTc4MTMyNzYyNX0.o2rvrB1KnTP2bmqHW76svDydS7PppsBrYVjot6vSIgg',NULL,NULL,'ACTIVE','2026-06-06 05:13:45.202730','admin_system'),(10,'LOCAL','2026-05-06 06:34:26.042834',NULL,'customer@furniture.com','Khách',NULL,NULL,_binary '',NULL,'Hàng',NULL,0,'$2b$12$b0f72y.YSTypj5GvLYWlbePwy6sIf8i26yjt7rfL1JSVQ0B06asVq',NULL,NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMCIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzgwNzIyODg5LCJleHAiOjE3ODEzMjc2ODl9.EdwS_8-Tu_T8OY_G-TJoYhu95eRbel5dqJ1JxNC-cOw',NULL,NULL,'ACTIVE','2026-06-06 05:14:49.323589','customer1'),(11,'LOCAL','2026-05-14 17:25:22.077780',NULL,'maiphuong@gmail.com','Nguyen',NULL,NULL,_binary '\0',NULL,'Phuong',NULL,NULL,'$2a$12$pfkqp1oO0C6.UCVwIqi5JeVHk/whwNVltQWVI4uRbzUO/2FU2ZP3G','0868872328',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMSIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzc4Nzc5NTIyLCJleHAiOjE3NzkzODQzMjJ9.gdrUilOdj9OyHVs28pptSC-SD0dN8zrHdwN6EOQOvXc',NULL,NULL,'ACTIVE','2026-05-14 17:25:22.084299','maiphuong'),(12,'LOCAL','2026-05-16 09:49:19.878651',NULL,'nghiale4379@gmail.com','nghia',NULL,NULL,_binary '\0',NULL,'le',NULL,0,'$2a$12$zyIbtr0oQ7s/MGMc7rspe.giwtObgZWIKxZfxdDSsH3cTSjdm2VE.','0926546591',NULL,'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMiIsInR5cGUiOiJyZWZyZXNoIiwiaWF0IjoxNzc4OTI2MTAyLCJleHAiOjE3Nzk1MzA5MDJ9.efjcivvILqB9QZgVpbUdKHdxT_vwfHp2Gf0ssEH6lDA',NULL,NULL,'ACTIVE','2026-05-16 10:08:22.920916','nghiale43');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlists`
--

DROP TABLE IF EXISTS `wishlists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlists` (
  `wishlist_id` int NOT NULL AUTO_INCREMENT,
  `added_at` datetime(6) DEFAULT NULL,
  `product_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`wishlist_id`),
  KEY `FKl7ao98u2bm8nijc1rv4jobcrx` (`product_id`),
  KEY `FK330pyw2el06fn5g28ypyljt16` (`user_id`),
  CONSTRAINT `FK330pyw2el06fn5g28ypyljt16` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKl7ao98u2bm8nijc1rv4jobcrx` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlists`
--

LOCK TABLES `wishlists` WRITE;
/*!40000 ALTER TABLE `wishlists` DISABLE KEYS */;
INSERT INTO `wishlists` VALUES (4,'2026-05-16 09:58:29.750983',2,12),(5,'2026-05-16 09:58:44.249554',1,12);
/*!40000 ALTER TABLE `wishlists` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-06 13:34:33
