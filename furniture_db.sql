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
INSERT INTO `addresses` VALUES (1,'42 Nguyễn Huệ','Thành phố Hồ Chí Minh','2026-05-01 09:00:00.000000','Quận 1',_binary '1','0901000001','Linh Trần','2026-05-01 09:00:00.000000',5,'Phường Bến Nghé'),(2,'18 Lê Lợi','Thành phố Hồ Chí Minh','2026-05-02 09:00:00.000000','Quận 3',_binary '1','0901000002','An Nguyễn','2026-05-02 09:00:00.000000',6,'Phường Võ Thị Sáu'),(3,'25 Tràng Tiền','Thành phố Hà Nội','2026-05-03 09:00:00.000000','Quận Hoàn Kiếm',_binary '1','0901000003','Minh Phạm','2026-05-03 09:00:00.000000',10,'Phường Tràng Tiền'),(4,'72 Trần Phú','Thành phố Đà Nẵng','2026-05-04 09:00:00.000000','Quận Hải Châu',_binary '1','0901000004','Phương Mai','2026-05-04 09:00:00.000000',11,'Phường Hải Châu I'),(5,'12 Nguyễn Trãi','Thành phố Hà Nội','2026-05-05 09:00:00.000000','Quận Thanh Xuân',_binary '1','0901000005','Nghĩa Lê','2026-05-05 09:00:00.000000',12,'Phường Thanh Xuân Trung'),(6,'8 Phan Chu Trinh','Thành phố Cần Thơ','2026-05-06 09:00:00.000000','Quận Ninh Kiều',_binary '0','0901000006','An Nguyễn','2026-05-06 09:00:00.000000',6,'Phường Tân An'),(7,'5 Hoàng Văn Thụ','Thành phố Hồ Chí Minh','2026-05-07 09:00:00.000000','Quận Phú Nhuận',_binary '0','0901000007','Linh Trần','2026-05-07 09:00:00.000000',5,'Phường 9');
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
INSERT INTO `cart_items` VALUES (1,1,'2026-06-06 08:30:00.000000',2790000.00,4,8,1,1,2790000.00,'2026-06-06 08:30:00.000000','Đen mun - D80 x C45cm - Gỗ óc chó'),(2,1,'2026-06-06 08:31:00.000000',3490000.00,19,37,1,1,3490000.00,'2026-06-06 08:31:00.000000','Trắng / chân đen - 120x60x75cm'),(3,2,'2026-06-06 08:35:00.000000',12990000.00,1,1,1,1,12990000.00,'2026-06-06 08:35:00.000000','Be nhạt - 3 chỗ - Vải bọc'),(4,2,'2026-06-06 08:36:00.000000',3490000.00,15,29,1,1,3490000.00,'2026-06-06 08:36:00.000000','Đen / walnut - 5 tầng'),(5,4,'2026-06-06 08:40:00.000000',1490000.00,10,20,1,1,1490000.00,'2026-06-06 08:40:00.000000','Be nhạt - ghế ăn bọc nệm'),(6,5,'2026-06-06 08:45:00.000000',5990000.00,20,39,1,1,5990000.00,'2026-06-06 08:45:00.000000','Đen - ghế công thái học');
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
INSERT INTO `carts` VALUES (1,'2026-06-01 08:00:00.000000',6280000.00,'2026-06-06 08:30:00.000000',5),(2,'2026-06-01 08:05:00.000000',16390000.00,'2026-06-06 08:35:00.000000',6),(3,'2026-06-01 08:10:00.000000',0.00,'2026-06-06 08:10:00.000000',9),(4,'2026-06-01 08:15:00.000000',1490000.00,'2026-06-06 08:40:00.000000',12),(5,'2026-06-01 08:20:00.000000',5990000.00,'2026-06-06 08:45:00.000000',10);
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
INSERT INTO `categories` VALUES (1,'Sofa & Armchair','2026-01-20 11:19:29.290315','Sofa phòng khách và ghế thư giãn','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400',NULL,'2026-06-06 09:00:00.000000'),(2,'Bàn trà','2026-01-20 11:19:29.290315','Bàn trà và bàn nước phòng khách','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=400',NULL,'2026-06-06 09:00:00.000000'),(3,'Kệ TV','2026-01-20 11:19:29.290315','Kệ tivi và tủ giải trí','https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=400',NULL,'2026-06-06 09:00:00.000000'),(4,'Bàn ăn','2026-01-20 11:19:29.290315','Bàn ăn gia đình','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=400',NULL,'2026-06-06 09:00:00.000000'),(5,'Ghế ăn','2026-01-20 11:19:29.290315','Ghế ăn và ghế phụ','https://images.unsplash.com/photo-1503602642458-232111445657?w=400',NULL,'2026-06-06 09:00:00.000000'),(6,'Giường ngủ','2026-01-20 11:19:29.290315','Giường ngủ các kích thước','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=400',NULL,'2026-06-06 09:00:00.000000'),(7,'Tủ áo','2026-01-20 11:19:29.290315','Tủ quần áo và hệ lưu trữ phòng ngủ','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=400',NULL,'2026-06-06 09:00:00.000000'),(8,'Táp đầu giường','2026-01-20 11:19:29.290315','Tủ đầu giường và táp nhỏ','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=400',NULL,'2026-06-06 09:00:00.000000'),(9,'Kệ sách','2026-01-20 11:19:29.290315','Kệ sách và kệ trang trí','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=400',NULL,'2026-06-06 09:00:00.000000'),(10,'Đèn trang trí','2026-01-20 11:19:29.290315','Đèn sàn và đèn bàn','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400',NULL,'2026-06-06 09:00:00.000000'),(11,'Gương','2026-01-20 11:19:29.290315','Gương đứng và gương treo tường','https://images.unsplash.com/photo-1618220179428-22790b461013?w=400',NULL,'2026-06-06 09:00:00.000000'),(12,'Bàn làm việc','2026-01-20 11:19:29.290315','Bàn làm việc tại nhà','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=400',NULL,'2026-06-06 09:00:00.000000'),(13,'Ghế văn phòng','2026-01-20 11:19:29.290315','Ghế văn phòng và ghế công thái học','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=400',NULL,'2026-06-06 09:00:00.000000'),(14,'Thảm','2026-01-20 11:19:29.290315','Thảm trải sàn phòng khách và phòng ngủ','https://images.unsplash.com/photo-1600166898405-da9535204843?w=400',NULL,'2026-06-06 09:00:00.000000'),(15,'Ngoài trời','2026-01-20 11:19:29.290315','Nội thất sân vườn và ban công','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=400',NULL,'2026-06-06 09:00:00.000000');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;
UPDATE `categories`
SET `category_name` = CASE `category_id`
    WHEN 1 THEN 'Phòng khách'
    WHEN 2 THEN 'Phòng ăn'
    WHEN 3 THEN 'Phòng ngủ'
    WHEN 4 THEN 'Phòng làm việc'
    WHEN 5 THEN 'Phòng bếp'
    WHEN 6 THEN 'Phòng tắm'
    WHEN 7 THEN 'Ban công'
    WHEN 8 THEN 'Sân vườn'
    WHEN 9 THEN 'Phòng trẻ em'
    WHEN 10 THEN 'Phòng giải trí'
    WHEN 11 THEN 'Lối vào'
    WHEN 12 THEN 'Phòng giặt'
    WHEN 13 THEN 'Kho lưu trữ'
    WHEN 14 THEN 'Căn hộ studio'
    WHEN 15 THEN 'Trang trí'
    ELSE `category_name`
  END,
  `description` = CASE `category_id`
    WHEN 1 THEN 'Sofa, bàn trà, kệ và thảm cho phòng khách.'
    WHEN 2 THEN 'Bàn ghế ăn gọn đẹp cho bữa cơm gia đình.'
    WHEN 3 THEN 'Giường, tủ áo, táp và đèn ngủ.'
    WHEN 4 THEN 'Bàn ghế, kệ sách cho góc làm việc.'
    WHEN 5 THEN 'Nội thất bếp và đảo bếp.'
    WHEN 6 THEN 'Tủ, kệ và gương cho phòng tắm.'
    WHEN 7 THEN 'Bàn ghế nhỏ cho ban công.'
    WHEN 8 THEN 'Nội thất ngoài trời và sân vườn.'
    WHEN 9 THEN 'Đồ dùng an toàn cho phòng trẻ em.'
    WHEN 10 THEN 'Kệ TV và đồ dùng giải trí tại nhà.'
    WHEN 11 THEN 'Gương, tủ giày và bàn console lối vào.'
    WHEN 12 THEN 'Kệ và tủ gọn cho phòng giặt.'
    WHEN 13 THEN 'Tủ, kệ và giải pháp lưu trữ.'
    WHEN 14 THEN 'Nội thất linh hoạt cho căn hộ nhỏ.'
    WHEN 15 THEN 'Đèn, gương, thảm và phụ kiện trang trí.'
    ELSE `description`
  END,
  `image` = CASE `category_id`
    WHEN 1 THEN 'https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=800&fit=crop&q=80'
    WHEN 2 THEN 'https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80'
    WHEN 3 THEN 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80'
    WHEN 4 THEN 'https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80'
    WHEN 5 THEN 'https://images.unsplash.com/photo-1556911220-bff31c812dba?w=800&fit=crop&q=80'
    WHEN 6 THEN 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80'
    WHEN 7 THEN 'https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80'
    WHEN 8 THEN 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80'
    WHEN 9 THEN 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80'
    WHEN 10 THEN 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80'
    WHEN 11 THEN 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80'
    WHEN 12 THEN 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80'
    WHEN 13 THEN 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80'
    WHEN 14 THEN 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80'
    WHEN 15 THEN 'https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80'
    ELSE `image`
  END,
  `parent_id` = NULL,
  `updated_at` = '2026-06-06 09:00:00.000000'
WHERE `category_id` BETWEEN 1 AND 15;

--
-- Table structure for table `ai_chat_messages`
--

