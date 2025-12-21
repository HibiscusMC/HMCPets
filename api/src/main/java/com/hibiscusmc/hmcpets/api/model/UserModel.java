package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    //Despawns all active pets for a player and resets their ownerInstance, to avoid pets
    //going to ghost players if rejoining.
    public void despawnActivePets(){
        activePets.values().forEach(pet -> {
            pet.pet().despawn(false);
        });
        activePets().clear();
    }

    //Forcibly despawns active pets - used in server shutdown
    public void destroyActivePets(){
        activePets.values().forEach(pet -> {
            pet.pet().destroy();
        });
        activePets().clear();
    }

    public Optional<PetModel> getPet(UUID uuid){
        CachedPet cachedPet = pets.get(uuid);
        if(cachedPet == null) return Optional.empty();

        return Optional.of(cachedPet.pet());
    }

    public Optional<PetModel> getActivePet(UUID uuid){
        CachedPet cachedPet = activePets.get(uuid);
        if(cachedPet == null) return Optional.empty();

        return Optional.of(cachedPet.pet());
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
        pet.despawn(true);
    }

    public void deleteActivePet(PetModel pet) {
        updatePetRemoval(activePets, pet, CachedPet.RemoveType.PERMANENT);
        pet.despawn(false);
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("CachedPet{");
            sb.append("cached=").append(cached);
            sb.append(", removed=").append(removed);
            sb.append(", pet=");

            if (pet == null) {
                sb.append("null");
            } else {
                sb.append("PetModel{");
                sb.append("id=").append(pet.id());
                sb.append(", owner=").append(pet.owner() != null ? pet.owner().uuid() : "null");
                sb.append(", config=").append(pet.config() != null ? pet.config().id() : "null");
                sb.append(", name='").append(pet.name()).append('\'');
                sb.append(", level=").append(pet.level());
                sb.append(", experience=").append(pet.experience());
                sb.append(", skin=").append(pet.skin() != null ? pet.skin().id() : "null");
                sb.append(", rarity=").append(pet.rarity() != null ? pet.rarity().id() : "null");
                sb.append(", collar=").append(pet.collar() != null ? pet.collar().id() : "null");
                sb.append(", craving=").append(pet.craving() != null ? pet.craving().getType() : "null");
                sb.append(", obtainedTimestamp=").append(pet.obtainedTimestamp());
                sb.append(", lastFed=").append(pet.lastFed());
                sb.append(", status=").append(pet.status());
                sb.append(", power=").append(pet.power());
                sb.append(", health=").append(pet.health());
                sb.append(", attack=").append(pet.attack());
                sb.append(", hunger=").append(pet.hunger());
                sb.append(", isSpawned=").append(pet.isSpawned());
                sb.append(", entity=").append(pet.entity() != null ? pet.entity().getType() : "null");
                sb.append(", ownerInstance=").append(pet.ownerInstance() != null ? pet.ownerInstance().getName() : "null");
                sb.append('}');
            }

            sb.append('}');
            return sb.toString();
        }

    }

}