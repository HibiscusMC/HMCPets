package com.hibiscusmc.hmcpets.command;

import co.aikar.commands.PaperCommandManager;
import com.hibiscusmc.hmcpets.HMCPetsPlugin;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.service.Service;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

public class CommandService extends Service {

	@Inject
	private Plugin plugin;
	@Inject
	private Injector injector;

	@Inject
	private PetConfig petConfig;

	protected CommandService() {
		super("Command");
	}

	@Override
	protected void initialize() {
		PaperCommandManager manager = new PaperCommandManager(HMCPetsPlugin.instance());

		manager.registerCommand(injector.getInstance(PetsAdminCommand.class));
		manager.registerCommand(injector.getInstance(PetsCommand.class));

		manager.getCommandCompletions().registerCompletion("pets", c -> petConfig.allPets().keySet());
	}

	@Override
	protected void cleanup() {

	}

}