DROP TABLE IF EXISTS `ai_chat_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_chat_messages` (
  `message_id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` varchar(64) NOT NULL,
  `user_id` int NOT NULL,
  `role` enum('USER','ASSISTANT') NOT NULL,
  `content` text NOT NULL,
  `metadata_json` text,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `idx_ai_chat_user_conversation` (`user_id`,`conversation_id`),
  CONSTRAINT `fk_ai_chat_messages_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
INSERT INTO `chat_messages` VALUES (1,'5-1','2026-06-01 09:00:00.000000',_binary '1','Chào shop, mình cần tư vấn sofa cho phòng khách 20m2.',1,'SHOP',5,'USER','2026-06-01 09:00:00.000000',NULL,NULL,'TEXT'),(2,'5-1','2026-06-01 09:03:00.000000',_binary '1','Shop gợi ý sofa gỗ sồi màu beige, kích thước vừa với không gian của anh chị.',5,'USER',1,'SHOP','2026-06-01 09:03:00.000000',NULL,NULL,'TEXT'),(3,'10-1','2026-06-02 10:10:00.000000',_binary '1','Ghế công thái học lưng lưới còn hàng màu đen không?',1,'SHOP',10,'USER','2026-06-02 10:10:00.000000',NULL,NULL,'TEXT'),(4,'10-1','2026-06-02 10:12:00.000000',_binary '1','Dạ, màu đen còn hàng và có thể giao trong 2 ngày.',10,'USER',1,'SHOP','2026-06-02 10:12:00.000000',NULL,NULL,'TEXT');
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
INSERT INTO `order_items` VALUES (1,'2026-05-20 09:10:00.000000',10.00,12990000.00,1,1,1,12990000.00,'2026-05-20 09:10:00.000000',1,'Be nhạt - 3 chỗ - Vải bọc cao cấp'),(2,'2026-05-22 10:15:00.000000',8.00,2590000.00,4,1,2,2590000.00,'2026-05-22 10:15:00.000000',7,'Nâu vân óc chó - D80 x C45cm'),(3,'2026-05-24 11:30:00.000000',11.00,15990000.00,11,1,3,15990000.00,'2026-05-24 11:30:00.000000',21,'Sồi sáng - queen 160x200cm'),(4,'2026-05-24 11:30:00.000000',5.00,490000.00,17,1,3,490000.00,'2026-05-24 11:30:00.000000',33,'Trắng ngà - đèn bàn gốm'),(5,'2026-05-26 14:20:00.000000',9.00,15990000.00,8,1,4,15990000.00,'2026-05-26 14:20:00.000000',15,'Nâu tự nhiên - bộ bàn ăn 6 ghế'),(6,'2026-05-28 15:40:00.000000',7.00,1490000.00,10,1,5,1490000.00,'2026-05-28 15:40:00.000000',20,'Be nhạt - ghế ăn bọc nệm'),(7,'2026-05-29 16:05:00.000000',10.00,12990000.00,1,1,6,12990000.00,'2026-05-29 16:05:00.000000',2,'Xám tro - 3 chỗ - Vải bọc'),(8,'2026-05-30 09:45:00.000000',11.00,17490000.00,11,1,7,17490000.00,'2026-05-30 09:45:00.000000',22,'Óc chó đậm - queen 160x200cm'),(9,'2026-06-01 13:15:00.000000',12.00,5990000.00,20,1,8,5990000.00,'2026-06-01 13:15:00.000000',39,'Đen - ghế công thái học'),(10,'2026-06-03 18:05:00.000000',10.00,9200000.00,22,1,9,9200000.00,'2026-06-03 18:05:00.000000',43,'Xám trắng - bàn ngoài trời 4 ghế'),(11,'2026-06-04 09:25:00.000000',12.00,5990000.00,20,1,10,5990000.00,'2026-06-04 09:25:00.000000',40,'Xám - ghế công thái học');
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
  `payment_method` enum('COD','BANK_TRANSFER') NOT NULL,
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
INSERT INTO `orders` VALUES (1,'2026-05-20 09:10:00.000000','Giao giờ hành chính','COD','PAID',1,30000.00,'DELIVERED',13020000.00,'2026-05-23 16:20:00.000000',5),(2,'2026-05-22 10:15:00.000000','Cần gọi trước khi giao','COD','PAID',2,0.00,'DELIVERED',2590000.00,'2026-05-25 14:10:00.000000',6),(3,'2026-05-24 11:30:00.000000','Giao cuối tuần','COD','PAID',3,50000.00,'DELIVERED',16540000.00,'2026-05-28 15:00:00.000000',10),(4,'2026-05-26 14:20:00.000000','Kiểm tra màu gỗ trước khi nhận','COD','PENDING',4,70000.00,'PROCESSING',16060000.00,'2026-05-27 09:00:00.000000',11),(5,'2026-05-28 15:40:00.000000','','COD','PENDING',5,30000.00,'PENDING',1520000.00,'2026-05-28 15:40:00.000000',12),(6,'2026-05-29 16:05:00.000000','Đổi màu sang xám','COD','CANCELLED',6,30000.00,'CANCELLED',13020000.00,'2026-05-29 17:00:00.000000',6),(7,'2026-05-30 09:45:00.000000','Lắp đặt tại phòng ngủ','COD','PENDING',1,80000.00,'SHIPPED',17570000.00,'2026-06-01 10:30:00.000000',5),(8,'2026-06-01 13:15:00.000000','','COD','PAID',3,0.00,'DELIVERED',5990000.00,'2026-06-03 11:20:00.000000',10),(9,'2026-06-03 18:05:00.000000','Giao sau 18h','COD','PENDING',7,30000.00,'PENDING',9230000.00,'2026-06-03 18:05:00.000000',5),(10,'2026-06-04 09:25:00.000000','Khách hủy do đặt trùng','COD','CANCELLED',4,50000.00,'CANCELLED',6040000.00,'2026-06-04 12:00:00.000000',11);
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
  `payment_method` enum('COD','BANK_TRANSFER') NOT NULL,
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
INSERT INTO `payments` VALUES (1,13020000.00,'2026-05-20 09:10:00.000000',1,'2026-05-23 16:20:00.000000','COD','PAID','COD-20260520-0001','2026-05-23 16:20:00.000000'),(2,2590000.00,'2026-05-22 10:15:00.000000',2,'2026-05-25 14:10:00.000000','COD','PAID','COD-20260522-0002','2026-05-25 14:10:00.000000'),(3,16540000.00,'2026-05-24 11:30:00.000000',3,'2026-05-28 15:00:00.000000','COD','PAID','COD-20260524-0003','2026-05-28 15:00:00.000000'),(4,16060000.00,'2026-05-26 14:20:00.000000',4,NULL,'COD','PENDING','COD-20260526-0004','2026-05-27 09:00:00.000000'),(5,1520000.00,'2026-05-28 15:40:00.000000',5,NULL,'COD','PENDING','COD-20260528-0005','2026-05-28 15:40:00.000000'),(6,13020000.00,'2026-05-29 16:05:00.000000',6,NULL,'COD','FAILED','COD-20260529-0006','2026-05-29 17:00:00.000000'),(7,17570000.00,'2026-05-30 09:45:00.000000',7,NULL,'COD','PENDING','COD-20260530-0007','2026-06-01 10:30:00.000000'),(8,5990000.00,'2026-06-01 13:15:00.000000',8,'2026-06-03 11:20:00.000000','COD','PAID','COD-20260601-0008','2026-06-03 11:20:00.000000'),(9,9230000.00,'2026-06-03 18:05:00.000000',9,NULL,'COD','PENDING','COD-20260603-0009','2026-06-03 18:05:00.000000'),(10,6040000.00,'2026-06-04 09:25:00.000000',10,NULL,'COD','FAILED','COD-20260604-0010','2026-06-04 12:00:00.000000');
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
INSERT INTO `product_reviews` VALUES (1,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-02 09:07:00.000000','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80',b'1',1,5,'2026-06-02 09:07:00.000000',6,NULL),(2,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-03 09:20:00.000000','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80',b'1',1,5,'2026-06-03 09:20:00.000000',10,NULL),(3,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-03 09:14:00.000000','https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&fit=crop&q=80',b'1',2,5,'2026-06-03 09:14:00.000000',10,NULL),(4,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-04 09:27:00.000000','https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&fit=crop&q=80',b'1',2,5,'2026-06-04 09:27:00.000000',11,NULL),(5,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-04 09:21:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',3,5,'2026-06-04 09:21:00.000000',11,NULL),(6,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-05 09:34:00.000000','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80',b'1',3,4,'2026-06-05 09:34:00.000000',12,NULL),(7,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-05 09:28:00.000000','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80',b'1',4,4,'2026-06-05 09:28:00.000000',12,NULL),(8,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-06 09:41:00.000000','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80',b'1',4,5,'2026-06-06 09:41:00.000000',5,NULL),(9,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-06 09:35:00.000000','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80',b'1',5,5,'2026-06-06 09:35:00.000000',5,NULL),(10,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-01 09:48:00.000000','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80',b'1',5,5,'2026-06-01 09:48:00.000000',6,NULL),(11,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-01 09:42:00.000000','https://images.unsplash.com/photo-1615874959474-d609969a20ed?w=800&fit=crop&q=80',b'1',6,5,'2026-06-01 09:42:00.000000',6,NULL),(12,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-02 09:55:00.000000','https://images.unsplash.com/photo-1615874959474-d609969a20ed?w=800&fit=crop&q=80',b'1',6,5,'2026-06-02 09:55:00.000000',10,NULL),(13,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-02 09:49:00.000000','https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80',b'1',7,5,'2026-06-02 09:49:00.000000',10,NULL),(14,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-03 09:02:00.000000','https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80',b'1',7,4,'2026-06-03 09:02:00.000000',11,NULL),(15,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-03 09:56:00.000000','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80',b'1',8,4,'2026-06-03 09:56:00.000000',11,NULL),(16,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-04 09:09:00.000000','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80',b'1',8,5,'2026-06-04 09:09:00.000000',12,NULL),(17,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-04 09:03:00.000000','https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80',b'1',9,5,'2026-06-04 09:03:00.000000',12,NULL),(18,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-05 09:16:00.000000','https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80',b'1',9,5,'2026-06-05 09:16:00.000000',5,NULL),(19,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-05 09:10:00.000000','https://images.unsplash.com/photo-1559599189-fe84dea4eb79?w=800&fit=crop&q=80',b'1',10,5,'2026-06-05 09:10:00.000000',5,NULL),(20,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-06 09:23:00.000000','https://images.unsplash.com/photo-1559599189-fe84dea4eb79?w=800&fit=crop&q=80',b'1',10,5,'2026-06-06 09:23:00.000000',6,NULL),(21,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-06 09:17:00.000000','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80',b'1',11,5,'2026-06-06 09:17:00.000000',6,NULL),(22,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-01 09:30:00.000000','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80',b'1',11,4,'2026-06-01 09:30:00.000000',10,NULL),(23,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-01 09:24:00.000000','https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&fit=crop&q=80',b'1',12,4,'2026-06-01 09:24:00.000000',10,NULL),(24,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-02 09:37:00.000000','https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&fit=crop&q=80',b'1',12,5,'2026-06-02 09:37:00.000000',11,NULL),(25,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-02 09:31:00.000000','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80',b'1',13,5,'2026-06-02 09:31:00.000000',11,NULL),(26,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-03 09:44:00.000000','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80',b'1',13,5,'2026-06-03 09:44:00.000000',12,NULL),(27,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-03 09:38:00.000000','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80',b'1',14,5,'2026-06-03 09:38:00.000000',12,NULL),(28,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-04 09:51:00.000000','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80',b'1',14,5,'2026-06-04 09:51:00.000000',5,NULL),(29,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-04 09:45:00.000000','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80',b'1',15,5,'2026-06-04 09:45:00.000000',5,NULL),(30,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-05 09:58:00.000000','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80',b'1',15,4,'2026-06-05 09:58:00.000000',6,NULL),(31,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-05 09:52:00.000000','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80',b'1',16,4,'2026-06-05 09:52:00.000000',6,NULL),(32,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-06 09:05:00.000000','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80',b'1',16,5,'2026-06-06 09:05:00.000000',10,NULL),(33,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-06 09:59:00.000000','https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=800&fit=crop&q=80',b'1',17,5,'2026-06-06 09:59:00.000000',10,NULL),(34,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-01 09:12:00.000000','https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=800&fit=crop&q=80',b'1',17,5,'2026-06-01 09:12:00.000000',11,NULL),(35,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-01 09:06:00.000000','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80',b'1',18,5,'2026-06-01 09:06:00.000000',11,NULL),(36,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-02 09:19:00.000000','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80',b'1',18,5,'2026-06-02 09:19:00.000000',12,NULL),(37,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-02 09:13:00.000000','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80',b'1',19,5,'2026-06-02 09:13:00.000000',12,NULL),(38,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-03 09:26:00.000000','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80',b'1',19,4,'2026-06-03 09:26:00.000000',5,NULL),(39,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-03 09:20:00.000000','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800&fit=crop&q=80',b'1',20,4,'2026-06-03 09:20:00.000000',5,NULL),(40,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-04 09:33:00.000000','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800&fit=crop&q=80',b'1',20,5,'2026-06-04 09:33:00.000000',6,NULL),(41,'Đóng gói kỹ, chất liệu chắc chắn, giao hàng đúng hẹn.','2026-06-04 09:27:00.000000','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80',b'1',21,5,'2026-06-04 09:27:00.000000',6,NULL),(42,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-05 09:40:00.000000','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80',b'1',21,5,'2026-06-05 09:40:00.000000',10,NULL),(43,'Mình dùng vài ngày thấy rất hài lòng, ảnh thực tế giống hình trên app.','2026-06-05 09:34:00.000000','https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80',b'1',22,5,'2026-06-05 09:34:00.000000',10,NULL),(44,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-06 09:47:00.000000','https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80',b'1',22,5,'2026-06-06 09:47:00.000000',11,NULL),(45,'Hoàn thiện tốt, kích thước chuẩn, đặt vào phòng nhìn gọn hơn hẳn.','2026-06-06 09:41:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',23,5,'2026-06-06 09:41:00.000000',11,NULL),(46,'Sản phẩm đúng mô tả, màu ngoài thực tế đẹp và dễ phối với không gian nhà.','2026-06-01 09:54:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80',b'1',23,4,'2026-06-01 09:54:00.000000',12,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_variants`
--

LOCK TABLES `product_variants` WRITE;
/*!40000 ALTER TABLE `product_variants` DISABLE KEYS */;
INSERT INTO `product_variants` VALUES (1,'Be nhạt','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80','Vải bọc cao cấp + khung gỗ sồi',12990000.00,NULL,1,NULL,'3 chỗ (220x88x80cm)',8,NULL,'2026-06-06 09:00:00.000000',45.00),(2,'Xám tro','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1493663284031-b7e3aefcae8e?w=800&fit=crop&q=80','Vải bọc cao cấp + khung gỗ sồi',12990000.00,NULL,1,NULL,'3 chỗ (220x88x80cm)',12,NULL,'2026-06-06 09:00:00.000000',45.00),(3,'Xanh cổ vịt','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1567538096630-e0c55bd6374c?w=800&fit=crop&q=80','Nhung nhung + chân kim loại',3990000.00,NULL,2,NULL,'1 chỗ (78x85x100cm)',9,NULL,'2026-06-06 09:00:00.000000',16.00),(4,'Hồng đất','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1598300042247-d088f8ab3a91?w=800&fit=crop&q=80','Nhung nhung + chân kim loại',4290000.00,NULL,2,NULL,'1 chỗ (78x85x100cm)',9,NULL,'2026-06-06 09:00:00.000000',16.00),(5,'Xám nhạt','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80','Vải nỉ cao cấp + khung gỗ',18500000.00,NULL,3,NULL,'Góc L (250x160x90cm)',8,NULL,'2026-06-06 09:00:00.000000',48.00),(6,'Xanh than','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=800&fit=crop&q=80','Vải nỉ cao cấp + khung gỗ',19500000.00,NULL,3,NULL,'Góc L (250x160x90cm)',8,NULL,'2026-06-06 09:00:00.000000',48.00),(7,'Nâu vân óc chó','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80','Gỗ óc chó veneer',2590000.00,NULL,4,NULL,'D80 x C45cm',16,NULL,'2026-06-06 09:00:00.000000',14.00),(8,'Đen mun','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1530018607912-eff2daa1bac4?w=800&fit=crop&q=80','Gỗ óc chó sơn đen',2790000.00,NULL,4,NULL,'D80 x C45cm',14,NULL,'2026-06-06 09:00:00.000000',14.00),(9,'Vàng đồng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80','Kính cường lực + khung thép mạ vàng',4200000.00,NULL,5,NULL,'100x50x45cm',11,NULL,'2026-06-06 09:00:00.000000',18.00),(10,'Bạc inox','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80','Kính cường lực + khung inox',3800000.00,NULL,5,NULL,'100x50x45cm',11,NULL,'2026-06-06 09:00:00.000000',18.00),(11,'Trắng sữa','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80','MDF chống ẩm sơn trắng',3800000.00,NULL,6,NULL,'180x40x45cm',10,NULL,'2026-06-06 09:00:00.000000',28.00),(12,'Vân óc chó','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1615874959474-d609969a20ed?w=800&fit=crop&q=80','MDF phủ vân gỗ óc chó',4400000.00,NULL,6,NULL,'180x40x45cm',8,NULL,'2026-06-06 09:00:00.000000',28.00),(13,'Sồi tự nhiên','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=800&fit=crop&q=80','Gỗ sồi nguyên khối',6990000.00,NULL,7,NULL,'D120 x C75cm',8,NULL,'2026-06-06 09:00:00.000000',34.00),(14,'Sồi mở rộng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80','Gỗ sồi nguyên khối',8490000.00,NULL,7,NULL,'D150 x C75cm',6,NULL,'2026-06-06 09:00:00.000000',38.00),(15,'Nâu tự nhiên','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1617806118233-18e1de247200?w=800&fit=crop&q=80','Gỗ cao su nguyên khối',15990000.00,NULL,8,NULL,'160x80x75cm + 6 ghế',6,NULL,'2026-06-06 09:00:00.000000',42.00),(16,'Trắng sữa','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80','Gỗ cao su sơn trắng',16990000.00,NULL,8,NULL,'160x80x75cm + 6 ghế',6,NULL,'2026-06-06 09:00:00.000000',42.00),(17,'Gỗ tự nhiên','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80','Gỗ ép uốn cong + chân gỗ',1450000.00,NULL,9,NULL,'45x52x80cm (2 ghế)',18,NULL,'2026-06-06 09:00:00.000000',10.00),(18,'Đen','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1581539250439-c96689b516dd?w=800&fit=crop&q=80','Gỗ ép sơn đen + chân gỗ',1450000.00,NULL,9,NULL,'45x52x80cm (2 ghế)',17,NULL,'2026-06-06 09:00:00.000000',10.00),(19,'Xám nhạt','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1559599189-fe84dea4eb79?w=800&fit=crop&q=80','Nệm vải + chân gỗ sồi',1490000.00,NULL,10,NULL,'48x55x82cm',20,NULL,'2026-06-06 09:00:00.000000',7.00),(20,'Be nhạt','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1503602642458-232111445657?w=800&fit=crop&q=80','Nệm vải + chân gỗ sồi',1490000.00,NULL,10,NULL,'48x55x82cm',20,NULL,'2026-06-06 09:00:00.000000',7.00),(21,'Sồi sáng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80','Gỗ sồi + đầu giường bọc vải',15990000.00,NULL,11,NULL,'queen 160x200cm',5,NULL,'2026-06-06 09:00:00.000000',52.00),(22,'Óc chó đậm','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&fit=crop&q=80','Gỗ óc chó + đầu giường bọc vải',17490000.00,NULL,11,NULL,'queen 160x200cm',5,NULL,'2026-06-06 09:00:00.000000',52.00),(23,'Đen huyền','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&fit=crop&q=80','Da PU + khung gỗ',14900000.00,NULL,12,NULL,'king 180x200cm',4,NULL,'2026-06-06 09:00:00.000000',60.00),(24,'Nâu đất','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1522771739844-6a9f6d5f14af?w=800&fit=crop&q=80','Da PU + khung gỗ',15900000.00,NULL,12,NULL,'king 180x200cm',4,NULL,'2026-06-06 09:00:00.000000',60.00),(25,'Vân óc chó','2026-01-20 11:19:29.309659','https://gvawood.com/cdn/shop/files/aa1d659b4486b0be2765947a8253e324_1a883734-b658-4034-b5d1-c2418a7659f9.png?crop=center&height=1200&v=1760696756&width=1200','MDF phủ melamine vân gỗ',9800000.00,NULL,13,NULL,'180x60x220cm',4,NULL,'2026-06-06 09:00:00.000000',85.00),(26,'Trắng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80','MDF phủ melamine trắng',8900000.00,NULL,13,NULL,'180x60x220cm',3,NULL,'2026-06-06 09:00:00.000000',85.00),(27,'Trắng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80','MDF sơn trắng',1290000.00,NULL,14,NULL,'45x40x50cm',23,NULL,'2026-06-06 09:00:00.000000',9.00),(28,'Vân óc chó','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1499933374294-4584851497cc?w=800&fit=crop&q=80','MDF phủ vân gỗ',1490000.00,NULL,14,NULL,'45x40x50cm',22,NULL,'2026-06-06 09:00:00.000000',9.00),(29,'Đen / walnut','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80','Khung thép + vân gỗ',3490000.00,NULL,15,NULL,'80x30x180cm',10,NULL,'2026-06-06 09:00:00.000000',18.00),(30,'Trắng / gỗ thông','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80','Khung thép trắng + vân gỗ',3290000.00,NULL,15,NULL,'80x30x180cm',10,NULL,'2026-06-06 09:00:00.000000',18.00),(31,'Vàng đồng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80','Kim loại mạ vàng + chao vải',2800000.00,NULL,16,NULL,'Cao 165cm',9,NULL,'2026-06-06 09:00:00.000000',7.50),(32,'Đen nhám','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80','Kim loại sơn đen + chao vải',2600000.00,NULL,16,NULL,'Cao 165cm',9,NULL,'2026-06-06 09:00:00.000000',7.50),(33,'Trắng ngà','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1540932239986-30128078f3c5?w=800&fit=crop&q=80','Gốm sứ + chao vải linen',890000.00,NULL,17,NULL,'D20 x C38cm',15,NULL,'2026-06-06 09:00:00.000000',2.80),(34,'Xanh sage','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=800&fit=crop&q=80','Gốm sứ + chao vải linen',990000.00,NULL,17,NULL,'D20 x C38cm',15,NULL,'2026-06-06 09:00:00.000000',2.80),(35,'Đen','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80','Khung kim loại sơn đen',2200000.00,NULL,18,NULL,'60x160cm',7,NULL,'2026-06-06 09:00:00.000000',12.00),(36,'Vàng đồng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1606170033648-5d55a3edf314?w=800&fit=crop&q=80','Khung kim loại mạ vàng',2500000.00,NULL,18,NULL,'60x160cm',7,NULL,'2026-06-06 09:00:00.000000',12.00),(37,'Trắng / chân đen','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=800&fit=crop&q=80','MDF + khung thép',3490000.00,NULL,19,NULL,'120x60x75cm',9,NULL,'2026-06-06 09:00:00.000000',20.00),(38,'Vân óc chó / chân đen','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800&fit=crop&q=80','MDF vân gỗ + khung thép',3790000.00,NULL,19,NULL,'120x60x75cm',9,NULL,'2026-06-06 09:00:00.000000',20.00),(39,'Đen','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1580480055273-228ff5388ef8?w=800&fit=crop&q=80','Lưới thoáng khí + khung nhôm',5990000.00,NULL,20,NULL,'65x65x110-125cm',8,NULL,'2026-06-06 09:00:00.000000',14.00),(40,'Xám','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800&fit=crop&q=80','Lưới thoáng khí + khung nhôm',5990000.00,NULL,20,NULL,'65x65x110-125cm',8,NULL,'2026-06-06 09:00:00.000000',14.00),(41,'Be nhạt / kem','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80','Sợi polyester cao cấp',1890000.00,NULL,21,NULL,'160x230cm',14,NULL,'2026-06-06 09:00:00.000000',6.00),(42,'Xám / trắng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1576185850227-1f72b7f8d483?w=800&fit=crop&q=80','Sợi polyester cao cấp',1890000.00,NULL,21,NULL,'160x230cm',14,NULL,'2026-06-06 09:00:00.000000',6.00),(43,'Xám trắng','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80','Mây nhựa PE + khung nhôm',9200000.00,NULL,22,NULL,'Bàn D120 + 4 ghế',4,NULL,'2026-06-06 09:00:00.000000',32.00),(44,'Nâu tự nhiên','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80','Mây nhựa PE + khung nhôm',8900000.00,NULL,22,NULL,'Bàn D120 + 4 ghế',4,NULL,'2026-06-06 09:00:00.000000',32.00),(45,'Gỗ tếch','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80','Gỗ tếch nguyên khối',4500000.00,NULL,23,NULL,'150x60x90cm',5,NULL,'2026-06-06 09:00:00.000000',24.00),(46,'Xám bạc','2026-01-20 11:19:29.309659','https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80','Gỗ tếch + đệm ngoài trời',5200000.00,NULL,23,NULL,'150x60x90cm',4,NULL,'2026-06-06 09:00:00.000000',24.00);
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
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,4.80,1,'2026-01-20 11:19:29.306199','Sofa vải 3 chỗ, đệm ngồi dày, khung gỗ sồi, màu trung tính dễ phối phòng khách','220 x 88 x 80 cm',10.00,'Sofa vải gỗ sồi 3 chỗ',18,1,96,'ACTIVE',20,'2026-06-06 09:00:00.000000',45.00),(2,4.70,1,'2026-01-20 11:19:29.306199','Ghế thư giãn bọc nhung, lưng cao, chân kim loại mạ vàng, hợp góc đọc sách','78 x 85 x 100 cm',12.00,'Ghế thư giãn bọc nhung',12,1,54,'ACTIVE',18,'2026-06-06 09:00:00.000000',16.00),(3,4.60,1,'2026-01-20 11:19:29.306199','Sofa góc chữ L cho căn hộ hiện đại, vải nỉ dày dặn, khung gỗ tự nhiên','250 x 160 x 90 cm',8.00,'Sofa góc chữ L hiện đại',14,1,61,'ACTIVE',16,'2026-06-06 09:00:00.000000',48.00),(4,4.70,2,'2026-01-20 11:19:29.306199','Bàn trà tròn mặt veneer vân óc chó, chân gỗ đặc, phủ sơn chống trầy','80 x 80 x 40 cm',8.00,'Bàn trà tròn vân óc chó',16,1,83,'ACTIVE',30,'2026-06-06 09:00:00.000000',14.00),(5,4.50,2,'2026-01-20 11:19:29.306199','Bàn nước mặt kính cường lực, khung thép mạ vàng, hợp phòng khách sang trọng','100 x 50 x 45 cm',6.00,'Bàn nước mặt kính khung vàng',9,1,37,'ACTIVE',22,'2026-06-06 09:00:00.000000',18.00),(6,4.60,3,'2026-01-20 11:19:29.306199','Kệ TV tối giản có ngăn kéo và khoang mở, MDF chống ẩm','180 x 40 x 45 cm',7.00,'Kệ TV 180cm Bắc Âu',10,1,42,'ACTIVE',18,'2026-06-06 09:00:00.000000',28.00),(7,4.80,4,'2026-01-20 11:19:29.306199','Bàn ăn tròn gỗ sồi nguyên khối, phủ dầu lau an toàn thực phẩm','120 x 120 x 75 cm',9.00,'Bàn ăn tròn gỗ sồi',13,1,48,'ACTIVE',14,'2026-06-06 09:00:00.000000',34.00),(8,4.60,4,'2026-01-20 11:19:29.306199','Bộ bàn ăn 6 ghế gỗ cao su, mặt bàn rộng, phủ sơn chống ẩm','160 x 80 x 75 cm',10.00,'Bộ bàn ăn 6 ghế gỗ cao su',15,1,52,'ACTIVE',12,'2026-06-06 09:00:00.000000',42.00),(9,4.40,5,'2026-01-20 11:19:29.306199','Bộ 2 ghế ăn gỗ ép uốn cong, nhẹ, bền, dễ xếp với bàn ăn nhỏ','47 x 50 x 82 cm',6.00,'Bộ 2 ghế ăn gỗ uốn',11,1,74,'ACTIVE',35,'2026-06-06 09:00:00.000000',10.00),(10,4.50,5,'2026-01-20 11:19:29.306199','Ghế ăn bọc nệm vải bố, chân gỗ sồi, ngồi êm trong bữa ăn dài','45 x 50 x 85 cm',7.00,'Ghế ăn bọc nệm Bắc Âu',12,1,69,'ACTIVE',40,'2026-06-06 09:00:00.000000',7.00),(11,4.80,6,'2026-01-20 11:19:29.306199','Giường queen khung gỗ sồi, vạt phản chắc, đầu giường bọc vải','206 x 166 x 105 cm',11.00,'Giường ngủ queen Bắc Âu',20,1,45,'ACTIVE',10,'2026-06-06 09:00:00.000000',52.00),(12,4.60,6,'2026-01-20 11:19:29.306199','Giường king bọc da PU, khung gỗ tự nhiên, đầu giường êm','200 x 180 x 110 cm',9.00,'Giường ngủ bọc da PU king',14,1,36,'ACTIVE',8,'2026-06-06 09:00:00.000000',60.00),(13,4.50,7,'2026-01-20 11:19:29.306199','Tủ áo cửa lùa, ray trượt êm, chia khoang treo và gấp đồ khoa học','180 x 60 x 220 cm',13.00,'Tủ áo cửa lùa vân óc chó',10,1,22,'ACTIVE',7,'2026-06-06 09:00:00.000000',85.00),(14,4.40,8,'2026-01-20 11:19:29.306199','Táp đầu giường 2 ngăn kéo, tay nắm âm, bo cạnh an toàn','45 x 40 x 50 cm',5.00,'Táp đầu giường 2 ngăn kéo',17,1,88,'ACTIVE',45,'2026-06-06 09:00:00.000000',9.00),(15,4.50,9,'2026-01-20 11:19:29.306199','Kệ sách 5 tầng khung thép sơn tĩnh điện, đợt MDF chống ẩm','80 x 30 x 180 cm',10.00,'Kệ sách 5 tầng khung thép',13,1,57,'ACTIVE',20,'2026-06-06 09:00:00.000000',18.00),(16,4.60,10,'2026-01-20 11:19:29.306199','Đèn sàn cần cong, chụp vải linen, ánh sáng vàng ấm','40 x 40 x 170 cm',9.00,'Đèn sàn cần cong Bắc Âu',11,1,46,'ACTIVE',18,'2026-06-06 09:00:00.000000',7.50),(17,4.40,10,'2026-01-20 11:19:29.306199','Đèn bàn đế gốm men mờ, chao vải, phù hợp bàn đầu giường','28 x 28 x 45 cm',6.00,'Đèn bàn gốm men mờ',16,1,72,'ACTIVE',30,'2026-06-06 09:00:00.000000',2.80),(18,4.70,11,'2026-01-20 11:19:29.306199','Gương đứng viền kim loại mảnh, có thể treo hoặc dựng sàn','60 x 160 x 3 cm',10.00,'Gương đứng viền kim loại',12,1,51,'ACTIVE',14,'2026-06-06 09:00:00.000000',12.00),(19,4.50,12,'2026-01-20 11:19:29.306199','Bàn làm việc mặt gỗ chống xước, chân thép chữ U, hợp góc làm việc tại nhà','120 x 60 x 75 cm',9.00,'Bàn làm việc 120cm khung thép',14,1,63,'ACTIVE',18,'2026-06-06 09:00:00.000000',20.00),(20,4.70,13,'2026-01-20 11:19:29.306199','Ghế công thái học tựa lưng lưới, đệm đúc, ngả khóa nhiều mức','68 x 65 x 120 cm',12.00,'Ghế công thái học lưng lưới',19,1,58,'ACTIVE',16,'2026-06-06 09:00:00.000000',14.00),(21,4.40,14,'2026-01-20 11:19:29.306199','Thảm dệt phẳng sợi tổng hợp, ít bám bụi, dễ vệ sinh','160 x 230 x 1 cm',14.00,'Thảm dệt phẳng Bắc Âu',15,1,92,'ACTIVE',28,'2026-06-06 09:00:00.000000',6.00),(22,4.50,15,'2026-01-20 11:19:29.306199','Bộ bàn ghế ngoài trời khung nhôm, sợi mây PE chống UV','150 x 90 x 74 cm',10.00,'Bộ bàn ngoài trời giả mây 4 ghế',8,1,25,'ACTIVE',8,'2026-06-06 09:00:00.000000',32.00),(23,4.60,15,'2026-01-20 11:19:29.306199','Ghế băng gỗ tếch tự nhiên, chịu mưa nắng tốt, màu gỗ ấm','150 x 60 x 90 cm',8.00,'Ghế băng ngoài trời gỗ tếch',9,1,31,'ACTIVE',9,'2026-06-06 09:00:00.000000',24.00);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;
UPDATE `products`
SET `category_id` = CASE `product_id`
    WHEN 1 THEN 1
    WHEN 2 THEN 1
    WHEN 3 THEN 1
    WHEN 4 THEN 1
    WHEN 5 THEN 1
    WHEN 6 THEN 10
    WHEN 7 THEN 2
    WHEN 8 THEN 2
    WHEN 9 THEN 2
    WHEN 10 THEN 2
    WHEN 11 THEN 3
    WHEN 12 THEN 3
    WHEN 13 THEN 13
    WHEN 14 THEN 3
    WHEN 15 THEN 4
    WHEN 16 THEN 15
    WHEN 17 THEN 3
    WHEN 18 THEN 11
    WHEN 19 THEN 4
    WHEN 20 THEN 4
    WHEN 21 THEN 9
    WHEN 22 THEN 7
    WHEN 23 THEN 8
    ELSE `category_id`
  END,
  `product_name` = CASE `product_id`
    WHEN 1 THEN 'Sofa vải gỗ sồi 3 chỗ'
    WHEN 2 THEN 'Ghế thư giãn bọc nhung'
    WHEN 3 THEN 'Sofa góc chữ L hiện đại'
    WHEN 4 THEN 'Bàn trà tròn vân óc chó'
    WHEN 5 THEN 'Bàn nước kính khung vàng'
    WHEN 6 THEN 'Kệ TV 180cm Bắc Âu'
    WHEN 7 THEN 'Bàn ăn tròn gỗ sồi'
    WHEN 8 THEN 'Bộ bàn ăn 6 ghế'
    WHEN 9 THEN 'Bộ 2 ghế ăn gỗ uốn'
    WHEN 10 THEN 'Ghế ăn bọc nệm Bắc Âu'
    WHEN 11 THEN 'Giường ngủ queen Bắc Âu'
    WHEN 12 THEN 'Giường king bọc da PU'
    WHEN 13 THEN 'Tủ lưu trữ cửa lùa vân óc chó'
    WHEN 14 THEN 'Táp đầu giường 2 ngăn'
    WHEN 15 THEN 'Kệ sách 5 tầng'
    WHEN 16 THEN 'Đèn sàn cần cong Bắc Âu'
    WHEN 17 THEN 'Đèn bàn gốm men mờ'
    WHEN 18 THEN 'Gương đứng viền kim loại'
    WHEN 19 THEN 'Bàn làm việc 120cm'
    WHEN 20 THEN 'Ghế công thái học lưng lưới'
    WHEN 21 THEN 'Thảm phòng trẻ em Bắc Âu'
    WHEN 22 THEN 'Bộ bàn ghế ban công 4 ghế'
    WHEN 23 THEN 'Ghế băng sân vườn gỗ tếch'
    ELSE `product_name`
  END,
  `description` = CASE `product_id`
    WHEN 1 THEN 'Sofa vải 3 chỗ, đệm dày, khung gỗ sồi, hợp phòng khách hiện đại.'
    WHEN 2 THEN 'Ghế thư giãn lưng cao, bọc nhung mềm, đặt đẹp ở góc đọc sách phòng khách.'
    WHEN 3 THEN 'Sofa góc chữ L rộng rãi, vải nỉ dày, tối ưu phòng khách căn hộ.'
    WHEN 4 THEN 'Bàn trà tròn veneer vân óc chó, chân gỗ chắc, mặt phủ chống trầy.'
    WHEN 5 THEN 'Bàn nước kính cường lực, khung kim loại sang, dễ phối sofa phòng khách.'
    WHEN 6 THEN 'Kệ TV tối giản, có ngăn kéo và khoang mở cho phòng giải trí.'
    WHEN 7 THEN 'Bàn ăn tròn gỗ sồi nguyên khối, phù hợp gia đình 4 người.'
    WHEN 8 THEN 'Bộ bàn ăn 6 ghế gỗ cao su, mặt rộng, màu ấm cho phòng ăn.'
    WHEN 9 THEN 'Bộ 2 ghế ăn gỗ ép uốn cong, nhẹ, bền, hợp bàn ăn nhỏ.'
    WHEN 10 THEN 'Ghế ăn bọc nệm vải bố, chân gỗ sồi, ngồi êm trong bữa ăn dài.'
    WHEN 11 THEN 'Giường queen khung gỗ sồi, vạt chắc, đầu giường bọc vải êm.'
    WHEN 12 THEN 'Giường king bọc da PU, khung gỗ tự nhiên, đầu giường dày dặn.'
    WHEN 13 THEN 'Tủ lưu trữ cửa lùa, ray êm, chia khoang treo và gấp đồ khoa học.'
    WHEN 14 THEN 'Táp đầu giường 2 ngăn kéo, tay nắm âm, bo cạnh an toàn.'
    WHEN 15 THEN 'Kệ sách 5 tầng khung thép, đợt MDF chống ẩm cho góc làm việc.'
    WHEN 16 THEN 'Đèn sàn cần cong, chụp vải linen, ánh sáng vàng ấm dễ trang trí.'
    WHEN 17 THEN 'Đèn bàn gốm men mờ, chao vải, phù hợp táp đầu giường.'
    WHEN 18 THEN 'Gương đứng viền kim loại mảnh, hợp lối vào, phòng ngủ hoặc trang trí.'
    WHEN 19 THEN 'Bàn làm việc mặt gỗ chống xước, chân thép chữ U, gọn cho góc làm việc tại nhà.'
    WHEN 20 THEN 'Ghế công thái học lưng lưới, đệm đúc, ngả khóa nhiều mức.'
    WHEN 21 THEN 'Thảm dệt phẳng mềm, ít bám bụi, phù hợp phòng trẻ em và góc chơi.'
    WHEN 22 THEN 'Bộ bàn ghế ban công khung nhôm, mây PE chống UV, gọn cho không gian nhỏ.'
    WHEN 23 THEN 'Ghế băng gỗ tếch chịu mưa nắng, màu gỗ ấm cho sân vườn.'
    ELSE `description`
  END
