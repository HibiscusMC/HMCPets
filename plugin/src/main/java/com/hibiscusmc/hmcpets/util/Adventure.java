package com.hibiscusmc.hmcpets.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;

public class Adventure {
    private final static MiniMessage MINI_MESSAGE
            = MiniMessage.miniMessage();

    public static Component parse(String message) {
        return MINI_MESSAGE.deserialize(message);
    }

    public static Component parse(String message, List<TagResolver.Single> resolvers) {
        return MINI_MESSAGE.deserialize(message, TagResolver.resolver(resolvers));
    }

    public static Component parseForMeta(String message) {
        return parse(message)
                .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }
}
