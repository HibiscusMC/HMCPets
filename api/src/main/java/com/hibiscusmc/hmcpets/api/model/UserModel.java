package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Setter(AccessLevel.NONE)
public class UserModel {

    private final UUID uuid;

    private final Map<UUID, CachedPet> pets = new HashMap<>();
    private final Map<UUID, CachedPet> activePets = new HashMap<>();
    private final Map<UUID, CachedPet> favoritePets = new HashMap<>();

    @Setter(AccessLevel.PUBLIC)
    private int petPoints;

    public void despawnActivePets(){
        activePets.values().forEach(pet -> pet.pet().despawn());
    }

    public void setPets(Iterable<PetModel> allPets) {
        allPets.forEach(pet -> pets.put(pet.id(), new CachedPet(pet, false)));
    }

    public void addPet(PetModel pet) {
        addPetTo(pets, pet, null);
    }

    public void removePet(PetModel pet) {
        updatePetRemoval(pets, pet, CachedPet.RemoveType.TEMPORAL);
    }

    public void deletePet(PetModel pet) {
        updatePetRemoval(pets, pet, CachedPet.RemoveType.PERMANENT);
    }

    public long countPets() {
        return pets.values().stream().filter(pet -> pet.removed() == CachedPet.RemoveType.NONE).count();
    }

    public void setActivePets(Iterable<PetModel> allPets) {
        allPets.forEach(pet -> {
            pet.status(PetStatus.ACTIVE);
            activePets.put(pet.id(), new CachedPet(pet, false));
        });
    }

    public void addActivePet(PetModel pet, Location spawnLocation) {
        addPetTo(activePets, pet, PetStatus.ACTIVE);

        pet.spawn(spawnLocation);
    }

    public void removeActivePet(PetModel pet) {
        pet.status(PetStatus.IDLE);

        updatePetRemoval(activePets, pet, CachedPet.RemoveType.TEMPORAL);
        pet.despawn();
    }

    public void deleteActivePet(PetModel pet) {
        updatePetRemoval(activePets, pet, CachedPet.RemoveType.PERMANENT);
        pet.despawn();
    }

    public long countActivePets() {
        return activePets.values().stream().filter(pet -> pet.removed() == CachedPet.RemoveType.NONE).count();
    }

    public void setFavoritePets(Iterable<PetModel> allPets) {
        allPets.forEach(pet -> favoritePets.put(pet.id(), new CachedPet(pet, false)));
    }

    public void addFavoritePet(PetModel pet) {
        addPetTo(favoritePets, pet, null);
    }

    public void removeFavoritePet(PetModel pet) {
        updatePetRemoval(favoritePets, pet, CachedPet.RemoveType.TEMPORAL);
    }

    public void deleteFavoritePet(PetModel pet) {
        updatePetRemoval(favoritePets, pet, CachedPet.RemoveType.PERMANENT);
    }

    public boolean hasFavoritePet(PetModel pet) {
        CachedPet cached = favoritePets.get(pet.id());

        return cached != null && cached.removed() == CachedPet.RemoveType.NONE;
    }

    public long countFavoritePets() {
        return favoritePets.values().stream().filter(pet -> pet.removed() == CachedPet.RemoveType.NONE).count();
    }

    private void addPetTo(Map<UUID, CachedPet> map, PetModel pet, PetStatus status) {
        if (pet == null) {
            return;
        }

        if (status != null) {
            pet.status(status);
        }

        CachedPet existing = map.get(pet.id());
        if (existing == null) {
            map.put(pet.id(), new CachedPet(pet));
            return;
        }

        if (existing.removed() != CachedPet.RemoveType.NONE) {
            existing.removed(CachedPet.RemoveType.NONE);
        }

        existing.pet(pet);
    }

    private void updatePetRemoval(Map<UUID, CachedPet> map, PetModel pet, CachedPet.RemoveType type) {
        if (pet == null) {
            return;
        }

        CachedPet cached = map.get(pet.id());
        if (cached == null) {
            return;
        }

        if (type == CachedPet.RemoveType.PERMANENT) {
            map.remove(pet.id());
            return;
        }

        cached.removed(type);
    }

    @Data
    public static class CachedPet {

        private PetModel pet;
        private boolean cached;
        private RemoveType removed = RemoveType.NONE;

        public CachedPet(PetModel pet, boolean cached) {
            this.pet = pet;
            this.cached = cached;
        }

        public CachedPet(PetModel pet) {
            this(pet, true);
        }

        public enum RemoveType {
            NONE,
            TEMPORAL,
            PERMANENT
        }

    }

}