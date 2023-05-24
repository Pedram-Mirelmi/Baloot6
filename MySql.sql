DROP DATABASE IF EXISTS Baloot6;
CREATE DATABASE Baloot6;

USE Baloot6;

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    userId             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username            VARCHAR(255) UNIQUE NOT NULL,
    password            VARCHAR(255) NOT NULL ,
    email               VARCHAR(127) NOT NULL ,
    birthDate           DATE NOT NULL ,
    address             TEXT NOT NULL ,
    credit              INT UNSIGNED NOT NULL

);

DROP TABLE IF EXISTS providers;

CREATE TABLE providers
(
    providerId          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY ,
    name                VARCHAR(255) NOT NULL ,
    registryDate        DATE NOT NULL DEFAULT(NOW())
);

DROP TABLE IF EXISTS commodities;

CREATE TABLE commodities
(
    commodityId         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY ,
    name                VARCHAR(255) NOT NULL ,
    providerId          INT UNSIGNED NOT NULL ,
    price               DECIMAL UNSIGNED NOT NULL ,
    inStock             INT UNSIGNED NOT NULL ,
    FOREIGN KEY (providerId) REFERENCES providers(providerId)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS shoppingItems;

CREATE TABLE shoppingItems
(
    shoppingItemId   INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    userId              INT UNSIGNED NOT NULL ,
    commodityId         INT UNSIGNED NOT NULL ,
    count               INT UNSIGNED NOT NULL ,
    beenPurchased       BOOLEAN NOT NULL DEFAULT (FALSE),
    FOREIGN KEY (commodityId) REFERENCES commodities(commodityId)
        ON DELETE CASCADE,
    FOREIGN KEY (userId) REFERENCES users(userId)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS categories;

CREATE TABLE categories
(
    categoryId          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY ,
    categoryName        VARCHAR(255) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS commodities_categories;

CREATE TABLE commodities_categories
(
    commodityId         INT UNSIGNED NOT NULL ,
    categoryId          INT UNSIGNED NOT NULL ,
    UNIQUE (commodityId, categoryId),
    FOREIGN KEY (commodityId) REFERENCES commodities(commodityId)
        ON DELETE CASCADE ,
    FOREIGN KEY (categoryId) REFERENCES categories(categoryId)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS ratings;

CREATE TABLE ratings
(
    ratingId            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY ,
    userId              INT UNSIGNED NOT NULL ,
    commodityId         INT UNSIGNED NOT NULL ,
    rating              FLOAT NOT NULL ,
    UNIQUE (userId, commodityId),
    FOREIGN KEY (userId) REFERENCES users(userId)
        ON DELETE CASCADE ,
    FOREIGN KEY (commodityId) REFERENCES commodities(commodityId)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS discounts;

CREATE TABLE discounts
(
    discountId      INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    discountCode    VARCHAR(63) UNIQUE NOT NULL ,
    discountAmount  INT UNSIGNED NOT NULL
);


DROP TABLE IF EXISTS comments;

CREATE TABLE comments
(
    commentId       INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    userId          INT UNSIGNED NOT NULL ,
    commodityId     INT UNSIGNED NOT NULL ,
    text            VARCHAR(511) NOT NULL ,
    date            DATE NOT NULL DEFAULT(NOW()),
    FOREIGN KEY (userId) REFERENCES users(userId)
        ON DELETE CASCADE ,
    FOREIGN KEY (commodityId) REFERENCES commodities(commodityId)
        ON DELETE CASCADE
);


DROP TABLE IF EXISTS commentsVotes;

CREATE TABLE commentsVotes
(
    userId          INT UNSIGNED NOT NULL ,
    commentId       INT UNSIGNED NOT NULL ,
    vote            INT SIGNED  NOT NULL ,
    UNIQUE (userId, commentId),
    FOREIGN KEY (commentId) REFERENCES comments(commentId)
        ON DELETE CASCADE
);

DROP TABLE IF EXISTS discountUses;

CREATE TABLE discountUses
(
    discountUseId       INT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    discountId          INT UNSIGNED NOT NULL ,
    userId              INT UNSIGNED NOT NULL ,
    UNIQUE (userId, discountId),
    FOREIGN KEY (discountId) REFERENCES discounts(discountId)
        ON DELETE CASCADE ,
    FOREIGN KEY (userId) REFERENCES users(userId)
        ON DELETE CASCADE
);