--liquibase formatted sql
--changeset julio:202508242332
--comment: boards table create
CREATE TABLE BOARDS(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
) ENGINE= InnoDB;
--rollback DROP TABLE BOARDS