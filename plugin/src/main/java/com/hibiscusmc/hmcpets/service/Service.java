package com.hibiscusmc.hmcpets.service;

import lombok.extern.java.Log;

@Log(topic = "HMCPets")
public abstract class Service {
    private final String name;

    protected Service(String serviceName) {
        name = serviceName;
    }

    protected abstract void initialize();

    protected abstract void cleanup();

    public void reload() {
        log.info("Reloading " + name + " service...");
        long start = System.currentTimeMillis();

        System.out.println(this.name);

        cleanup();
        initialize();

        long end = System.currentTimeMillis() - start;
        log.info("Reloaded " + name + " service in " + end + " ms");
    }

    public void load() {
        log.info("Loading " + name + " service...");
        long start = System.currentTimeMillis();

        initialize();

        long end = System.currentTimeMillis() - start;
        log.info("Loaded " + name + " service in " + end + " ms");
    }

    public void unload() {
        log.info("Unloading " + name + " service...");
        long start = System.currentTimeMillis();

        cleanup();

        long end = System.currentTimeMillis() - start;
        log.info("Unloaded " + name + " service in " + end + " ms");
    }

}