package com.hibiscusmc.hmcpets.cache;

import com.hibiscusmc.hmcpets.model.User;
import com.hibiscusmc.hmcpets.storage.Storage;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserCache extends HashMap<UUID, User> {

    private final Map<Integer, User> usersbyIndex
            = new HashMap<>();

    @Inject
    private Storage storage;

    @Override
    public User put(UUID key, User value) {
        usersbyIndex.put(value.id(), value);

        return super.put(key, value);
    }

    @Override
    public User remove(Object key) {
        User user = get(key);
        if (user == null) {
            return null;
        }

        usersbyIndex.remove(user.id());
        return super.remove(key);
    }

    @Nullable
    public User fetch(UUID uuid) {
        return getOrDefault(
                uuid,
                storage.implementation().selectUserByUuid(uuid)
        );
    }

    @Nullable
    public User fetch(int index) {
        return usersbyIndex.getOrDefault(
                index,
                storage.implementation().selectUser(index)
        );
    }

}