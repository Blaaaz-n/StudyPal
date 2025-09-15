-- USERS
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email TEXT NOT NULL UNIQUE,
                       password_hash TEXT NOT NULL,
                       first_name TEXT,
                       last_name TEXT
);

-- PLANS
CREATE TABLE plans (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                       title TEXT NOT NULL,
                       start_date DATE NOT NULL,
                       end_date DATE NOT NULL
);

-- EVENTS
CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,
                        plan_id BIGINT NOT NULL REFERENCES plans(id) ON DELETE CASCADE,
                        title TEXT NOT NULL,
                        start_ts TIMESTAMPTZ NOT NULL,
                        end_ts   TIMESTAMPTZ NOT NULL,
                        CONSTRAINT chk_event_time_range CHECK (end_ts > start_ts)
);
