package com.hibiscusmc.hmcpets.storage.impl;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.model.Collar;
import com.hibiscusmc.hmcpets.model.Pet;
import com.hibiscusmc.hmcpets.model.Skin;
import com.hibiscusmc.hmcpets.model.User;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface Storage {
    String name();

    void initialize(PluginConfig.StorageConfig config);
    void close();

    Connection getConnection();

    void insertPet(Pet pet);
    Pet selectPet(User user, int petId);
    List<Pet> selectPets(User user);
    void updatePetName(Pet pet, String newName);
    void updatePetLevel(Pet pet, int newLevel, int newExperience);
    void updatePetSkin(Pet pet, Skin newSkin);
    void updatePetRarity(Pet pet, Pet.Rarity newRarity);
    void updatePetCollar(Pet pet, Collar newCollar);
    void updatePetCraving(Pet pet, ItemStack newCraving);
    void updatePetStatus(Pet pet, Pet.Status newStatus);
    void updatePetStats(Pet pet, int power, double health, double attack, double hunger);
    void savePet(Pet pet);
    void deletePet(Pet pet);

    void insertUser(User user);
    User selectUser(int userId);
    User selectUserByUuid(UUID uuid);

    void updateUserPetPoints(User user, int newPoints);
    void saveUser(User user);
    void deleteUser(User user);

    void insertActivePet(User user, Pet pet);
    List<Pet> selectActivePets(User user);
    void deleteActivePet(User user, Pet pet);

    void insertFavoritePets(User user, Pet pet);
    List<Pet> selectFavoritePets(User user);
    void deleteFavoritePet(User user, Pet pet);

    default String getStatementPath(PluginConfig.StorageConfig.Method method, String statement) {
        return "statements/" + method.name().toLowerCase() + "/" + statement + ".sql";
    }
}
