CREATE SCHEMA IF NOT EXISTS promenad;
USE promenad;
DROP TABLE  IF EXISTS geolocation;
CREATE TABLE geolocation
(

    id        BIGINT AUTO_INCREMENT NOT NULL,
    full_name  VARCHAR(255),
    latitude  DOUBLE,
    longitude DOUBLE,
    PRIMARY KEY (id)
);