ALTER TABLE `user`
    ADD UNIQUE KEY `uk_user_phone` (`phone`);

ALTER TABLE `admin`
    ADD UNIQUE KEY `uk_admin_phone` (`phone`);
