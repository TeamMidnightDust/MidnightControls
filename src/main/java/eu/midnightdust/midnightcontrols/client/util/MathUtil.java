package eu.midnightdust.midnightcontrols.client.util;

import net.minecraft.util.math.MathHelper;

public class MathUtil {
    public static class PolarUtil {
        public float polarX;
        public float polarY;
        public PolarUtil() {}

        public void calculate(float x, float y, float speedFactor) {
            calculate(x, y, speedFactor, 0);
        }
        public void calculate(float x, float y, float speedFactor, double deadZone) {
            double inputR = Math.pow(x, 2) + Math.pow(y, 2);
            inputR = (Math.abs(speedFactor * MathHelper.clamp(inputR,0.f,1.f)));
            inputR = inputR < deadZone ? 0f : (inputR-deadZone) / (1f-deadZone);
            double inputTheta = Math.atan2(y, x);
            polarX = (float) (inputR *Math.cos(inputTheta));
            polarY = (float) (inputR *Math.sin(inputTheta));
        }
    }
}
