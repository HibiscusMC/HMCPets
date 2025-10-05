package com.hibiscusmc.hmcpets.config.internal;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.service.Service;
import team.unnamed.inject.Inject;

public class ConfigService extends Service {

    @Inject
    private LangConfig langConfig;
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    private PetConfig petConfig;
    @Inject
    private MenuConfig menuConfig;

    protected ConfigService() {
        super("Configuration");
    }

    @Override
    protected void initialize() {
        langConfig.setup();
        pluginConfig.setup();
        petConfig.setup();
    }

    @Override
    protected void cleanup() {
    }

}