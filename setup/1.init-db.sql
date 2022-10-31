CREATE SCHEMA IF NOT EXISTS `movie_rama` DEFAULT CHARACTER SET utf8 COLLATE utf8_bin;
USE `movie_rama`;

DROP TABLE IF EXISTS `movie_rama`.`account`;
CREATE TABLE IF NOT EXISTS `movie_rama`.`account`
(
    `id`         bigint(11)   NOT NULL AUTO_INCREMENT,
    `password`   VARCHAR(255) NOT NULL,
    `username`   VARCHAR(64)  NOT NULL,
    `first_name` VARCHAR(45)  NOT NULL,
    `last_name`  VARCHAR(45)  NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `username_uidx` (`username` ASC)
) ENGINE = InnoDB;


DROP TABLE IF EXISTS `movie_rama`.`movie`;
CREATE TABLE IF NOT EXISTS `movie_rama`.`movie`
(
    `id`           BIGINT(11)   NOT NULL AUTO_INCREMENT,
    `account_id`   BIGINT(11)   NOT NULL,
    `title`        VARCHAR(255) NOT NULL,
    `description`  TEXT         NOT NULL,
    `created_date` DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_account_id_idx` (`account_id` ASC),
    CONSTRAINT `fk_movie_account_account_id`
        FOREIGN KEY (`account_id`)
            REFERENCES `movie_rama`.`account` (`id`)
            ON DELETE RESTRICT
            ON UPDATE NO ACTION
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `movie_rama`.`account_movie_likes`;
CREATE TABLE IF NOT EXISTS `movie_rama`.`account_movie_likes`
(
    movie_id   BIGINT(11) NOT NULL,
    account_id BIGINT(11) NOT NULL,
    PRIMARY KEY (movie_id, account_id),
    CONSTRAINT `fk_account_movie_likes_account_id`
        FOREIGN KEY (`account_id`)
            REFERENCES `movie_rama`.`account` (`id`),
    CONSTRAINT `fk_movie_movie_likes_movie_id`
        FOREIGN KEY (`movie_id`)
            REFERENCES `movie_rama`.`movie` (`id`)
) ENGINE = InnoDB;

DROP TABLE IF EXISTS `movie_rama`.`account_movie_dislikes`;
CREATE TABLE IF NOT EXISTS `movie_rama`.`account_movie_dislikes`
(
    movie_id   BIGINT(11) NOT NULL,
    account_id BIGINT(11) NOT NULL,
    PRIMARY KEY (movie_id, account_id),
    CONSTRAINT `fk_account_movie_dislikes_account_id`
        FOREIGN KEY (`account_id`)
            REFERENCES `movie_rama`.`account` (`id`),
    CONSTRAINT `fk_movie_movie_dislikes_movie_id`
        FOREIGN KEY (`movie_id`)
            REFERENCES `movie_rama`.`movie` (`id`)
) ENGINE = InnoDB;