WHERE `product_id` BETWEEN 1 AND 23;
INSERT INTO `products` (`product_id`,`average_rating`,`category_id`,`created_at`,`description`,`dimensions`,`discount`,`product_name`,`review_count`,`shop_id`,`sold`,`status`,`stock`,`updated_at`,`weight`) VALUES
(24,4.60,5,'2026-06-06 09:00:00.000000','Xe đảo bếp có mặt gỗ cao su, kệ mở và bánh xe khóa, tiện sơ chế và lưu đồ bếp.','90 x 45 x 90 cm',8.00,'Xe đảo bếp có bánh xe',0,1,18,'ACTIVE',12,'2026-06-06 09:00:00.000000',24.00),
(25,4.50,6,'2026-06-06 09:00:00.000000','Tủ lavabo chống ẩm, ngăn kéo êm, mặt đá trắng gọn cho phòng tắm nhỏ.','80 x 46 x 85 cm',7.00,'Tủ lavabo chống ẩm 80cm',0,1,15,'ACTIVE',10,'2026-06-06 09:00:00.000000',32.00),
(26,4.40,12,'2026-06-06 09:00:00.000000','Kệ phòng giặt khung thép sơn tĩnh điện, có tầng để máy và giỏ đồ.','68 x 40 x 170 cm',6.00,'Kệ phòng giặt đa năng',0,1,21,'ACTIVE',16,'2026-06-06 09:00:00.000000',18.00),
(27,4.70,14,'2026-06-06 09:00:00.000000','Sofa giường gấp mở nhanh, dùng làm ghế ban ngày và giường phụ cho căn hộ nhỏ.','190 x 88 x 82 cm',10.00,'Sofa giường gấp gọn',0,1,24,'ACTIVE',12,'2026-06-06 09:00:00.000000',42.00);
INSERT INTO `products` (`product_id`,`average_rating`,`category_id`,`created_at`,`description`,`dimensions`,`discount`,`product_name`,`review_count`,`shop_id`,`sold`,`status`,`stock`,`updated_at`,`weight`) VALUES
(28,4.60,5,'2026-06-06 09:00:00.000000','Tủ bếp dưới chống ẩm, có ngăn kéo và khoang chứa nồi chảo, hợp căn hộ nhỏ.','120 x 55 x 85 cm',9.00,'Tủ bếp dưới mô đun 120cm',0,1,19,'ACTIVE',12,'2026-06-06 09:00:00.000000',46.00),
(29,4.50,6,'2026-06-06 09:00:00.000000','Gương phòng tắm có kệ để đồ và đèn LED viền, chống ẩm, dễ vệ sinh.','70 x 12 x 80 cm',6.00,'Gương kệ phòng tắm LED',0,1,22,'ACTIVE',14,'2026-06-06 09:00:00.000000',9.00),
(30,4.40,7,'2026-06-06 09:00:00.000000','Bàn cà phê gấp gọn cho ban công, mặt chống nước, dễ cất khi không dùng.','60 x 60 x 72 cm',5.00,'Bàn cà phê ban công gấp gọn',0,1,30,'ACTIVE',20,'2026-06-06 09:00:00.000000',8.00),
(31,4.70,8,'2026-06-06 09:00:00.000000','Ghế thư giãn gỗ acacia có đệm rời, chịu nắng nhẹ, hợp sân vườn và hiên nhà.','68 x 82 x 78 cm',8.00,'Ghế thư giãn sân vườn Acacia',0,1,17,'ACTIVE',10,'2026-06-06 09:00:00.000000',19.00),
(32,4.60,9,'2026-06-06 09:00:00.000000','Giường trẻ em khung gỗ bo cạnh, có hộc kéo dưới gầm để đồ chơi và chăn gối.','206 x 106 x 75 cm',10.00,'Giường trẻ em hộc kéo',0,1,13,'ACTIVE',8,'2026-06-06 09:00:00.000000',38.00),
(33,4.50,11,'2026-06-06 09:00:00.000000','Tủ giày lối vào dáng mỏng, có ghế ngồi thay giày và hộc để ô dù.','100 x 35 x 90 cm',7.00,'Tủ giày lối vào có ghế ngồi',0,1,26,'ACTIVE',16,'2026-06-06 09:00:00.000000',28.00),
(34,4.40,12,'2026-06-06 09:00:00.000000','Tủ giặt đứng chống ẩm, chia khoang để chất tẩy rửa, khăn và giỏ đồ.','60 x 40 x 180 cm',6.00,'Tủ giặt đứng dáng mỏng',0,1,18,'ACTIVE',12,'2026-06-06 09:00:00.000000',26.00),
(35,4.50,13,'2026-06-06 09:00:00.000000','Tủ kho nhiều ngăn, cửa phẳng, dùng cho đồ gia dụng, vali và vật dụng ít dùng.','120 x 45 x 200 cm',8.00,'Tủ kho đa năng 120cm',0,1,14,'ACTIVE',9,'2026-06-06 09:00:00.000000',58.00),
(36,4.60,14,'2026-06-06 09:00:00.000000','Bàn ăn gập tường kiêm bàn làm việc, tiết kiệm diện tích cho căn hộ nhỏ.','90 x 60 x 75 cm',9.00,'Bàn gập tường cho căn hộ nhỏ',0,1,20,'ACTIVE',14,'2026-06-06 09:00:00.000000',17.00),
(37,4.50,15,'2026-06-06 09:00:00.000000','Bộ tranh vải 3 tấm tông trung tính, tạo điểm nhấn nhẹ cho phòng khách và phòng ngủ.','120 x 60 x 3 cm',5.00,'Bộ tranh vải tông trung tính',0,1,32,'ACTIVE',24,'2026-06-06 09:00:00.000000',3.00);
INSERT INTO `product_variants` (`variant_id`,`color`,`created_at`,`image_url`,`material`,`price`,`processor`,`product_id`,`ram`,`size`,`stock`,`storage`,`updated_at`,`weight`) VALUES
(49,'Gỗ tự nhiên','2026-06-06 09:00:00.000000','https://foter.com/photos/425/rolling-kitchen-island-with-folding-leaf-and-cart-handles.jpeg?s=lbx','Gỗ cao su + khung thép',4290000.00,NULL,24,NULL,'90x45x90cm',6,NULL,'2026-06-06 09:00:00.000000',24.00),
(50,'Trắng / oak','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1556911220-bff31c812dba?w=800&fit=crop&q=80','MDF chống ẩm + mặt gỗ',4590000.00,NULL,24,NULL,'100x50x90cm',6,NULL,'2026-06-06 09:00:00.000000',28.00),
(51,'Trắng đá','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80','MDF chống ẩm + mặt đá',5290000.00,NULL,25,NULL,'80x46x85cm',5,NULL,'2026-06-06 09:00:00.000000',32.00),
(52,'Vân óc chó','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80','MDF chống ẩm phủ vân gỗ',5690000.00,NULL,25,NULL,'90x46x85cm',5,NULL,'2026-06-06 09:00:00.000000',36.00),
(53,'Trắng','2026-06-06 09:00:00.000000','https://images-na.ssl-images-amazon.com/images/I/71sJ1BfzANL._AC_SL1500_.jpg','Thép sơn tĩnh điện + MDF',1890000.00,NULL,26,NULL,'68x40x170cm',8,NULL,'2026-06-06 09:00:00.000000',18.00),
(54,'Đen nhám','2026-06-06 09:00:00.000000','https://image1.rank-king.jp/article/original/75204.webp','Thép sơn tĩnh điện + MDF',1990000.00,NULL,26,NULL,'75x40x170cm',8,NULL,'2026-06-06 09:00:00.000000',20.00),
(55,'Xám linen','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=800&fit=crop&q=80','Vải linen + khung gỗ',7990000.00,NULL,27,NULL,'190x88x82cm',6,NULL,'2026-06-06 09:00:00.000000',42.00),
(56,'Be nhạt','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=800&fit=crop&q=80','Vải bố + khung gỗ',8290000.00,NULL,27,NULL,'200x90x82cm',6,NULL,'2026-06-06 09:00:00.000000',44.00),
(57,'Trắng mờ','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1556911220-bff31c812dba?w=800&fit=crop&q=80','MDF chống ẩm phủ acrylic',6890000.00,NULL,28,NULL,'120x55x85cm',6,NULL,'2026-06-06 09:00:00.000000',46.00),
(58,'Vân óc chó','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1556911220-bff31c812dba?w=800&fit=crop&q=80','MDF chống ẩm phủ vân gỗ',7390000.00,NULL,28,NULL,'120x55x85cm',6,NULL,'2026-06-06 09:00:00.000000',48.00),
(59,'Gương bạc','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80','Gương Bỉ + khung nhôm',2890000.00,NULL,29,NULL,'70x12x80cm',7,NULL,'2026-06-06 09:00:00.000000',9.00),
(60,'Đen viền mảnh','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80','Gương Bỉ + khung nhôm đen',3290000.00,NULL,29,NULL,'80x12x80cm',7,NULL,'2026-06-06 09:00:00.000000',10.00),
(61,'Trắng','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80','Thép sơn tĩnh điện + mặt HPL',1290000.00,NULL,30,NULL,'60x60x72cm',10,NULL,'2026-06-06 09:00:00.000000',8.00),
(62,'Xanh olive','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80','Thép sơn tĩnh điện + mặt HPL',1490000.00,NULL,30,NULL,'65x65x72cm',10,NULL,'2026-06-06 09:00:00.000000',9.00),
(63,'Gỗ acacia','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80','Gỗ acacia + đệm vải ngoài trời',3890000.00,NULL,31,NULL,'68x82x78cm',5,NULL,'2026-06-06 09:00:00.000000',19.00),
(64,'Xám tro','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80','Gỗ acacia sơn xám + đệm',4190000.00,NULL,31,NULL,'68x82x78cm',5,NULL,'2026-06-06 09:00:00.000000',19.00),
(65,'Trắng / gỗ','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80','Gỗ thông + MDF sơn an toàn',6990000.00,NULL,32,NULL,'206x106x75cm',4,NULL,'2026-06-06 09:00:00.000000',38.00),
(66,'Xanh pastel','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80','Gỗ thông + MDF sơn an toàn',7290000.00,NULL,32,NULL,'206x106x75cm',4,NULL,'2026-06-06 09:00:00.000000',38.00),
(67,'Sồi sáng','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80','MDF phủ melamine + nệm da PU',3290000.00,NULL,33,NULL,'100x35x90cm',8,NULL,'2026-06-06 09:00:00.000000',28.00),
(68,'Vân óc chó','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80','MDF phủ vân gỗ + nệm da PU',3590000.00,NULL,33,NULL,'110x35x90cm',8,NULL,'2026-06-06 09:00:00.000000',30.00),
(69,'Trắng','2026-06-06 09:00:00.000000','https://image1.shopserve.jp/ecmeubles.com/pic-labo/llimg/sf-0105_00.jpg','MDF chống ẩm',3190000.00,NULL,34,NULL,'60x40x180cm',6,NULL,'2026-06-06 09:00:00.000000',26.00),
(70,'Xám nhạt','2026-06-06 09:00:00.000000','https://image1.shopserve.jp/ecmeubles.com/pic-labo/llimg/sf-0105_02.jpg?t=20211102163559','MDF chống ẩm phủ melamine',3390000.00,NULL,34,NULL,'70x40x180cm',6,NULL,'2026-06-06 09:00:00.000000',29.00),
(71,'Trắng phẳng','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80','MDF phủ melamine',6290000.00,NULL,35,NULL,'120x45x200cm',5,NULL,'2026-06-06 09:00:00.000000',58.00),
(72,'Gỗ sồi','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80','MDF phủ vân sồi',6790000.00,NULL,35,NULL,'140x45x200cm',4,NULL,'2026-06-06 09:00:00.000000',64.00),
(73,'Trắng','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80','Gỗ ép phủ laminate',2690000.00,NULL,36,NULL,'90x60x75cm',7,NULL,'2026-06-06 09:00:00.000000',17.00),
(74,'Vân óc chó','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800&fit=crop&q=80','Gỗ ép phủ veneer vân óc chó',2990000.00,NULL,36,NULL,'100x60x75cm',7,NULL,'2026-06-06 09:00:00.000000',19.00),
(75,'Be nhạt line','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80','Vải in tranh + khung gỗ thông',990000.00,NULL,37,NULL,'3 tấm 40x60cm',12,NULL,'2026-06-06 09:00:00.000000',3.00),
(76,'Đỏ đất','2026-06-06 09:00:00.000000','https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80','Vải in tranh + khung gỗ thông',1090000.00,NULL,37,NULL,'3 tấm 40x60cm',12,NULL,'2026-06-06 09:00:00.000000',3.00);
UPDATE `product_variants`
SET `color` = CASE `variant_id`
    WHEN 1 THEN 'Be nhạt'
    WHEN 3 THEN 'Xanh cổ vịt'
    WHEN 5 THEN 'Xám nhạt'
    WHEN 6 THEN 'Xanh than'
    WHEN 7 THEN 'Nâu vân óc chó'
    WHEN 8 THEN 'Đen mun'
    WHEN 13 THEN 'Sồi tự nhiên'
    WHEN 14 THEN 'Sồi mở rộng'
    WHEN 21 THEN 'Sồi sáng'
    WHEN 22 THEN 'Óc chó đậm'
    WHEN 25 THEN 'Vân óc chó'
    WHEN 28 THEN 'Vân óc chó'
    WHEN 29 THEN 'Đen / vân óc chó'
    WHEN 34 THEN 'Xanh xám'
    WHEN 38 THEN 'Vân óc chó / chân đen'
    WHEN 41 THEN 'Be / kem'
    WHEN 50 THEN 'Trắng / sồi'
    WHEN 52 THEN 'Vân óc chó'
    WHEN 55 THEN 'Xám vải lanh'
    WHEN 56 THEN 'Be nhạt'
    WHEN 58 THEN 'Vân óc chó'
    WHEN 62 THEN 'Xanh ô liu'
    WHEN 63 THEN 'Gỗ keo'
    WHEN 67 THEN 'Sồi sáng'
    WHEN 68 THEN 'Vân óc chó'
    WHEN 74 THEN 'Vân óc chó'
    WHEN 75 THEN 'Be nét mảnh'
    WHEN 76 THEN 'Đỏ đất'
    ELSE `color`
  END,
  `material` = CASE `variant_id`
    WHEN 5 THEN 'Vải nỉ cao cấp + khung gỗ'
    WHEN 6 THEN 'Vải nỉ cao cấp + khung gỗ'
    WHEN 7 THEN 'Gỗ óc chó phủ veneer'
    WHEN 17 THEN 'Gỗ ép uốn cong + chân gỗ'
    WHEN 18 THEN 'Gỗ ép sơn đen + chân gỗ'
    WHEN 25 THEN 'Gỗ MDF phủ vân óc chó'
    WHEN 26 THEN 'Gỗ MDF phủ melamine trắng'
    WHEN 38 THEN 'Gỗ MDF vân gỗ + khung thép'
    WHEN 43 THEN 'Mây nhựa PE + khung nhôm'
    WHEN 44 THEN 'Mây nhựa PE + khung nhôm'
    WHEN 45 THEN 'Gỗ tếch nguyên khối'
    WHEN 46 THEN 'Gỗ tếch + đệm ngoài trời'
    WHEN 57 THEN 'Gỗ MDF chống ẩm phủ bóng mờ'
    WHEN 58 THEN 'Gỗ MDF chống ẩm phủ vân gỗ'
    WHEN 61 THEN 'Thép sơn tĩnh điện + mặt chống nước'
    WHEN 62 THEN 'Thép sơn tĩnh điện + mặt chống nước'
    WHEN 63 THEN 'Gỗ keo + đệm vải ngoài trời'
    WHEN 64 THEN 'Gỗ keo sơn xám + đệm'
    WHEN 67 THEN 'Gỗ MDF phủ melamine + nệm da PU'
    WHEN 68 THEN 'Gỗ MDF phủ vân gỗ + nệm da PU'
    WHEN 71 THEN 'Gỗ MDF phủ melamine'
    WHEN 72 THEN 'Gỗ MDF phủ vân sồi'
    WHEN 73 THEN 'Gỗ ép phủ laminate'
    WHEN 74 THEN 'Gỗ ép phủ veneer óc chó'
    WHEN 75 THEN 'Vải in tranh + khung gỗ thông'
    WHEN 76 THEN 'Vải in tranh + khung gỗ thông'
    ELSE `material`
  END,
  `image_url` = CASE `variant_id`
    WHEN 5 THEN 'https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800&fit=crop&q=80'
    WHEN 11 THEN 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80'
    WHEN 12 THEN 'https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?w=800&fit=crop&q=80'
    WHEN 25 THEN 'https://gvawood.com/cdn/shop/files/aa1d659b4486b0be2765947a8253e324_1a883734-b658-4034-b5d1-c2418a7659f9.png?crop=center&height=1200&v=1760696756&width=1200'
    WHEN 26 THEN 'https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=800&fit=crop&q=80'
    WHEN 29 THEN 'https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80'
    WHEN 30 THEN 'https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80'
    WHEN 35 THEN 'https://images.unsplash.com/photo-1606170033648-5d55a3edf314?w=800&fit=crop&q=80'
    WHEN 36 THEN 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80'
    WHEN 49 THEN 'https://foter.com/photos/425/rolling-kitchen-island-with-folding-leaf-and-cart-handles.jpeg?s=lbx'
    WHEN 50 THEN 'https://images.unsplash.com/photo-1556911220-bff31c812dba?w=800&fit=crop&q=80'
    WHEN 51 THEN 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80'
    WHEN 52 THEN 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80'
    WHEN 53 THEN 'https://images-na.ssl-images-amazon.com/images/I/71sJ1BfzANL._AC_SL1500_.jpg'
    WHEN 54 THEN 'https://image1.rank-king.jp/article/original/75204.webp'
    WHEN 57 THEN 'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=800&fit=crop&q=80'
    WHEN 58 THEN 'https://images.unsplash.com/photo-1556911220-bff31c812dba?w=800&fit=crop&q=80'
    WHEN 59 THEN 'https://images.unsplash.com/photo-1620626011761-996317b8d101?w=800&fit=crop&q=80'
    WHEN 60 THEN 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=800&fit=crop&q=80'
    WHEN 61 THEN 'https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80'
    WHEN 62 THEN 'https://images.unsplash.com/photo-1600210492493-0946911123ea?w=800&fit=crop&q=80'
    WHEN 63 THEN 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80'
    WHEN 64 THEN 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&fit=crop&q=80'
    WHEN 65 THEN 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80'
    WHEN 66 THEN 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800&fit=crop&q=80'
    WHEN 67 THEN 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80'
    WHEN 68 THEN 'https://images.unsplash.com/photo-1618220179428-22790b461013?w=800&fit=crop&q=80'
    WHEN 69 THEN 'https://image1.shopserve.jp/ecmeubles.com/pic-labo/llimg/sf-0105_00.jpg'
    WHEN 70 THEN 'https://image1.shopserve.jp/ecmeubles.com/pic-labo/llimg/sf-0105_02.jpg?t=20211102163559'
    WHEN 71 THEN 'https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80'
    WHEN 72 THEN 'https://images.unsplash.com/photo-1594620302200-9a762244a156?w=800&fit=crop&q=80'
    WHEN 73 THEN 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800&fit=crop&q=80'
    WHEN 74 THEN 'https://images.unsplash.com/photo-1593642632559-0c6d3fc62b89?w=800&fit=crop&q=80'
    WHEN 75 THEN 'https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80'
    WHEN 76 THEN 'https://images.unsplash.com/photo-1600166898405-da9535204843?w=800&fit=crop&q=80'
    ELSE `image_url`
  END;
