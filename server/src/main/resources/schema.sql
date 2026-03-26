DROP TABLE IF EXISTS users cascade;
DROP TABLE IF EXISTS items cascade;
DROP TABLE IF EXISTS requests cascade;
DROP TABLE IF EXISTS bookings cascade;
DROP TABLE IF EXISTS comments cascade;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description VARCHAR(1000) NOT NULL,
    requestor_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);
CREATE TABLE IF NOT EXISTS items (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     description VARCHAR(1000),
                                     is_available BOOLEAN NOT NULL,
                                     owner_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                                     request_id BIGINT REFERENCES requests(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                        end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                        item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
                                        booker_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                                        status SMALLINT NOT NULL,
                                        CONSTRAINT valid_dates CHECK (start_date < end_date)
);
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                        text VARCHAR(1000) NOT NULL,
                                        item_id BIGINT NOT NULL REFERENCES items(id) ON DELETE CASCADE,
                                        author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                        created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);