# -- Add Users

# --- !Ups

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  provider VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE TRIGGER update_user_timestamp BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

# --- !Downs
DROP TRIGGER update_user_timestamp ON users;
DROP TABLE users;