UPDATE `products` p
SET p.review_count = (SELECT COUNT(*) FROM `product_reviews` r WHERE r.product_id = p.product_id),
    p.average_rating = COALESCE((SELECT ROUND(AVG(r.rating), 2) FROM `product_reviews` r WHERE r.product_id = p.product_id), p.average_rating);

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
INSERT INTO `return_requests` VALUES (1,'Đổi trả được chấp nhận, hoàn tiền sau khi kiểm tra sản phẩm.','2026-06-04 10:00:00.000000','furniture/returns/coffee-table-scratch','IMAGE','https://images.unsplash.com/photo-1533090481720-856c6e3c1fdc?w=800&fit=crop&q=80',2,2,'Mặt bàn bị xước nhẹ ở cạnh khi nhận hàng','APPROVED','2026-06-04 15:30:00.000000',6),(2,'Sản phẩm nằm trong sai số kích thước công bố, hỗ trợ voucher lần mua sau.','2026-06-05 09:20:00.000000',NULL,NULL,NULL,8,9,'Ghế cao hơn bàn làm việc hiện tại của gia đình','REJECTED','2026-06-05 13:10:00.000000',10);
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
INSERT INTO `shops` VALUES (1,'42 Nguyễn Huệ, Quận 1, TP.HCM','https://images.unsplash.com/photo-1615873968403-89e068629265?w=1200','2026-01-20 11:19:29.298916','Cửa hàng nội thất demo cho ứng dụng single-shop',1200,'https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=200',1,4.70,'Fur Home','ACTIVE',23,'2026-06-06 09:00:00.000000',50000);
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
INSERT INTO `sub_orders` VALUES (1,'2026-05-20 09:10:00.000000',1,30000.00,1,'DELIVERED',12990000.00,'2026-05-23 16:20:00.000000'),(2,'2026-05-22 10:15:00.000000',2,0.00,1,'DELIVERED',2590000.00,'2026-05-25 14:10:00.000000'),(3,'2026-05-24 11:30:00.000000',3,50000.00,1,'DELIVERED',16490000.00,'2026-05-28 15:00:00.000000'),(4,'2026-05-26 14:20:00.000000',4,70000.00,1,'PROCESSING',15990000.00,'2026-05-27 09:00:00.000000'),(5,'2026-05-28 15:40:00.000000',5,30000.00,1,'PENDING',1490000.00,'2026-05-28 15:40:00.000000'),(6,'2026-05-29 16:05:00.000000',6,30000.00,1,'CANCELLED',12990000.00,'2026-05-29 17:00:00.000000'),(7,'2026-05-30 09:45:00.000000',7,80000.00,1,'SHIPPED',17490000.00,'2026-06-01 10:30:00.000000'),(8,'2026-06-01 13:15:00.000000',8,0.00,1,'DELIVERED',5990000.00,'2026-06-03 11:20:00.000000'),(9,'2026-06-03 18:05:00.000000',9,30000.00,1,'PENDING',9200000.00,'2026-06-03 18:05:00.000000'),(10,'2026-06-04 09:25:00.000000',10,50000.00,1,'CANCELLED',5990000.00,'2026-06-04 12:00:00.000000');
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
INSERT INTO `user_roles` VALUES (1,3),(5,1),(6,1),(9,3),(10,1),(11,1),(12,1);
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
INSERT INTO `users` VALUES (1,'LOCAL','2026-01-20 03:33:04.619540','1998-01-12','admin@fur.vn','Quản trị','OTHER',NULL,_binary '1',NULL,'Hệ thống',NULL,0,'$2a$10$5mIWLezC.UrkTjKxZu3w3eD1NrOIDdG.elwStMJaH1z2JNRakZCr.','0901000000',NULL,NULL,NULL,NULL,'ACTIVE','2026-06-06 09:00:00.000000','admin'),(5,'LOCAL','2026-04-02 01:09:08.916859','1999-04-18','linh@fur.vn','Linh','FEMALE',NULL,_binary '1',NULL,'Trần',NULL,0,'$2a$10$5mIWLezC.UrkTjKxZu3w3eD1NrOIDdG.elwStMJaH1z2JNRakZCr.','0901000001','https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&fit=crop&q=80',NULL,NULL,NULL,'ACTIVE','2026-06-06 09:05:00.000000','linhtran'),(6,'LOCAL','2026-05-05 08:31:44.914858','2000-11-02','an@fur.vn','An','MALE',NULL,_binary '1',NULL,'Nguyễn',NULL,0,'$2a$10$5mIWLezC.UrkTjKxZu3w3eD1NrOIDdG.elwStMJaH1z2JNRakZCr.','0901000002','https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&fit=crop&q=80',NULL,NULL,NULL,'ACTIVE','2026-06-06 09:06:00.000000','annguyen'),(9,'LOCAL','2026-05-06 06:34:25.755758','1995-06-20','ops@fur.vn','Vận hành','OTHER',NULL,_binary '1',NULL,'Cửa hàng',NULL,0,'$2a$10$5mIWLezC.UrkTjKxZu3w3eD1NrOIDdG.elwStMJaH1z2JNRakZCr.','0901000009',NULL,NULL,NULL,NULL,'ACTIVE','2026-06-06 09:07:00.000000','ops_admin'),(10,'LOCAL','2026-05-06 06:34:26.042834','1997-09-08','minh@fur.vn','Minh','MALE',NULL,_binary '1',NULL,'Phạm',NULL,0,'$2a$10$5mIWLezC.UrkTjKxZu3w3eD1NrOIDdG.elwStMJaH1z2JNRakZCr.','0901000003','https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&fit=crop&q=80',NULL,NULL,NULL,'ACTIVE','2026-06-06 09:08:00.000000','minhpham'),(11,'LOCAL','2026-05-14 17:25:22.077780','2001-03-15','phuong@fur.vn','Phương','FEMALE',NULL,_binary '1',NULL,'Mai',NULL,0,'$2a$10$5mIWLezC.UrkTjKxZu3w3eD1NrOIDdG.elwStMJaH1z2JNRakZCr.','0901000004','https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=400&fit=crop&q=80',NULL,NULL,NULL,'ACTIVE','2026-06-06 09:09:00.000000','phuongmai'),(12,'LOCAL','2026-05-16 09:49:19.878651','1996-12-22','nghia@fur.vn','Nghĩa','MALE',NULL,_binary '1',NULL,'Lê',NULL,0,'$2a$10$5mIWLezC.UrkTjKxZu3w3eD1NrOIDdG.elwStMJaH1z2JNRakZCr.','0901000005','https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=400&fit=crop&q=80',NULL,NULL,NULL,'ACTIVE','2026-06-06 09:10:00.000000','nghiale');
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
INSERT INTO `wishlists` VALUES (1,'2026-06-02 09:00:00.000000',1,5),(2,'2026-06-02 09:05:00.000000',11,5),(3,'2026-06-02 09:10:00.000000',20,6),(4,'2026-06-02 09:15:00.000000',22,10),(5,'2026-06-02 09:20:00.000000',10,12);
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

