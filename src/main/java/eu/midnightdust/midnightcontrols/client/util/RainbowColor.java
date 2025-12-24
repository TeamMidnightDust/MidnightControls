package eu.midnightdust.midnightcontrols.client.util;

import java.awt.*;

public class RainbowColor {
    public static float hue;
    public static void tick() {
        if (hue > 1) hue = 0f;
        hue = hue + 0.01f;
    }

    public static Color radialRainbow(float saturation, float brightness) {
        return Color.getHSBColor(hue, saturation, brightness);
    }
}
