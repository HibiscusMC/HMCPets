package com.hibiscusmc.hmcpets.api.i18n;

import com.hibiscusmc.hmcpets.api.util.Adventure;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.intellij.lang.annotations.Subst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class LangEntry {

    private final static LegacyComponentSerializer LEGACY
            = LegacyComponentSerializer.legacyAmpersand();

    private String string;

    public LangEntry(String string) {
        this.string = string;
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

        audience.sendMessage(Adventure.parse(string, placeholders));
    }

    public Component component() {
        return Adventure.parse(string);
    }

    public String legacy() {
        return LEGACY.serialize(component());
    }

}