-- ============================================================
-- Auto cleanup after restoring legacy dump.
-- This keeps seed/demo data, then migrates to the single-shop schema.
-- ============================================================

-- Single-shop cleanup migration.
-- Run after backing up furniture_db.

USE furniture_db;

DROP PROCEDURE IF EXISTS drop_fk_for_column;
DROP PROCEDURE IF EXISTS add_column_if_missing;
DROP PROCEDURE IF EXISTS drop_column_if_exists;
DROP PROCEDURE IF EXISTS add_fk_if_missing;
DROP PROCEDURE IF EXISTS fail_if_order_items_missing_order;

DELIMITER //

CREATE PROCEDURE drop_fk_for_column(IN table_name_in VARCHAR(64), IN column_name_in VARCHAR(64))
BEGIN
    DECLARE fk_name VARCHAR(64);

    SELECT kcu.CONSTRAINT_NAME
      INTO fk_name
      FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
     WHERE kcu.TABLE_SCHEMA = DATABASE()
       AND kcu.TABLE_NAME = table_name_in
       AND kcu.COLUMN_NAME = column_name_in
       AND kcu.REFERENCED_TABLE_NAME IS NOT NULL
     LIMIT 1;

    IF fk_name IS NOT NULL THEN
        SET @sql_text = CONCAT('ALTER TABLE `', table_name_in, '` DROP FOREIGN KEY `', fk_name, '`');
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE add_column_if_missing(
    IN table_name_in VARCHAR(64),
    IN column_name_in VARCHAR(64),
    IN column_def_in TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = table_name_in
           AND COLUMN_NAME = column_name_in
    ) THEN
        SET @sql_text = CONCAT('ALTER TABLE `', table_name_in, '` ADD COLUMN `', column_name_in, '` ', column_def_in);
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE drop_column_if_exists(IN table_name_in VARCHAR(64), IN column_name_in VARCHAR(64))
BEGIN
    IF EXISTS (
        SELECT 1
          FROM INFORMATION_SCHEMA.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = table_name_in
           AND COLUMN_NAME = column_name_in
    ) THEN
        SET @sql_text = CONCAT('ALTER TABLE `', table_name_in, '` DROP COLUMN `', column_name_in, '`');
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE add_fk_if_missing(
    IN table_name_in VARCHAR(64),
    IN fk_name_in VARCHAR(64),
    IN column_name_in VARCHAR(64),
    IN referenced_table_in VARCHAR(64),
    IN referenced_column_in VARCHAR(64)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
          FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = table_name_in
           AND CONSTRAINT_NAME = fk_name_in
    ) THEN
        SET @sql_text = CONCAT(
            'ALTER TABLE `', table_name_in, '` ADD CONSTRAINT `', fk_name_in,
            '` FOREIGN KEY (`', column_name_in, '`) REFERENCES `',
            referenced_table_in, '` (`', referenced_column_in, '`)'
        );
        PREPARE stmt FROM @sql_text;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//

