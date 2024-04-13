# 스키마 생성
DROP SCHEMA IF EXISTS mefi; 
create schema mefi; 
use mefi; 


drop table if exists `user`; 

CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `dept` VARCHAR(255) NULL,
    `position` VARCHAR(255) NULL,
    `created_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `img_url` VARCHAR(255) NULL,
    `token_id` BIGINT NOT NULL, 
	CONSTRAINT PK_USER PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `token`;

CREATE TABLE `token` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `refresh_token` VARCHAR(500) NOT NULL, 
     CONSTRAINT PK_TOKEN PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `conference`;

CREATE TABLE `conference` (
	`id` BIGINT AUTO_INCREMENT NOT NULL,
    `team_id` BIGINT NOT NULL,
    `leader_id` BIGINT NOT NULL,
    `call_start` DATETIME NOT NULL,
    `call_end` DATETIME NOT NULL,
    `thumbnail_url` VARCHAR(255) NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL,
    `status` ENUM('TODO', 'DONE', 'CANCELED') NOT NULL,
    CONSTRAINT PK_CONFERENCE PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `user_conference`;

CREATE TABLE `user_conference` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `conference_id` BIGINT NOT NULL,
    CONSTRAINT PK_USER_CONFERENCE PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `team`;

CREATE TABLE `team` (
	`id` BIGINT AUTO_INCREMENT NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NULL,
    `created_time` DATETIME NOT NULL,
    CONSTRAINT PK_TEAM PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `user_team`;

CREATE TABLE `user_team` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `team_id` BIGINT NOT NULL,
    `role` ENUM('LEADER', 'MEMBER') NOT NULL,
    CONSTRAINT PK_USER_TEAM PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `private_schedule`;

CREATE TABLE `private_schedule` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `started_time` DATETIME NOT NULL,
    `end_time` DATETIME NOT NULL,
    `summary` VARCHAR(255) NOT NULL,
    `description` VARCHAR(255) NOT NULL, 
    `type` ENUM('CONFERENCE', 'BUSINESSTRIP') NOT NULL,
    CONSTRAINT PK_PRIVATE_SCHEDULE PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `notifications`;

CREATE TABLE `notification` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `user_id` BIGINT NOT NULL,
    `message` VARCHAR(255) NOT NULL,
    `status` BOOLEAN NOT NULL,
    `created_time` DATETIME NOT NULL, 
    `sender` VARCHAR(255) NOT NULL,
    CONSTRAINT PK_NOTIFICATION PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `email_auth`;

CREATE TABLE `email_auth` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `email` VARCHAR(255) NOT NULL,
    `random_num` VARCHAR(10) NOT NULL,
    `created_time` DATETIME NOT NULL,
    CONSTRAINT PK_EMAIL_AUTH PRIMARY KEY(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `file`;

CREATE TABLE `file` (
    `id` BIGINT AUTO_INCREMENT NOT NULL,
    `file_name` VARCHAR(255) NULL,
    `conference_id` BIGINT NOT NULL,
    `type` ENUM('DOCUMENT', 'ATTACHMENT') NOT NULL, 
     CONSTRAINT PK_FILE PRIMARY KEY(id)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COLLATE = UTF8MB4_0900_AI_CI;

ALTER TABLE `conference` ADD CONSTRAINT `FK_team_TO_conference_1` FOREIGN KEY (
	`team_id`
)
REFERENCES `team` (
	`id`
);

ALTER TABLE `user_conference` ADD CONSTRAINT `FK_user_TO_teamnotificationnotificationuser_conference_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`id`
);

ALTER TABLE `user_conference` ADD CONSTRAINT `FK_conference_TO_user_conference_1` FOREIGN KEY (
	`conference_id`
)
REFERENCES `conference` (
	`id`
);

ALTER TABLE `user_team` ADD CONSTRAINT `FK_user_TO_user_team_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`id`
);

ALTER TABLE `user_team` ADD CONSTRAINT `FK_team_TO_user_team_1` FOREIGN KEY (
	`team_id`
)
REFERENCES `team` (
	`id`
);

ALTER TABLE `private_schedule` ADD CONSTRAINT `FK_user_TO_private_schedule_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`id`
);

ALTER TABLE `notification` ADD CONSTRAINT `FK_user_TO_notification_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `user` (
	`id`
);

ALTER TABLE `user` ADD CONSTRAINT `FK_token_TO_user_1` FOREIGN KEY (
	`token_id`
)
REFERENCES `token` (
	`id`
);

ALTER TABLE `file` ADD CONSTRAINT `FK_conference_TO_file_1` FOREIGN KEY (
	`conference_id`
)
REFERENCES `conference` (
	`id`
);
