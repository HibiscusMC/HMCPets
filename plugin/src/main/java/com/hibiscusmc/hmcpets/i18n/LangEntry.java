package com.hibiscusmc.hmcpets.i18n;

import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.util.Adventure;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.intellij.lang.annotations.Subst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LangEntry {

    private final static LegacyComponentSerializer LEGACY
            = LegacyComponentSerializer.legacyAmpersand();
    private final static MiniMessage MINI_MESSAGE
            = MiniMessage.miniMessage();

    @Getter
    @Setter
    private String string;

    private final LangConfig langConfig;

    public LangEntry(LangConfig langConfig, String string) {
        this.langConfig = langConfig;
        this.string = string;
    }

    public LangEntry(String string) {
        this(null, string);
    }

    public void send(Audience audience) {
        if (string.isEmpty()) {
            return;
        }

        audience.sendMessage(component());
    }

    public void send(Audience audience, Map<String, String> data) {
        if (string.isEmpty()) {
            return;
        }

        List<TagResolver.Single> placeholders = new ArrayList<>(data.entrySet().stream()
                .map(entry -> {
                    @Subst("") String key = entry.getKey();

                    return Placeholder.parsed(key, entry.getValue());
                }).toList());

        if (langConfig != null && string.contains("<prefix>") && !data.containsKey("<prefix>")) {
            Component prefix = langConfig
                    .prefix().component();

            placeholders.add(Placeholder.component("prefix", prefix));
        }

        audience.sendMessage(Adventure.parse(string, placeholders));
    }

    public Component component() {
        if (langConfig != null && string.contains("<prefix>")) {
            Component prefix = langConfig.prefix().component();

            return MINI_MESSAGE.deserialize(string, Placeholder.component("prefix", prefix));
        } else {
            return MINI_MESSAGE.deserialize(string);
        }
    }

    public String legacy() {
        return LEGACY.serialize(component());
    }

}