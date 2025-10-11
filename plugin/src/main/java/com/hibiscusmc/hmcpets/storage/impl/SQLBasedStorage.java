package com.hibiscusmc.hmcpets.storage.impl;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import com.hibiscusmc.hmcpets.api.registry.PetRarityRegistry;
import com.hibiscusmc.hmcpets.api.storage.Storage;
import com.hibiscusmc.hmcpets.api.storage.StorageMethod;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.pet.PetData;
import lombok.extern.java.Log;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log(topic = "HMCPets")
public abstract class SQLBasedStorage implements Storage {

    // Pets Statements
    private static final String PETS_INSERT = "INSERT INTO <prefix>pets (" +
            " pet_id, owner, name, level, experience," +
            " skin, rarity, collar, craving," +
            " status, power, health, attack, hunger" +
            ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String PETS_SELECT_ALL = "SELECT * FROM <prefix>pets WHERE owner = ?;";
    private static final String PETS_SELECT_BY_ID = "SELECT * FROM <prefix>pets WHERE id = ?;";
    private static final String PETS_UPDATE_NAME = "UPDATE <prefix>pets SET name = ? WHERE id = ?;";
    private static final String PETS_UPDATE_LEVEL = "UPDATE <prefix>pets SET level = ?, experience = ? WHERE id = ?;";
    private static final String PETS_UPDATE_SKIN = "UPDATE <prefix>pets SET skin = ? WHERE id = ?;";
    private static final String PETS_UPDATE_RARITY = "UPDATE <prefix>pets SET rarity = ? WHERE id = ?;";
    private static final String PETS_UPDATE_COLLAR = "UPDATE <prefix>pets SET collar = ? WHERE id = ?;";
    private static final String PETS_UPDATE_CRAVING = "UPDATE <prefix>pets SET craving = ? WHERE id = ?;";
    private static final String PETS_UPDATE_STATUS = "UPDATE <prefix>pets SET status = ? WHERE id = ?;";
    private static final String PETS_UPDATE_STATS = "UPDATE <prefix>pets SET power = ?, health = ?, attack = ?, hunger = ? WHERE id = ?;";
    private static final String PETS_UPDATE = "UPDATE <prefix>pets " +
            " SET owner = ?, name = ?, level = ?, experience = ?," +
            " skin = ?, rarity = ?, collar = ?, craving = ?," +
            " status = ?, power = ?, health = ?, attack = ?," +
            " hunger = ?" +
            " WHERE id = ?;";
    private static final String PETS_DELETE_BY_ID = "DELETE FROM <prefix>pets WHERE id = ?;";

