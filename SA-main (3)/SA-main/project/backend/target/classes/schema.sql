CREATE TABLE IF NOT EXISTS personaldata (
  id INT AUTO_INCREMENT PRIMARY KEY,
  realname VARCHAR(255),
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  role VARCHAR(50),
  topic_id INT,
  comment_id INT
);

CREATE TABLE IF NOT EXISTS topic_data (
  topic_id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255),
  topic_content TEXT,
  random_name VARCHAR(255),
  real_name VARCHAR(255),
  topic_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments (
  comment_id INT AUTO_INCREMENT PRIMARY KEY,
  topic_id INT,
  user_id INT,
  author_anon VARCHAR(255),
  content TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
