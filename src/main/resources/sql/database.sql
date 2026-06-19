CREATE DATABASE IF NOT EXISTS `sylibrary` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `sylibrary`;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `user` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(64) DEFAULT NULL,
    `userImage` VARCHAR(256) DEFAULT NULL,
    `password` VARCHAR(128) DEFAULT NULL,
    `cardID` VARCHAR(64) DEFAULT NULL,
    `age` INT DEFAULT NULL,
    `sex` VARCHAR(8) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `address` VARCHAR(128) DEFAULT NULL,
    `role` VARCHAR(16) DEFAULT 'USER',
    `createdTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_phone` (`phone`),
    KEY `idx_user_created_time` (`createdTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `admin` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(64) DEFAULT NULL,
    `password` VARCHAR(128) DEFAULT NULL,
    `phone` VARCHAR(20) DEFAULT NULL,
    `email` VARCHAR(64) DEFAULT NULL,
    `role` VARCHAR(16) DEFAULT 'ADMIN',
    `createdTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `refreshToken` (
    `token_id` INT NOT NULL AUTO_INCREMENT,
    `id` INT NOT NULL,
    `type` VARCHAR(32) NOT NULL,
    `jti` VARCHAR(36) NOT NULL,
    `role` VARCHAR(16) DEFAULT NULL,
    `createdTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`token_id`),
    KEY `idx_refresh_token_jti_id` (`jti`, `id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `book` (
    `book_id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(80) DEFAULT NULL,
    `author` VARCHAR(64) DEFAULT NULL,
    `imageUrl` VARCHAR(256) DEFAULT NULL,
    `tags` VARCHAR(128) DEFAULT NULL,
    `comment` VARCHAR(256) DEFAULT NULL,
    `source_platform` VARCHAR(50) DEFAULT NULL,
    `source_id` VARCHAR(100) DEFAULT NULL,
    `isbn` VARCHAR(30) DEFAULT NULL,
    `publisher` VARCHAR(255) DEFAULT NULL,
    `publish_year` VARCHAR(20) DEFAULT NULL,
    `description` TEXT DEFAULT NULL,
    `createdTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updateTime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`book_id`),
    UNIQUE KEY `uk_book_source` (`source_platform`, `source_id`),
    KEY `idx_book_isbn` (`isbn`),
    KEY `idx_book_created_time` (`createdTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user_favorite_book` (
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
