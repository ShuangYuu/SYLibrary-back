CREATE TABLE `user_favorite_book` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `user_id` INT NOT NULL,
    `book_id` INT NOT NULL,
    `createdTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_favorite_book` (`user_id`, `book_id`),
    KEY `idx_user_favorite_book_user` (`user_id`),
    KEY `idx_user_favorite_book_book` (`book_id`),
    CONSTRAINT `fk_user_favorite_book_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_favorite_book_book` FOREIGN KEY (`book_id`) REFERENCES `book` (`book_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
