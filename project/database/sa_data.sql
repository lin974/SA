CREATE DATABASE SA_DB;
USE SA_DB;


CREATE TABLE personaldata (
    realname VARCHAR(20),
    email VARCHAR(20),   
    password VARCHAR(20),
    role VARCHAR(1),        
    topic_id INT,
    comment_id INT,
    
    PRIMARY KEY (topic_id, comment_id) 
);


CREATE TABLE topic_data(
    topic_id INT PRIMARY KEY,
    random_name VARCHAR(8),
    topic_content VARCHAR(500),
    topic_time VARCHAR(13)
);


CREATE TABLE comment_data(
    topic_id INT,
    comment_id INT,
    random_name VARCHAR(8),
    comment_content VARCHAR(500),
    comment_time VARCHAR(13),
   
    PRIMARY KEY (topic_id, comment_id) 
);

INSERT INTO personaldata VALUES("小林","123@gmail.com","123","0",1,0);
INSERT INTO personaldata VALUES("小黑","1234@gmail.com","1234","0",1,1);

INSERT INTO topic_data VALUES(1,"54632159","中大餐廳推薦","20251205");
INSERT INTO comment_data VALUES(1,1,"55632159","推薦下山吃","20251206");

SHOW TABLES;