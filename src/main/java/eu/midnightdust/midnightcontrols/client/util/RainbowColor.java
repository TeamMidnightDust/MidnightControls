package eu.midnightdust.midnightcontrols.client.util;

import java.awt.*;

public class RainbowColor {
    public static Color radialRainbow(float saturation, float brightness) {
        float hue = (System.currentTimeMillis() % (1000)) / 1000f;
        return Color.getHSBColor(hue, saturation, brightness);
    }
}
