-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema soundcloud
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema soundcloud
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `soundcloud` DEFAULT CHARACTER SET utf8 ;
USE `soundcloud` ;

-- -----------------------------------------------------
-- Table `soundcloud`.`descriptions`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`descriptions` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `content` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 27
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(200) NOT NULL,
  `age` INT NOT NULL,
  `gender` VARCHAR(1) NOT NULL,
  `created_at` DATETIME NOT NULL,
  `profile_picture_url` VARCHAR(200) NULL DEFAULT NULL,
  `enabled` TINYINT NOT NULL DEFAULT '0',
  `last_active` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`songs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`songs` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `owner_id` INT NOT NULL,
  `uploaded_at` DATETIME NOT NULL,
  `views` INT NOT NULL DEFAULT '0',
  `song_url` VARCHAR(200) NULL DEFAULT NULL,
  `cover_photo_url` VARCHAR(200) NULL DEFAULT NULL,
  `is_public` TINYINT NOT NULL,
  `description_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `songs_users_fk_idx` (`owner_id` ASC) VISIBLE,
  INDEX `songs_descriptions_fk_idx` (`description_id` ASC) VISIBLE,
  CONSTRAINT `songs_descriptions_fk`
    FOREIGN KEY (`description_id`)
    REFERENCES `soundcloud`.`descriptions` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `songs_users_fk`
    FOREIGN KEY (`owner_id`)
    REFERENCES `soundcloud`.`users` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 19
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`comments` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `song_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `content` TEXT NOT NULL,
  `posted_at` DATETIME NOT NULL,
  `parent_comment_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `comments_songs_fk_idx` (`song_id` ASC) VISIBLE,
  INDEX `comments_users_fk_idx` (`user_id` ASC) VISIBLE,
  INDEX `comment_parent_comment_fk_idx` (`parent_comment_id` ASC) VISIBLE,
  CONSTRAINT `comment_parent_comment_fk`
    FOREIGN KEY (`parent_comment_id`)
    REFERENCES `soundcloud`.`comments` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `comments_songs_fk`
    FOREIGN KEY (`song_id`)
    REFERENCES `soundcloud`.`songs` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `comments_users_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `soundcloud`.`users` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 14
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`tags` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`descriptions_have_tags`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`descriptions_have_tags` (
  `description_id` INT NOT NULL,
  `tag_id` INT NOT NULL,
  PRIMARY KEY (`description_id`, `tag_id`),
  INDEX `dht_tags_fk_idx` (`tag_id` ASC) VISIBLE,
  CONSTRAINT `dht_descriptions_fk`
    FOREIGN KEY (`description_id`)
    REFERENCES `soundcloud`.`descriptions` (`id`),
  CONSTRAINT `dht_tags_fk`
    FOREIGN KEY (`tag_id`)
    REFERENCES `soundcloud`.`tags` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`playlists`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`playlists` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL,
  `owner_id` INT NOT NULL,
  `created_at` DATETIME NOT NULL,
  `last_modified` DATETIME NOT NULL,
  `cover_photo_url` VARCHAR(200) NULL DEFAULT NULL,
  `is_public` TINYINT NOT NULL,
  `description_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `playlists_users_fk_idx` (`owner_id` ASC) VISIBLE,
  INDEX `playlists_descriptions_fk_idx` (`description_id` ASC) VISIBLE,
  CONSTRAINT `playlists_descriptions_fk`
    FOREIGN KEY (`description_id`)
    REFERENCES `soundcloud`.`descriptions` (`id`),
  CONSTRAINT `playlists_users_fk`
    FOREIGN KEY (`owner_id`)
    REFERENCES `soundcloud`.`users` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`playlists_have_songs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`playlists_have_songs` (
  `playlist_id` INT NOT NULL,
  `song_id` INT NOT NULL,
  PRIMARY KEY (`playlist_id`, `song_id`),
  INDEX `phs_songs_fk_idx` (`song_id` ASC) VISIBLE,
  CONSTRAINT `phs_playlists_fk`
    FOREIGN KEY (`playlist_id`)
    REFERENCES `soundcloud`.`playlists` (`id`),
  CONSTRAINT `phs_songs_fk`
    FOREIGN KEY (`song_id`)
    REFERENCES `soundcloud`.`songs` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`users_follow_users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`users_follow_users` (
  `followed_id` INT NOT NULL,
  `follower_id` INT NOT NULL,
  PRIMARY KEY (`followed_id`, `follower_id`),
  INDEX `ufu_follower_fk_idx` (`follower_id` ASC) VISIBLE,
  CONSTRAINT `ufu_followed_fk`
    FOREIGN KEY (`followed_id`)
    REFERENCES `soundcloud`.`users` (`id`),
  CONSTRAINT `ufu_follower_fk`
    FOREIGN KEY (`follower_id`)
    REFERENCES `soundcloud`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`users_like_comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`users_like_comments` (
  `user_id` INT NOT NULL,
  `comment_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `comment_id`),
  INDEX `ulc_comments_fk_idx` (`comment_id` ASC) VISIBLE,
  CONSTRAINT `ulc_comments_fk`
    FOREIGN KEY (`comment_id`)
    REFERENCES `soundcloud`.`comments` (`id`),
  CONSTRAINT `ulc_users_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `soundcloud`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`users_like_playlists`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`users_like_playlists` (
  `user_id` INT NOT NULL,
  `playlist_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `playlist_id`),
  INDEX `ULK_playlists_fk_idx` (`playlist_id` ASC) VISIBLE,
  CONSTRAINT `ULK_playlists_fk`
    FOREIGN KEY (`playlist_id`)
    REFERENCES `soundcloud`.`playlists` (`id`),
  CONSTRAINT `ULK_users_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `soundcloud`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`users_like_songs`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`users_like_songs` (
  `user_id` INT NOT NULL,
  `song_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `song_id`),
  INDEX `uls_songs_fk_idx` (`song_id` ASC) VISIBLE,
  CONSTRAINT `uls_songs_fk`
    FOREIGN KEY (`song_id`)
    REFERENCES `soundcloud`.`songs` (`id`),
  CONSTRAINT `uls_users_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `soundcloud`.`users` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb3;


-- -----------------------------------------------------
-- Table `soundcloud`.`verification_tokens`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `soundcloud`.`verification_tokens` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `token` VARCHAR(200) NOT NULL,
  `user_id` INT NOT NULL,
  `expiry_time` DATETIME NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `token_UNIQUE` (`token` ASC) VISIBLE,
  INDEX `vt_users_fk_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `vt_users_fk`
    FOREIGN KEY (`user_id`)
    REFERENCES `soundcloud`.`users` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
AUTO_INCREMENT = 10
DEFAULT CHARACTER SET = utf8mb3;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
