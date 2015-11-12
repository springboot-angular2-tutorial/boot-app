CREATE TABLE `user` (
  `id`       BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `name`     VARCHAR(30)  NOT NULL,
  `password` VARCHAR(100) NOT NULL,
  `username` VARCHAR(30)  NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_username` (`username`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `micropost` (
  `id`         BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `content`    VARCHAR(255) NOT NULL,
  `created_at` DATETIME     NOT NULL,
  `user_id`    BIGINT(20)   NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_user` (`user_id`),
  CONSTRAINT `FK_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE `relationship` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `followed_id` BIGINT(20) NOT NULL,
  `follower_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_followed` (`followed_id`),
  KEY `FK_follower` (`follower_id`),
  CONSTRAINT `FK_follower` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_followed` FOREIGN KEY (`followed_id`) REFERENCES `user` (`id`),
  UNIQUE KEY `UK_follower_followed` (`follower_id`, `followed_id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

