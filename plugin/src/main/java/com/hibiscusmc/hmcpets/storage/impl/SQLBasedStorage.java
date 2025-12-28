package com.hibiscusmc.hmcpets.storage.impl;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.registry.PetRarityRegistry;
import com.hibiscusmc.hmcpets.api.storage.Storage;
import com.hibiscusmc.hmcpets.api.storage.StorageMethod;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.pet.PetData;
import lombok.extern.java.Log;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Log(topic = "HMCPets")
public abstract class SQLBasedStorage implements Storage {

    // Pets Statements
    private static final String PETS_INSERT = "INSERT INTO <prefix>pets (" +
            " pet_id, owner, pet_type, pet_name, pet_level, experience," +
            " skin, rarity, collar, craving, obtained_timestamp, last_fed_timestamp," +
            " power, health, attack, hunger" +
            ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String PETS_SELECT_ALL = "SELECT * FROM <prefix>pets WHERE owner = ?;";
    private static final String PETS_SELECT_BY_ID = "SELECT * FROM <prefix>pets WHERE pet_id = ?;";
    private static final String PETS_UPDATE_NAME = "UPDATE <prefix>pets SET pet_name = ? WHERE pet_id = ?;";
    private static final String PETS_UPDATE_LEVEL = "UPDATE <prefix>pets SET pet_level = ?, experience = ? WHERE pet_id = ?;";
    private static final String PETS_UPDATE_SKIN = "UPDATE <prefix>pets SET skin = ? WHERE pet_id = ?;";
    private static final String PETS_UPDATE_RARITY = "UPDATE <prefix>pets SET rarity = ? WHERE pet_id = ?;";
    private static final String PETS_UPDATE_COLLAR = "UPDATE <prefix>pets SET collar = ? WHERE pet_id = ?;";
    private static final String PETS_UPDATE_CRAVING = "UPDATE <prefix>pets SET craving = ? WHERE pet_id = ?;";
    private static final String PETS_UPDATE_STATS = "UPDATE <prefix>pets SET power = ?, health = ?, attack = ?, hunger = ? WHERE pet_id = ?;";
    private static final String PETS_UPDATE = "UPDATE <prefix>pets" +
            " SET owner = ?, pet_type = ?, pet_name = ?, pet_level = ?, experience = ?," +
            " skin = ?, rarity = ?, collar = ?, craving = ?," +
            " obtained_timestamp = ?, last_fed_timestamp = ?," +
            " power = ?, health = ?, attack = ?, hunger = ?" +
            "WHERE pet_id = ?;";
    private static final String PETS_DELETE_BY_ID = "DELETE FROM <prefix>pets WHERE pet_id = ?;";

    // User Statements
    private static final String USERS_INSERT = "INSERT INTO <prefix>users (" +
            " uuid," +
            " pet_points" +
            ") " +
            "VALUES (?, ?);";
    private static final String USERS_SELECT_BY_ID = "SELECT * FROM <prefix>users WHERE uuid = ?;";
    private static final String USERS_UPDATE_PET_POINTS = "UPDATE <prefix>users SET pet_points = ? WHERE uuid = ?;";
    private static final String USERS_UPDATE = "UPDATE <prefix>users SET pet_points = ? WHERE uuid = ?;";
    private static final String USERS_DELETE_BY_ID = "DELETE FROM <prefix>users WHERE uuid = ?;";

    // Active Pets Statements
    private static final String ACTIVE_PETS_INSERT = "INSERT INTO <prefix>active_pets (" +
            " user_id," +
            " pet_id" +
            ") " +
            "VALUES (?, ?);";
    private static final String ACTIVE_PETS_SELECT_ALL = "SELECT * FROM <prefix>active_pets WHERE user_id = ?;";
    private static final String ACTIVE_PETS_DELETE = "DELETE FROM <prefix>active_pets WHERE user_id = ? AND pet_id = ?;";

    // Favorite Pets Statements
    private static final String FAVORITE_PETS_INSERT = "INSERT INTO <prefix>favorite_pets (" +
            " user_id," +
            " pet_id" +
            ") " +
            "VALUES (?, ?);";
    private static final String FAVORITE_PETS_SELECT_ALL = "SELECT * FROM <prefix>favorite_pets WHERE user_id = ?;";
    private static final String FAVORITE_PETS_DELETE = "DELETE FROM <prefix>favorite_pets WHERE user_id = ? AND pet_id = ?;";

    protected final HMCPets instance;
    protected final PluginConfig pluginConfig;
    protected final PetConfig petConfig;

    public SQLBasedStorage(HMCPets instance, PluginConfig pluginConfig, PetConfig petConfig) {
        this.instance = instance;
        this.pluginConfig = pluginConfig;
        this.petConfig = petConfig;
    }

    private String parsePrefix(String statement) {
        return statement.replace("<prefix>", pluginConfig.storage().prefix());
    }

