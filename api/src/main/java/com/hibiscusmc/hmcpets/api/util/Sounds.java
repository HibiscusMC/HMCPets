package com.hibiscusmc.hmcpets.api.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.intellij.lang.annotations.Subst;

import java.util.Arrays;
import java.util.Iterator;

public class Sounds {

    public static Sound parseSound(String string) {
        Iterator<String> split = Arrays.stream(string.split(";")).iterator();
        if (!split.hasNext()) {
            return null;
        }

        Sound.Builder sound = Sound.sound();

        @Subst("minecraft:sound")
        String rawSoundName = split.next();
        sound.type(Key.key(rawSoundName));

        return sound.build();
    }

    public static void playSound(Audience audience, String rawSound) {
        Sound sound = parseSound(rawSound);
        if (sound == null) {
            return;
        }

        audience.playSound(sound);
    }

}
