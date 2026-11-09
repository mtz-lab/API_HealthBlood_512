-- ============================================================
-- glucoAPI - Esquema de la base de datos
-- Compatible con MySQL 8 y TiDB Cloud Starter
-- ============================================================

CREATE DATABASE IF NOT EXISTS gluco_api
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

USE gluco_api;

CREATE TABLE IF NOT EXISTS usuario (
  idUsuario       INT NOT NULL AUTO_INCREMENT,
  nombre          VARCHAR(100) NOT NULL,
  primerApellido  VARCHAR(100) DEFAULT NULL,
  segundoApellido VARCHAR(100) DEFAULT NULL,
  email           VARCHAR(150) NOT NULL,
  PRIMARY KEY (idUsuario),
  UNIQUE KEY uk_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS medida (
  idMedida   INT NOT NULL AUTO_INCREMENT,
  idUsuario  INT NOT NULL,
  glucosa    INT NOT NULL,
  fechaHora  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  contexto   VARCHAR(100) DEFAULT NULL,
  notas      VARCHAR(500) DEFAULT NULL,
  PRIMARY KEY (idMedida),
  KEY idx_medida_usuario (idUsuario),
  CONSTRAINT fk_medida_usuario FOREIGN KEY (idUsuario)
    REFERENCES usuario (idUsuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
