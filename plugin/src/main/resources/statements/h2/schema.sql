CREATE TABLE IF NOT EXISTS <prefix>users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    pet_points INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS <prefix>pets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id VARCHAR(50) NOT NULL,
    owner INT NOT NULL,
    name VARCHAR(256) NOT NULL,
    level INT NOT NULL DEFAULT 1,
    experience LONG NOT NULL DEFAULT 0,
    skin VARCHAR(50),
    rarity VARCHAR(256),
    collar VARCHAR(256),
    craving VARCHAR(256),
    obtained_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_fed_timestamp TIMESTAMP NULL,
    status VARCHAR(256),
    power INT DEFAULT 0,
    health DOUBLE DEFAULT 0,
    attack DOUBLE DEFAULT 0,
    hunger DOUBLE DEFAULT 0,
    CONSTRAINT fk_<prefix>pets_owner FOREIGN KEY (owner) REFERENCES <prefix>users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS <prefix>active_pets (
    user_id INT NOT NULL,
    pet_id INT NOT NULL,
    PRIMARY KEY (user_id, pet_id),
    FOREIGN KEY (user_id) REFERENCES <prefix>users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES <prefix>pets(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS <prefix>favorite_pets (
    user_id INT NOT NULL,
    pet_id INT NOT NULL,
    PRIMARY KEY (user_id, pet_id),
    FOREIGN KEY (user_id) REFERENCES <prefix>users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES <prefix>pets(id) ON DELETE CASCADE
);