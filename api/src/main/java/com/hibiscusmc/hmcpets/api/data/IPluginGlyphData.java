package com.hibiscusmc.hmcpets.api.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

public interface IPluginGlyphData {

    GlyphBar healthBar();

    GlyphBar hungerBar();


    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    class GlyphBar {
        private int segmentsAmount;
        private Map<Integer, String> segments;

        public static GlyphBar of(int segmentsAmount, Map<Integer, String> segments) {
            return new GlyphBar(segmentsAmount, segments);
        }

        /**
         * Depending on the amount of segments the bar has, returns the correct segment for the given progress
         * @return Nexo/ItemsAdder Glyph ID
         */
        public String getSegment(int progress, int maxAmount) {
            int index = (int) Math.round((double) progress / maxAmount * segmentsAmount);
            index = Math.clamp(index, 0, segmentsAmount);
            return segments.get(index);
        }
    }

}
