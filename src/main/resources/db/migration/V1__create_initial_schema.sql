CREATE TABLE `users` (
                         `id` int NOT NULL AUTO_INCREMENT,
                         `email` varchar(150) NOT NULL,
                         `name` varchar(100) NOT NULL,
                         `password` varchar(255) NOT NULL,
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


CREATE TABLE `expense` (
                           `id` int NOT NULL AUTO_INCREMENT,
                           `amount` decimal(10,2) NOT NULL,
                           `category` varchar(255) NOT NULL,
                           `description` varchar(255) DEFAULT NULL,
                           `expense_date` date NOT NULL,
                           `user_id` int NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci