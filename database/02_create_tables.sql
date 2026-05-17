CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE mods (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    author VARCHAR(100),
    description TEXT,
    downloads INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    developer VARCHAR(150),
    publisher VARCHAR(150),
    platform VARCHAR(100),
    release_date VARCHAR(50),
    metacritic INTEGER DEFAULT 0,
    image_path VARCHAR(500),
    price DECIMAL(10,2) DEFAULT 0.00,
    rawg_image VARCHAR(500)
);

CREATE TABLE game_cards (
    id SERIAL PRIMARY KEY,
    mod_id INTEGER NOT NULL REFERENCES mods(id) ON DELETE CASCADE,
    user_login VARCHAR(150) NOT NULL,
    card_holder VARCHAR(200) NOT NULL,
    card_number_last4 VARCHAR(4) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    purchased_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    id SERIAL PRIMARY KEY,
    game_card_id INTEGER NOT NULL REFERENCES game_cards(id) ON DELETE CASCADE,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) DEFAULT 'SUCCESS',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_ref VARCHAR(100)
);

CREATE TABLE ratings (
    id SERIAL PRIMARY KEY,
    mod_id INTEGER NOT NULL REFERENCES mods(id) ON DELETE CASCADE,
    user_login VARCHAR(150) NOT NULL,
    stars INTEGER CHECK (stars >= 1 AND stars <= 5),
    comment TEXT,
    rated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(mod_id, user_login)
);
