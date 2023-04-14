CREATE TABLE IF NOT EXISTS users (
	user_id 		BIGINT 			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	user_name 		VARCHAR(50)		NOT NULL,
	email 			VARCHAR(255) 	UNIQUE NOT NULL
	CHECK (email ~* '^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$')
);

CREATE TABLE IF NOT EXISTS items (
	item_id			BIGINT			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	owner_id		BIGINT			REFERENCES users (user_id) ON DELETE CASCADE,
	item_name		VARCHAR(50)		NOT NULL,
	description		VARCHAR(200)	NOT NULL,
	available		BOOLEAN			NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
	comment_id		BIGINT			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	content			VARCHAR(200)	NOT NULL,
	author_id		BIGINT			REFERENCES users (user_id) ON DELETE CASCADE,
	item_id			BIGINT			REFERENCES items (item_id) ON DELETE cascade,
	creation_date	TIMESTAMP		DEFAULT CURRENT_TIMESTAMP
);

CREATE TYPE booking_status AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS bookings (
	booking_id		BIGINT			GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	start_date		TIMESTAMP		NOT NULL,
	end_date		TIMESTAMP		NOT NULL,
	status			booking_status	NOT NULL,
	booker_id		BIGINT			REFERENCES users (user_id) ON DELETE CASCADE,
	item_id			BIGINT			REFERENCES items (item_id) ON DELETE CASCADE
);