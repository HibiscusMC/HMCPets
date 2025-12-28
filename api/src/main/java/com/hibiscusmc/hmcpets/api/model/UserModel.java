package com.hibiscusmc.hmcpets.api.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.*;

@Data
@Setter(AccessLevel.NONE)
public class UserModel {

    private final UUID uuid;

    private final Map<UUID, CachedPet> pets = new HashMap<>();

    //List of deleted pets at runtime - updated on saving.
    private final List<UUID> deletedPets = new ArrayList<>();

    @Setter(AccessLevel.PUBLIC)
    private int petPoints;

    //Despawns all active pets for a player and resets their ownerInstance, to avoid pets
    //going to ghost players if rejoining.
    public void despawnActivePets(){
        pets.values().forEach(pet -> {
            pet.pet().despawn(false);
        });
    }

    //Forcibly despawns active pets - used in server shutdown
    public void destroyActivePets(){
        pets.values().forEach(pet -> {
            pet.pet().destroy();
        });
    }

    public Optional<PetModel> getPet(UUID uuid){
        CachedPet cachedPet = pets.get(uuid);
        if(cachedPet == null) return Optional.empty();

        return Optional.of(cachedPet.pet());
    }

    public int spawnedPets(){
        return (int)pets.values().stream().filter(pet -> pet.pet().isSpawned()).count();
    }

    public Optional<PetModel> getPet(String partialUUID){
        return pets.values().stream().map(CachedPet::pet).filter(pet -> pet.id().toString().startsWith(partialUUID)).findAny();
    }

    public void setPets(Iterable<PetModel> allPets) {
        allPets.forEach(pet -> pets.put(pet.id(), new CachedPet(pet, false)));
    }

    public void addPet(PetModel pet) {
        pets.put(pet.id(), new CachedPet(pet));
    }

    public void removePet(PetModel pet) {
        pet.despawn(false);
        pet.destroy();

        pets.remove(pet.id());

        deletedPets.add(pet.id());
    }

    public long countPets() {
        return pets.values().stream().filter(pet -> pet.removed() == CachedPet.RemoveType.NONE).count();
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