package com.hibiscusmc.hmcpets.cache;

import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserCache extends HashMap<UUID, UserModel> {

    private final Map<Integer, UserModel> usersByIndex
            = new HashMap<>();

    @Inject
    private StorageHolder storage;

    @Override
    public UserModel put(UUID key, UserModel value) {
        usersByIndex.put(value.id(), value);

        return super.put(key, value);
    }

    @Override
    public UserModel remove(Object key) {
        UserModel user = get(key);
        if (user == null) {
            return null;
        }

        usersByIndex.remove(user.id());
        return super.remove(key);
    }

    @Nullable
    public UserModel fetch(UUID uuid) {
        return getOrDefault(
                uuid,
                storage.implementation().selectUserByUuid(uuid)
        );
    }

    @Nullable
    public UserModel fetch(int index) {
        return usersByIndex.getOrDefault(
                index,
                storage.implementation().selectUser(index)
        );
    }

}