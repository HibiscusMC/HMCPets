package com.hibiscusmc.hmcpets.config;

import lombok.extern.java.Log;
import me.lojosho.hibiscuscommons.config.serializer.ItemSerializer;
import me.lojosho.shaded.configurate.CommentedConfigurationNode;
import me.lojosho.shaded.configurate.ConfigurateException;
import me.lojosho.shaded.configurate.yaml.NodeStyle;
import me.lojosho.shaded.configurate.yaml.YamlConfigurationLoader;
import me.lojosho.shaded.configurate.yaml.internal.snakeyaml.DumperOptions;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Arrays;

@Log(topic = "HMCPets")
public abstract class AbstractConfig {
    private final Path path;

    protected YamlConfigurationLoader loader;
    protected CommentedConfigurationNode configNode;

    protected AbstractConfig(Path path) {
        this.path = path;
    }

    protected void load() {
        try {
            YamlConfigurationLoader.Builder builder = YamlConfigurationLoader
                    .builder()
                    .path(path)
                    .defaultOptions(opts ->
                            opts.serializers(build -> build.register(ItemStack.class, ItemSerializer.INSTANCE)))
                    .nodeStyle(NodeStyle.BLOCK);

            Field optsField = builder.getClass().getDeclaredField("options");
            optsField.setAccessible(true);

            DumperOptions dumperOptions = (DumperOptions) optsField.get(builder);
            dumperOptions.setWidth(Integer.MAX_VALUE);

            configNode = (loader = builder
                    .build())
                    .load();
        } catch (NoSuchFieldException | IllegalAccessException | ConfigurateException ex) {
            log.severe("Could not load config file: " + path);
            throw new RuntimeException(ex);
        }
    }

    protected CommentedConfigurationNode get(String path) {
        return configNode.node(Arrays.stream(path.split("\\.")).toList());
    }
}