CREATE PROCEDURE fail_if_order_items_missing_order()
BEGIN
    IF EXISTS (
        SELECT 1
          FROM order_items
         WHERE order_id IS NULL
         LIMIT 1
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot migrate order_items: some rows have no matching sub_orders.order_id';
    END IF;
END//

DELIMITER ;

START TRANSACTION;

DELETE ur
  FROM user_roles ur
  JOIN roles r ON ur.role_id = r.role_id
 WHERE r.role_name NOT IN ('CUSTOMER', 'ADMIN');

DELETE FROM roles
 WHERE role_name NOT IN ('CUSTOMER', 'ADMIN');

CALL add_column_if_missing('orders', 'recipient_name', 'VARCHAR(100) NULL');
CALL add_column_if_missing('orders', 'recipient_phone', 'VARCHAR(15) NULL');
CALL add_column_if_missing('orders', 'shipping_address_text', 'VARCHAR(500) NULL');

UPDATE orders o
  JOIN addresses a ON a.address_id = o.shipping_address_id
   SET o.recipient_name = COALESCE(o.recipient_name, a.recipient_name),
       o.recipient_phone = COALESCE(o.recipient_phone, a.phone),
       o.shipping_address_text = COALESCE(
           o.shipping_address_text,
           TRIM(BOTH ', ' FROM CONCAT_WS(', ', a.address_line, a.ward, a.district, a.city))
       );

ALTER TABLE orders MODIFY shipping_address_id INT NULL;
ALTER TABLE orders MODIFY payment_method ENUM('COD','BANK_TRANSFER') NOT NULL;

ALTER TABLE payments MODIFY payment_method ENUM('COD','BANK_TRANSFER') NOT NULL;

CALL add_column_if_missing('order_items', 'order_id', 'INT NULL');

UPDATE order_items oi
  JOIN sub_orders so ON so.sub_order_id = oi.sub_order_id
   SET oi.order_id = so.order_id
 WHERE oi.order_id IS NULL;

CALL fail_if_order_items_missing_order();

ALTER TABLE order_items MODIFY order_id INT NOT NULL;
CALL drop_fk_for_column('order_items', 'sub_order_id');
CALL drop_column_if_exists('order_items', 'sub_order_id');
CALL add_fk_if_missing('order_items', 'fk_order_items_orders', 'order_id', 'orders', 'order_id');

CALL drop_fk_for_column('products', 'shop_id');
CALL drop_column_if_exists('products', 'shop_id');

CALL drop_fk_for_column('cart_items', 'shop_id');
CALL drop_column_if_exists('cart_items', 'shop_id');

DROP TABLE IF EXISTS user_coupons;
DROP TABLE IF EXISTS coupons;
DROP TABLE IF EXISTS shipments;
DROP TABLE IF EXISTS shippers;
DROP TABLE IF EXISTS shop_reviews;
DROP TABLE IF EXISTS sub_orders;
DROP TABLE IF EXISTS shops;

COMMIT;

DROP PROCEDURE IF EXISTS drop_fk_for_column;
DROP PROCEDURE IF EXISTS add_column_if_missing;
DROP PROCEDURE IF EXISTS drop_column_if_exists;
DROP PROCEDURE IF EXISTS add_fk_if_missing;
DROP PROCEDURE IF EXISTS fail_if_order_items_missing_order;














