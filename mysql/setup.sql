CREATE USER 'domcretan'@'%' IDENTIFIED BY 'admin_root_kyr1';
GRANT ALL ON *.* TO 'domcretan'@'%' WITH GRANT OPTION;
CREATE DATABASE dvd_inventory;
USE dvd_inventory;
CREATE TABLE dvd (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(50) NOT NULL,
  director VARCHAR(50) NOT NULL,
  genre VARCHAR(20) NOT NULL,
  release_date DATE,
  summary VARCHAR(100),
  language VARCHAR(15)
);
CREATE TABLE user (
  username VARCHAR(20),
  password VARCHAR(20)
);
INSERT INTO user VALUES ('employee', 'employee1');
