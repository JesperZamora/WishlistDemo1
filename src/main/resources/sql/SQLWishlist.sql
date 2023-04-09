-- Wishlist database

-- Create database if no one exist
CREATE DATABASE IF NOT EXISTS wishlist_db;

-- Use databaase
USE wishlist_db;

-- Drop tables if they exist
DROP TABLE IF EXISTS userswishlist;
DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS users;

-- Create new tables
CREATE TABLE users(
user_id INTEGER NOT NULL AUTO_INCREMENT,
first_name VARCHAR(20),
last_name VARCHAR(20),
user_email VARCHAR(50) UNIQUE,
user_password VARCHAR(50),
PRIMARY KEY(user_id)
);

CREATE TABLE wishlist(
wish_id INTEGER NOT NULL AUTO_INCREMENT,
wish_title VARCHAR(50),
wish_description VARCHAR(255),
wish_url VARCHAR(2083),
wish_picture BLOB,
user_id INTEGER,
PRIMARY KEY(wish_id),
FOREIGN KEY(user_id) REFERENCES users(user_id)
);

