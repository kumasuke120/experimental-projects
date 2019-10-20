CREATE SCHEMA test;

CREATE TABLE test.sequences
(
  `name`    VARCHAR(255)    NOT NULL
    PRIMARY KEY,
  `value`   INT DEFAULT '0' NOT NULL,
  `comment` VARCHAR(2048)
);

-- @formatter:off
DELIMITER $$
DROP FUNCTION IF EXISTS test.`seq_nextval` $$
CREATE FUNCTION test.`seq_nextval`(
  _name VARCHAR(255)
)
RETURNS INT
  BEGIN
    UPDATE test.sequences SET `value` = `value` + 1 WHERE `name` = _name;
    SELECT `value`  FROM test.sequences WHERE `name` = _name INTO @retVal;
    RETURN @retVal;
  END $$

DROP FUNCTION IF EXISTS test.`seq_currval` $$
CREATE FUNCTION test.`seq_currval`(
  _name VARCHAR(255)
)
RETURNS INT
  BEGIN
    SELECT `value`  FROM test.sequences WHERE `name` = _name INTO @retVal;
    RETURN @retVal;
  END $$

DROP PROCEDURE IF EXISTS test.`seq_make_3` $$
CREATE PROCEDURE test.`seq_make_3`(
  IN _name    VARCHAR(255),
  IN _value   INT,
  IN _comment VARCHAR(2048)
)
  BEGIN
    INSERT INTO test.sequences (`name`, `value`, `comment`)
      VALUES (_name, _value, _comment);
  END $$

DROP PROCEDURE IF EXISTS test.`seq_make_2` $$
CREATE PROCEDURE test.`seq_make_2`(
  IN _name  VARCHAR(255),
  IN _value INT
)
  BEGIN
    CALL test.seq_make_3(_name, _value, '');
  END $$

DROP PROCEDURE IF EXISTS test.`seq_make` $$
CREATE PROCEDURE test.`seq_make`(
  IN _name VARCHAR(255)
)
  BEGIN
    CALL test.seq_make_2(_name, 0);
  END $$

DROP PROCEDURE IF EXISTS test.`seq_drop` $$
CREATE PROCEDURE test.`seq_drop`(
  IN _name VARCHAR(255)
)
  BEGIN
    DELETE FROM test.sequences WHERE `name` = _name;
  END $$
DELIMITER ;
-- @formatter:on

CREATE TABLE test.books (
  id           INT PRIMARY KEY,
  `name`       VARCHAR(255) NOT NULL,
  author       VARCHAR(200),
  publisher    VARCHAR(120),
  price        DECIMAL(10, 2),
  language     VARCHAR(20),
  pages        INT,
  isbn         CHAR(14),
  release_date DATE
);

CALL test.seq_make('books');

INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'),
        'Fifty Shades of Grey: Book One of the Fifty Shades Trilogy (Fifty Shades of Grey Series)', 'E L James',
        'Vintage Books; 1 edition (April 3, 2012)', 7.34, 'English', 514, '978-0345803481', '2012-04-03');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), 'The Hunger Games (Book 1)', 'Suzanne Collins',
        'Scholastic Press; Reprint edition (July 3, 2010)',
        7.36, 'English', 384, '978-0439023528', '2010-07-03');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), 'StrengthsFinder 2.0', 'Tom Rath', 'Gallup Press; 1 edition (February 1, 2007)',
        17.79, 'English', 175, '978-1595620156', '2007-02-01');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), 'Steve Jobs', 'Walter Isaacson', 'Simon & Schuster; 1 edition (October 24, 2011)',
        11.83, 'English', 656, '978-1451648539', '2011-10-24');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES
  (test.seq_nextval('books'), 'The Fault in Our Stars', 'John Green', 'Penguin Books; Reprint edition (April 8, 2014)',
   7.27, 'English', 352, '978-0142424179', '2014-04-08');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), 'Harry Potter and the Deathly Hallows (Book 7)', 'J. K. Rowling',
        'Arthur A. Levine Books (July 1, 2009)',
        12.38, 'English', 784, '978-0545139700', '2009-07-07');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), 'Asterix - Lateinisch: Asterix latein 15 Olympius - Latin edition', 'Goscinny',
        'French and European Publications Inc (November 20, 1990)',
        23.87, 'Latin', 48, '978-3770400652', '1990-11-20');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES
  (test.seq_nextval('books'), 'Harry Potter Und der Stein der Weisen (German Edition)', 'J. K. Rowling,‎ Klaus Fritz',
   'Carlsen Verlag GmbH (July 1, 2005)', 19.20, 'German', 334, '978-3551354013', '2005-06-01');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), 'Höhere Technische Mechanik: Nach Vorlesungen (German Edition)', 'István Szabó',
        'Springer; 2 edition (January 1, 1958)', 58.36, 'German', 498, '978-3662235058', NULL);
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), 'Code: The Hidden Language of Computer Hardware and Software', 'Charles Petzold',
        'Microsoft Press; 1 edition (October 21, 2000)', 16.37, 'English', 400, '978-0735611313', '2000-10-21');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), '魂でもいいから、そばにいて ─3・11後の霊体験を聞く', '奥野 修司',
        '新潮社 (2017/2/28)', 15.12, '日本語', 256, '978-4104049028', '2017-02-28');
INSERT INTO test.books (id, `name`, author, publisher, price, language, pages, isbn, release_date)
VALUES (test.seq_nextval('books'), '秋霖やまず: 吉原裏同心抄(三) (光文社時代小説文庫)', '佐伯 泰英',
        '光文社 (2018/3/9)', 6.48, '日本語', 314, '978-4334776138', '2018-03-09');

CREATE TABLE test.users (
  id           INT PRIMARY KEY,
  `first_name` VARCHAR(60)  NOT NULL,
  `last_name`  VARCHAR(60)  NOT NULL,
  `password`   VARCHAR(255) NOT NULL,
  email        VARCHAR(255)
);

CALL test.seq_make('users');

-- @formatter:off
DELIMITER $$
DROP PROCEDURE IF EXISTS test.`get_books_by_language` $$
CREATE PROCEDURE test.`get_books_by_language`(
  IN  _language VARCHAR(20),
  OUT _total    INT
)
  BEGIN
    SELECT count(id) INTO _total FROM test.books WHERE `language` = _language;
    SELECT * FROM test.books WHERE `language` = _language;
  END $$

DROP PROCEDURE IF EXISTS test.`get_books_by_id` $$
CREATE PROCEDURE test.`get_books_by_id`(
  IN _id INT
)
  BEGIN
    SELECT * FROM test.books WHERE id = _id;
  END $$

DROP PROCEDURE IF EXISTS test.`get_books_by_page` $$
# noinspection SqlUnused
CREATE PROCEDURE test.`get_books_by_page`(
  IN _page INT,
  INOUT _page_size INT
)
  BEGIN
    DECLARE offset INT;
    DECLARE count INT DEFAULT _page_size;
    SET offset = (_page - 1) * _page_size;
    SELECT count(id) INTO _page_size FROM (
      SELECT id FROM test.books LIMIT offset, count
    ) t;
    SELECT * FROM test.books LIMIT offset, count;
  END $$
DELIMITER ;
-- @formatter:on

CREATE TABLE test.auto_increment_id (
  id      INT PRIMARY KEY AUTO_INCREMENT,
  `value` CHAR(1) NOT NULL
);