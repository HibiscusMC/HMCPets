package com.hibiscusmc.hmcpets.storage.impl.text;

import com.hibiscusmc.hmcpets.api.data.IStorageData;
import com.hibiscusmc.hmcpets.api.model.enums.PetRarity;
import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import com.hibiscusmc.hmcpets.api.storage.Storage;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public class JSONStorage implements Storage {

    @Override
    public String name() {
        return "JSON";
    }

    @Override
    public void initialize(IStorageData config) {

    }

    @Override
    public void close() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void insertPet(PetModel pet) {

    }

    @Override
    public PetModel selectPet(UserModel user, int petId) {
        return null;
    }

    @Override
    public List<PetModel> selectPets(UserModel user) {
        return List.of();
    }

    @Override
    public void updatePetName(PetModel pet, String newName) {

    }

    @Override
    public void updatePetLevel(PetModel pet, int newLevel, int newExperience) {

    }

    @Override
    public void updatePetSkin(PetModel pet, SkinModel newSkin) {

    }

    @Override
    public void updatePetRarity(PetModel pet, PetRarity newRarity) {

    }

    @Override
    public void updatePetCollar(PetModel pet, CollarModel newCollar) {

    }

    @Override
    public void updatePetCraving(PetModel pet, ItemStack newCraving) {

    }

    @Override
    public void updatePetStatus(PetModel pet, PetStatus newStatus) {

    }

    @Override
    public void updatePetStats(PetModel pet, int power, double health, double attack, double hunger) {

    }

    @Override
    public void savePet(PetModel pet) {

    }

    @Override
    public void deletePet(PetModel pet) {

    }

    @Override
    public void insertUser(UserModel user) {

    }

    @Override
    public UserModel selectUser(int userId) {
        return null;
    }

    @Override
    public UserModel selectUserByUuid(UUID uuid) {
        return null;
    }

    @Override
    public void updateUserPetPoints(UserModel user, int newPoints) {

    }

    @Override
    public void saveUser(UserModel user) {

    }

    @Override
    public void deleteUser(UserModel user) {

    }

    @Override
    public void insertActivePet(UserModel user, PetModel pet) {

    }

    @Override
    public List<PetModel> selectActivePets(UserModel user) {
        return List.of();
    }

    @Override
    public void deleteActivePet(UserModel user, PetModel pet) {

    }

    @Override
    public void insertFavoritePets(UserModel user, PetModel pet) {

    }

    @Override
    public List<PetModel> selectFavoritePets(UserModel user) {
        return List.of();
    }

    @Override
    public void deleteFavoritePet(UserModel user, PetModel pet) {

    }

}