    // User Statements
    private static final String USERS_INSERT = "INSERT INTO <prefix>users (" +
            " uuid," +
            " pet_points" +
            ") " +
            "VALUES (?, ?);";
    private static final String USERS_SELECT_BY_ID = "SELECT * FROM <prefix>users WHERE id = ?;";
    private static final String USERS_SELECT_BY_UUID = "SELECT * FROM <prefix>users WHERE uuid = ?;";
    private static final String USERS_UPDATE_PET_POINTS = "UPDATE <prefix>users SET points = ? WHERE id = ?;";
    private static final String USERS_UPDATE = "UPDATE <prefix>users SET points = ? WHERE id = ?;";
    private static final String USERS_DELETE_BY_ID = "DELETE FROM <prefix>users WHERE id = ?;";

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
            statement.setString(1, pet.config().id());
            statement.setInt(2, pet.owner().id());
            statement.setString(3, pet.name());
            statement.setInt(4, pet.level());
            statement.setLong(5, pet.experience());
            statement.setString(6, pet.skin() != null ? pet.skin().id() : null);
            statement.setString(7, pet.rarity().id());
            statement.setString(8, pet.collar() != null ? pet.collar().id() : null);
            statement.setString(9, Hooks.getStringItem(pet.craving()));
            statement.setString(10, pet.status() != null ? pet.status().name().toLowerCase() : PetStatus.IDLE.name().toLowerCase());
            statement.setInt(11, pet.power());
            statement.setDouble(12, pet.health());
            statement.setDouble(13, pet.attack());
            statement.setDouble(14, pet.hunger());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public PetModel selectPet(UserModel user, int petId) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_SELECT_BY_ID))) {
            statement.setInt(1, petId);

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
    public List<PetModel> selectPets(UserModel user) {
        Connection connection = getConnection();
        List<PetModel> pets = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_SELECT_ALL))) {
            statement.setInt(1, user.id());

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
        int id = rs.getInt("id");
        int owner = rs.getInt("owner");
        String configId = rs.getString("pet_id");

        if (configId == null ||
                configId.equals("null") ||
                id == 0 ||
                owner == 0 ||
                owner != user.id()
        ) {
            return null;
        }

        PetData petData = petConfig.allPets().get(configId);
        PetModel pet = new PetModel(id, user, petData);

        String name = rs.getString("name");
        pet.name(name);

        int power = rs.getInt("power");
        int level = rs.getInt("level");
        long exp = rs.getLong("experience");
        double health = rs.getDouble("health");
        double attack = rs.getDouble("attack");
        double hunger = rs.getDouble("hunger");

        pet.power(power);
        pet.level(level);
        pet.experience(exp);
        pet.health(health);
        pet.attack(attack);
        pet.hunger(hunger);

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
        ItemStack craving = Hooks.getItem(rawCraving);
        if (craving != null) {
            pet.craving(craving);
        }

        PetStatus status = PetStatus.of(rs.getString("status"));
        pet.status(status);

        Date obtained = rs.getDate("obtained_timestamp");
        Date lastFed = rs.getDate("last_fed_timestamp");

        pet.obtainedTimestamp(obtained.getTime());

        if (lastFed != null) {
            pet.lastFed(lastFed.getTime());
        }

        return pet;
    }

    @Override
    public void updatePetName(PetModel pet, String newName) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_NAME))) {
            statement.setString(1, newName);
            statement.setInt(2, pet.id());

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
            statement.setInt(3, pet.id());

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
            statement.setInt(2, pet.id());

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
            statement.setInt(2, pet.id());

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
            statement.setInt(2, pet.id());

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
            statement.setInt(2, pet.id());

            statement.execute();

            pet.craving(newCraving);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void updatePetStatus(PetModel pet, PetStatus newStatus) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_UPDATE_STATUS))) {
            statement.setString(1, newStatus.name().toLowerCase());
            statement.setInt(2, pet.id());

            statement.execute();

            pet.status(newStatus);
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
            statement.setInt(5, pet.id());

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
            statement.setInt(1, pet.owner().id());
            statement.setString(2, pet.name());
            statement.setInt(3, pet.level());
            statement.setLong(4, pet.experience());
            statement.setString(5, pet.skin().id());
            statement.setString(6, pet.rarity().id());
            statement.setString(7, pet.collar().id());
            statement.setString(8, Hooks.getStringItem(pet.craving()));
            statement.setString(9, pet.status().name().toLowerCase());
            statement.setInt(10, pet.power());
            statement.setDouble(11, pet.health());
            statement.setDouble(12, pet.attack());
            statement.setDouble(13, pet.hunger());
            statement.setInt(14, pet.id());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void deletePet(PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(PETS_DELETE_BY_ID))) {
            statement.setInt(1, pet.id());

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
    public UserModel selectUser(int userId) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_SELECT_BY_ID))) {
            statement.setInt(1, userId);

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

    @Override
    public UserModel selectUserByUuid(UUID uuid) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_SELECT_BY_UUID))) {
            statement.setString(1, uuid.toString());

            try (ResultSet res = statement.executeQuery()) {
                if (res.next()) {
                    int userId = res.getInt("id");
                    if (userId != 0) return parseUser(res, userId);
                }

                return null;
            }
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
            return null;
        }
    }

    private UserModel parseUser(ResultSet res, int userId) throws SQLException {
        String uuid = res.getString("uuid");

        if (uuid == null || uuid.equals("null")
        ) {
            return null;
        }

        UserModel user = new UserModel(userId, UUID.fromString(uuid));

        int petPoints = res.getInt("pet_points");
        user.petPoints(petPoints);

        return user;
    }

    @Override
    public void updateUserPetPoints(UserModel user, int newPoints) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_UPDATE_PET_POINTS))) {
            statement.setInt(1, newPoints);
            statement.setInt(2, user.id());

            statement.execute();

            user.petPoints(newPoints);
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void saveUser(UserModel user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_UPDATE))) {
            statement.setInt(1, user.petPoints());
            statement.setInt(2, user.id());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void deleteUser(UserModel user) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(USERS_DELETE_BY_ID))) {
            statement.setInt(1, user.id());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void insertActivePet(UserModel user, PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(ACTIVE_PETS_INSERT))) {
            statement.setInt(1, user.id());
            statement.setInt(2, pet.id());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public List<PetModel> selectActivePets(UserModel user) {
        Connection connection = getConnection();
        List<PetModel> pets = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(ACTIVE_PETS_SELECT_ALL))) {
            statement.setInt(1, user.id());

            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    int petId = res.getInt("pet_id");
                    if (petId == 0) {
                        continue;
                    }

                    pets.add(selectPet(user, petId));
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
            statement.setInt(1, user.id());
            statement.setInt(2, pet.id());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public void insertFavoritePets(UserModel user, PetModel pet) {
        Connection connection = getConnection();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(FAVORITE_PETS_INSERT))) {
            statement.setInt(1, user.id());
            statement.setInt(2, pet.id());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    @Override
    public List<PetModel> selectFavoritePets(UserModel user) {
        Connection connection = getConnection();
        List<PetModel> pets = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(parsePrefix(FAVORITE_PETS_SELECT_ALL))) {
            statement.setInt(1, user.id());

            try (ResultSet res = statement.executeQuery()) {
                while (res.next()) {
                    int petId = res.getInt("pet_id");
                    if (petId == 0) {
                        continue;
                    }

                    pets.add(selectPet(user, petId));
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
            statement.setInt(1, user.id());
            statement.setInt(2, pet.id());

            statement.execute();
        } catch (SQLException ex) {
            log.severe(ex.getMessage());
        }
    }

    protected String getStatementPath(StorageMethod method, String statement) {
        return "statements/" + method.name().toLowerCase() + "/" + statement + ".sql";
    }

}