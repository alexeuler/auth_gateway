# -- Add Users

# --- !Ups

CREATE TABLE user_register_tokens (
  id SERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  value VARCHAR(255) NOT NULL,
  expiration_time TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE TRIGGER update_user_register_token_timestamp BEFORE UPDATE ON user_register_tokens FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

# --- !Downs
DROP TRIGGER update_user_register_token_timestamp ON user_register_tokens;
DROP TABLE user_register_tokens;
