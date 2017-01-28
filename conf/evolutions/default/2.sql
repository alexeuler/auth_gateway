# -- Add Tokens

# --- !Ups

CREATE TABLE tokens (
  id SERIAL PRIMARY KEY,
  value VARCHAR(255) NOT NULL,
  action VARCHAR(255) NOT NULL,
  payload VARCHAR(255) NOT NULL,
  expiration_time TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT current_timestamp,
  updated_at TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE TRIGGER update_token_timestamp BEFORE UPDATE ON tokens FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

# --- !Downs
DROP TRIGGER update_token_timestamp ON tokens;
DROP TABLE tokens;
