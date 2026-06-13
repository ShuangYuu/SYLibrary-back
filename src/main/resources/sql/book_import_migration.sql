ALTER TABLE `book`
    ADD COLUMN `source_platform` VARCHAR(50) NULL COMMENT '来源平台，例如 openLibrary、googleBooks' AFTER `comment`,
    ADD COLUMN `source_id` VARCHAR(100) NULL COMMENT '外部平台书籍 ID' AFTER `source_platform`,
    ADD COLUMN `publisher` VARCHAR(255) NULL COMMENT '出版社' AFTER `isbn`,
    ADD COLUMN `publish_year` VARCHAR(20) NULL COMMENT '出版年份' AFTER `publisher`,
    ADD COLUMN `description` TEXT NULL COMMENT '详细简介' AFTER `publish_year`;

ALTER TABLE `book`
    ADD UNIQUE KEY `uk_book_source` (`source_platform`, `source_id`);
