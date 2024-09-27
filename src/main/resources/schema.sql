CREATE TABLE IF NOT EXISTS users
(
  id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name  VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE
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
