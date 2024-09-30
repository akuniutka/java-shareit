CREATE TABLE IF NOT EXISTS users
(
  id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name  VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  CONSTRAINT users_email_ux UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
  id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  owner_id    BIGINT        NOT NULL REFERENCES users (id),
  name        VARCHAR(255)  NOT NULL,
  description VARCHAR(2000) NOT NULL,
  available   BOOLEAN       NOT NULL
);

CREATE TABLE IF NOT EXISTS bookings
(
  id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id       BIGINT                      NOT NULL REFERENCES items (id),
  booker_id     BIGINT                      NOT NULL REFERENCES users (id),
  booking_start TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  booking_end   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  status        VARCHAR(10)                 NOT NULL,
  CONSTRAINT start_end_order CHECK (booking_start < booking_end),
  CONSTRAINT status_values CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE IF NOT EXISTS comments
(
  id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  item_id   BIGINT                      NOT NULL REFERENCES items (id),
  author_id BIGINT                      NOT NULL REFERENCES users (id),
  text      VARCHAR(2000)               NOT NULL,
  created   TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE OR REPLACE VIEW last_bookings
AS
SELECT id AS booking_id,
       item_id
FROM (SELECT id,
             item_id,
             ROW_NUMBER() OVER (PARTITION BY item_id ORDER BY booking_start DESC) AS r
      FROM bookings
      WHERE status = 'APPROVED' AND booking_start <= CURRENT_TIMESTAMP) AS t
WHERE r = 1;

CREATE OR REPLACE VIEW next_bookings
AS
SELECT id AS booking_id,
       item_id
FROM (SELECT id,
             item_id,
             ROW_NUMBER() OVER (PARTITION BY item_id ORDER BY booking_start) AS r
      FROM bookings
      WHERE status = 'APPROVED' AND booking_start > CURRENT_TIMESTAMP) AS t
WHERE r = 1;