    @Override
    public void insertPet(PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_INSERT))) {
            statement.setString(1, pet.id().toString());
            statement.setString(2, pet.owner().uuid().toString());
            statement.setString(3, pet.config().id());
            statement.setString(4, pet.name());
            statement.setInt(5, pet.level());
            statement.setLong(6, pet.experience());
            statement.setString(7, pet.skin() != null ? pet.skin().id() : null);
            statement.setString(8, pet.rarity().id());
            statement.setString(9, pet.collar() != null ? pet.collar().id() : null);
            statement.setString(10, pet.craving() != null ? Hooks.getStringItem(pet.craving()) : null);
            statement.setTimestamp(11, Timestamp.from(Instant.ofEpochMilli(pet.obtainedTimestamp())));
            statement.setTimestamp(12, pet.lastFed() > 0 ? Timestamp.from(Instant.ofEpochMilli(pet.lastFed())) : null);
            statement.setInt(13, pet.power());
            statement.setDouble(14, pet.health());
            statement.setDouble(15, pet.attack());
            statement.setDouble(16, pet.hunger());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public PetModel selectPet(UserModel user, UUID petId) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_SELECT_BY_ID))) {
            statement.setString(1, petId.toString());

            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    return parsePet(user, res);
                } else return null;
            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            return null;
        }
    }

    @Override
    public Set<PetModel> selectPets(UserModel user) {
        Connection connection = getConnection();
        Set<PetModel> pets = new HashSet<>();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_SELECT_ALL))) {
            statement.setString(1, user.uuid().toString());

            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    pets.add(parsePet(user, res));
                }

                return pets;
            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            return pets;
        }
    }

    private PetModel parsePet(UserModel user, ResultSet rs) throws SQLException {
        String petId = rs.getString("pet_id");
        String owner = rs.getString("owner");
        String configId = rs.getString("pet_type");

        if (configId == null || petId.equals("null") || !owner.equals(user.uuid().toString())) {
            return null;
        }


        PetData petData = petConfig.allPets().get(configId);
        PetModel pet = new PetModel(UUID.fromString(petId), user, petData);

        String name = rs.getString("pet_name");
        pet.name(name);

        int power = rs.getInt("power");
        int level = rs.getInt("pet_level");
        long exp = rs.getLong("experience");
        double health = rs.getDouble("health");
        double attack = rs.getDouble("attack");
        double hunger = rs.getDouble("hunger");
        Timestamp obtainedTime = rs.getTimestamp("obtained_timestamp");
        Timestamp lastFedTime = rs.getTimestamp("last_fed_timestamp");

        pet.power(power);
        pet.level(level);
        pet.experience(exp);
        pet.health(health);
        pet.attack(attack);
        pet.hunger(hunger);

        pet.lastFed(lastFedTime.getTime());
        pet.obtainedTimestamp(obtainedTime.getTime());

        String rawSkin = rs.getString("skin");
        SkinModel skin = petData.skins().get(rawSkin);
        if (skin != null) {
            pet.skin(skin);
        }

        PetRarityRegistry rarityRegistry = instance.petRarityRegistry();
        Optional<PetRarity> rarity = rarityRegistry.getRegistered(rs.getString("rarity"));
        pet.rarity(rarity.orElse(null));

        String rawCollar = rs.getString("collar");
        CollarModel collar = petData.collars().get(rawCollar);
        if (collar != null) {
            pet.collar(collar);
        }

        String rawCraving = rs.getString("craving");
        if (rawCraving != null) {
            ItemStack craving = Hooks.getItem(rawCraving);
            pet.craving(craving);
        }

        return pet;
    }

    @Override
    public void updatePetName(PetModel pet, String newName) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_NAME))) {
            statement.setString(1, newName);
            statement.setString(2, pet.id().toString());

            statement.execute();

            pet.name(newName);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void updatePetLevel(PetModel pet, int newLevel, int newExperience) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_LEVEL))) {
            statement.setInt(1, newLevel);
            statement.setLong(2, newExperience);
            statement.setString(3, pet.id().toString());

            statement.execute();

            pet.level(newLevel);
            pet.experience(newExperience);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void updatePetSkin(PetModel pet, SkinModel newSkin) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_SKIN))) {
            statement.setString(1, newSkin.id());
            statement.setString(2, pet.id().toString());

            statement.execute();

            pet.skin(newSkin);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void updatePetRarity(PetModel pet, PetRarity newRarity) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_RARITY))) {
            statement.setString(1, newRarity.id());
            statement.setString(2, pet.id().toString());

            statement.execute();

            pet.rarity(newRarity);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void updatePetCollar(PetModel pet, CollarModel newCollar) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_COLLAR))) {
            statement.setString(1, newCollar.id());
            statement.setString(2, pet.id().toString());

            statement.execute();

            pet.collar(newCollar);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void updatePetCraving(PetModel pet, ItemStack newCraving) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_CRAVING))) {
            statement.setString(1, Hooks.getStringItem(newCraving));
            statement.setString(2, pet.id().toString());

            statement.execute();

            pet.craving(newCraving);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void updatePetStats(PetModel pet, int power, double health, double attack, double hunger) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_STATS))) {
            statement.setInt(1, power);
            statement.setDouble(2, health);
            statement.setDouble(3, attack);
            statement.setDouble(4, hunger);
            statement.setString(5, pet.id().toString());

            statement.execute();

            pet.power(power);
            pet.health(health);
            pet.attack(attack);
            pet.hunger(hunger);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void savePet(PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE))) {
            statement.setString(1, pet.owner().uuid().toString());
            statement.setString(2, pet.config().id());
            statement.setString(3, pet.name());
            statement.setInt(4, pet.level());
            statement.setLong(5, pet.experience());
            statement.setString(6, pet.skin() != null ? pet.skin().id() : null);
            statement.setString(7, pet.rarity() != null ? pet.rarity().id() : null);
            statement.setString(8, pet.collar() != null ? pet.collar().id() : null);
            statement.setString(9, pet.craving() != null ? Hooks.getStringItem(pet.craving()) : null);
            statement.setTimestamp(10, Timestamp.from(Instant.ofEpochMilli(pet.obtainedTimestamp())));
            statement.setTimestamp(11, pet.lastFed() > 0 ? Timestamp.from(Instant.ofEpochMilli(pet.lastFed())) : null);
            statement.setInt(12, pet.power());
            statement.setDouble(13, pet.health());
            statement.setDouble(14, pet.attack());
            statement.setDouble(15, pet.hunger());
            statement.setString(16, pet.id().toString()); //<-- Search index

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void deletePet(UUID petID) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_DELETE_BY_ID))) {
            statement.setString(1, petID.toString());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void insertUser(UserModel user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_INSERT))) {
            statement.setString(1, user.uuid().toString());
            statement.setInt(2, 0);

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public UserModel selectUser(UUID userId) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_SELECT_BY_ID))) {
            statement.setString(1, userId.toString());

            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    return parseUser(res, userId);
                } else return null;
            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            return null;
        }
    }

    private UserModel parseUser(ResultSet res, UUID userId) throws SQLException {
        String uuid = res.getString("uuid");

        if (uuid == null || uuid.equals("null")
        ) {
            return null;
        }

        UserModel user = new UserModel(userId);

        int petPoints = res.getInt("pet_points");
        user.petPoints(petPoints);

        return user;
    }

    @Override
    public void updateUserPetPoints(UserModel user, int newPoints) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_UPDATE_PET_POINTS))) {
            statement.setInt(1, newPoints);
            statement.setString(2, user.uuid().toString());

            statement.execute();

            user.petPoints(newPoints);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void saveUser(UserModel user) {
        System.out.println("Saving user " + user.uuid());
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_UPDATE))) {
            statement.setInt(1, user.petPoints());
            statement.setString(2, user.uuid().toString());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void deleteUser(UserModel user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_DELETE_BY_ID))) {
            statement.setString(1, user.uuid().toString());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void insertActivePet(UserModel user, PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(ACTIVE_PETS_INSERT))) {
            statement.setString(1, user.uuid().toString());
            statement.setString(2, pet.id().toString());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public Set<PetModel> selectActivePets(UserModel user) {
        Connection connection = getConnection();
        Set<PetModel> pets = new HashSet<>();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(ACTIVE_PETS_SELECT_ALL))) {
            statement.setString(1, user.uuid().toString());

            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    String petId = res.getString("pet_id");
                    if (petId == null) {
                        continue;
                    }

                    pets.add(selectPet(user, UUID.fromString(petId)));
                }

                return pets;
            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            return pets;
        }
    }

    @Override
    public void deleteActivePet(UserModel user, PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(ACTIVE_PETS_DELETE))) {
            statement.setString(1, user.uuid().toString());
            statement.setString(2, pet.id().toString());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void insertFavoritePets(UserModel user, PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(FAVORITE_PETS_INSERT))) {
            statement.setString(1, user.uuid().toString());
            statement.setString(2, pet.id().toString());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public Set<PetModel> selectFavoritePets(UserModel user) {
        Connection connection = getConnection();
        Set<PetModel> pets = new HashSet<>();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(FAVORITE_PETS_SELECT_ALL))) {
            statement.setString(1, user.uuid().toString());

            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    String petId = res.getString("pet_id");
                    if (petId == null) {
                        continue;
                    }

                    pets.add(selectPet(user, UUID.fromString(petId)));
                }

                return pets;
            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            return pets;
        }
    }

    @Override
    public void deleteFavoritePet(UserModel user, PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(FAVORITE_PETS_DELETE))) {
            statement.setString(1, user.uuid().toString());
            statement.setString(2, pet.id().toString());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    protected String getStatementPath(StorageMethod method, String statement) {
        return "statements/" + method.name().toLowerCase() + "/" + statement + ".sql";
    }

}