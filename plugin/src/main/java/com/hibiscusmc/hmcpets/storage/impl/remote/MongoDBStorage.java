package com.hibiscusmc.hmcpets.storage.impl.remote;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.model.Collar;
import com.hibiscusmc.hmcpets.model.Pet;
import com.hibiscusmc.hmcpets.model.Skin;
import com.hibiscusmc.hmcpets.model.User;
import com.hibiscusmc.hmcpets.storage.impl.Storage;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class MongoDBStorage implements Storage {
    @Override
    public String name() {
        return "MongoDB";
    }

    @Override
    public void initialize(PluginConfig.StorageConfig config) {

    }

    @Override
    public void close() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void insertPet(Pet pet) {

    }

    @Override
    public Pet selectPet(User user, int petId) {
        return null;
    }

    @Override
    public List<Pet> selectPets(User user) {
        return List.of();
    }

    @Override
    public void updatePetName(Pet pet, String newName) {

    }

    @Override
    public void updatePetLevel(Pet pet, int newLevel, int newExperience) {

    }

    @Override
    public void updatePetSkin(Pet pet, Skin newSkin) {

    }

    @Override
    public void updatePetRarity(Pet pet, Pet.Rarity newRarity) {

    }

    @Override
    public void updatePetCollar(Pet pet, Collar newCollar) {

    }

    @Override
    public void updatePetCraving(Pet pet, ItemStack newCraving) {

    }

    @Override
    public void updatePetStatus(Pet pet, Pet.Status newStatus) {

    }

    @Override
    public void updatePetStats(Pet pet, int power, double health, double attack, double hunger) {

    }

    @Override
    public void savePet(Pet pet) {

    }

    @Override
    public void deletePet(Pet pet) {

    }

    @Override
    public void insertUser(User user) {

    }

    @Override
    public User selectUser(int userId) {
        return null;
    }

    @Override
    public User selectUserByUuid(UUID uuid) {
        return null;
    }

    @Override
    public void updateUserPetPoints(User user, int newPoints) {

    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public void deleteUser(User user) {

    }

    @Override
    public void insertActivePet(User user, Pet pet) {

    }

    @Override
    public List<Pet> selectActivePets(User user) {
        return List.of();
    }

    @Override
    public void deleteActivePet(User user, Pet pet) {

    }

    @Override
    public void insertFavoritePets(User user, Pet pet) {

    }

    @Override
    public List<Pet> selectFavoritePets(User user) {
        return List.of();
    }

    @Override
    public void deleteFavoritePet(User user, Pet pet) {

    }
}
