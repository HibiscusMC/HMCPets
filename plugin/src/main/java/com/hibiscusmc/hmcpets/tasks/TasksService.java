package com.hibiscusmc.hmcpets.tasks;

import com.hibiscusmc.hmcpets.service.Service;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

import java.util.ArrayList;
import java.util.List;

public class TasksService extends Service {

    @Inject
    private Plugin plugin;
    @Inject
    private Injector injector;

    protected TasksService() {
        super("Tasks");
    }

    private final List<Integer> registeredTasks = new ArrayList<>();

    @Override
    protected void initialize() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        registeredTasks.add(scheduler.runTaskTimerAsynchronously(plugin, injector.getInstance(PermissibleTask.class), 0L, 100L).getTaskId());
    }

    @Override
    protected void cleanup() {
        //Cancel every registered task and cleanup the list
        registeredTasks.forEach(Bukkit.getScheduler()::cancelTask);
        registeredTasks.clear();
    }
}
