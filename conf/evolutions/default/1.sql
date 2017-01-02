# -- Add Users

# --- !Ups

CREATE OR REPLACE FUNCTION update_modified_column()
  RETURNS TRIGGER AS
$BODY$
BEGIN
  NEW.updated_at = now();;
  RETURN NEW;;
END;;
$BODY$
LANGUAGE plpgsql;

CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE TRIGGER update_user_timestamp BEFORE UPDATE ON users FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

# --- !Downs
DROP TRIGGER update_user_timestamp ON users;
DROP TABLE users;

