CREATE TABLE IF NOT EXISTS <prefix>users (
    uuid VARCHAR(36) PRIMARY KEY NOT NULL UNIQUE,
    pet_points INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS <prefix>pets (
    pet_id VARCHAR(36) PRIMARY KEY,
    owner VARCHAR(36) NOT NULL,
    pet_type VARCHAR(256) NOT NULL,
    pet_name VARCHAR(256) NOT NULL,
    pet_level INT NOT NULL DEFAULT 1,
    experience BIGINT NOT NULL DEFAULT 0,
    skin VARCHAR(50),
    rarity VARCHAR(256),
    collar VARCHAR(256),
    craving VARCHAR(256),
    obtained_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_fed_timestamp TIMESTAMP NULL,
    power INT DEFAULT 0,
    health DOUBLE DEFAULT 0,
    attack DOUBLE DEFAULT 0,
    hunger DOUBLE DEFAULT 0,
    CONSTRAINT fk_<prefix>pets_owner FOREIGN KEY (owner) REFERENCES <prefix>users(uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS <prefix>active_pets (
    user_id VARCHAR(36) NOT NULL,
    pet_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, pet_id),
    FOREIGN KEY (user_id) REFERENCES <prefix>users(uuid) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES <prefix>pets(pet_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS <prefix>favorite_pets (
    user_id VARCHAR(36) NOT NULL,
    pet_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (user_id, pet_id),
    FOREIGN KEY (user_id) REFERENCES <prefix>users(uuid) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES <prefix>pets(pet_id) ON DELETE CASCADE
);