package com.hibiscusmc.hmcpets.util;

import lombok.Getter;
import lombok.extern.java.Log;

@Log(topic = "HMCPets - DEBUG")
public class Debug {
    @Getter
    private static boolean debug = false;

    public static void log(String message, Object... args) {
        if (!debug) {
            return;
        }

        log.info(String.format(message, args));
    }

    public static boolean toggleDebug() {
        return debug = !debug;
    }
}
