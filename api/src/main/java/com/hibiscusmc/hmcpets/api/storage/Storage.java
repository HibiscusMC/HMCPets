package com.hibiscusmc.hmcpets.api.storage;

import com.hibiscusmc.hmcpets.api.data.IStorageData;
import com.hibiscusmc.hmcpets.api.model.enums.PetRarity;
import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

public interface Storage {

    String name();

    void initialize(IStorageData config);
    void close();

    Connection getConnection();

    void insertPet(PetModel pet);
    PetModel selectPet(UserModel user, int petId);
    List<PetModel> selectPets(UserModel user);
    void updatePetName(PetModel pet, String newName);
    void updatePetLevel(PetModel pet, int newLevel, int newExperience);
    void updatePetSkin(PetModel pet, SkinModel newSkin);
    void updatePetRarity(PetModel pet, PetRarity newRarity);
    void updatePetCollar(PetModel pet, CollarModel newCollar);
    void updatePetCraving(PetModel pet, ItemStack newCraving);
    void updatePetStatus(PetModel pet, PetStatus newStatus);
    void updatePetStats(PetModel pet, int power, double health, double attack, double hunger);
    void savePet(PetModel pet);
    void deletePet(PetModel pet);

    void insertUser(UserModel user);
    UserModel selectUser(int userId);
    UserModel selectUserByUuid(UUID uuid);

    void updateUserPetPoints(UserModel user, int newPoints);
    void saveUser(UserModel user);
    void deleteUser(UserModel user);

    void insertActivePet(UserModel user, PetModel pet);
    List<PetModel> selectActivePets(UserModel user);
    void deleteActivePet(UserModel user, PetModel pet);

    void insertFavoritePets(UserModel user, PetModel pet);
    List<PetModel> selectFavoritePets(UserModel user);
    void deleteFavoritePet(UserModel user, PetModel pet);

}