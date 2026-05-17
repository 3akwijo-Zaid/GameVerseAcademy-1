-- ============================================================
-- GameVerseAcademy — Schema PostgreSQL (DBeaver local)
-- Créer la base d'abord : CREATE DATABASE gameverseacademy;
-- ============================================================

-- Table users
CREATE TABLE IF NOT EXISTS users (
    id       SERIAL PRIMARY KEY,
    login    VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE
);

-- Table mods
CREATE TABLE IF NOT EXISTS mods (
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    category     VARCHAR(100),
    author       VARCHAR(100),
    description  TEXT,
    downloads    INTEGER DEFAULT 0,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    developer    VARCHAR(150),
    publisher    VARCHAR(150),
    platform     VARCHAR(100),
    release_date VARCHAR(50),
    metacritic   INTEGER DEFAULT 0,
    image_path   VARCHAR(500)
);

-- Utilisateur admin par défaut (mot de passe: admin123)
INSERT INTO users (login, password, is_admin)
VALUES ('admin', 'admin123', TRUE)
ON CONFLICT (login) DO NOTHING;

-- Données de test
INSERT INTO mods (title, category, author, description, publisher, downloads, platform, release_date, metacritic)
VALUES
  ('Elden Ring', 'RPG', 'admin', 'Un monde ouvert sombre et brutal de FromSoftware.', 'Bandai Namco', 15000000, 'PC / PS5', '2022-02-25', 96),
  ('God of War Ragnarök', 'Action', 'admin', 'Kratos et Atreus face au Ragnarök nordique.', 'Sony', 11000000, 'PS5', '2022-11-09', 94),
  ('Cyberpunk 2077', 'RPG', 'admin', 'Night City — un futur dystopique à explorer.', 'CD Projekt Red', 25000000, 'PC / PS5', '2020-12-10', 86),
  ('Hollow Knight', 'Adventure', 'admin', 'Un metroidvania indie dans un royaume souterrain.', 'Team Cherry', 5000000, 'PC / Switch', '2017-02-24', 90)
ON CONFLICT DO NOTHING;
