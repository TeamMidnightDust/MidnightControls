package eu.midnightdust.midnightcontrols.client.mouse;

import com.mojang.blaze3d.Blaze3D;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SmoothDouble;

public class EyeTrackerHandler {

    /**
     * Based on the updateMouse method in the Mouse.class, this changes the mouse algorithm to suit eye tracking.
     * This requires the cursor to not be locked, and the raw input setting to be turned off.
     */
    public static void updateMouseWithEyeTracking(double mouseX, double mouseY, Minecraft client, double lastMouseUpdateTime, boolean holdingLeftMouseButton, boolean usingLongRangedTool, SmoothDouble smoothX, SmoothDouble smoothY) {
        if (client.player == null) return;
        /* The player wants objects of interest to be moved under the crosshair that is always center of screen.
        * Normal mouse controls operate with the delta values from the direction of mouse movement,
        * but in eye tracking we want to use the cursor's actual x,y values (their point of gaze), relative to
        * the screen center (where the crosshair is). This new eye tracking delta creates a vector that points
        * from the crosshair to the gaze point. As the player keeps their eyes on the object of interest, we pull
        * that object into the center until the object is underneath the crosshair.
         */
        double deltaTime = Blaze3D.getTime() - lastMouseUpdateTime;

        // The center of screen is the new (0,0)
        double centerX = client.getWindow().getScreenWidth() / 2.0;
        double centerY = client.getWindow().getScreenHeight() / 2.0;
        double gazeRawX = mouseX - centerX;
        double gazeRawY = mouseY - centerY;

        //This part follows the original mouse.java somewhat closely, with different constants
        double feeling = 2.5;
        double sensitivity = client.options.sensitivity().get() * feeling;
        double spyglass = sensitivity * sensitivity * sensitivity;
        double moveScalar = spyglass * 8.0;

        double frameScalar;
        if(client.options.getCameraType().isFirstPerson() && client.player.isScoping()) {
            frameScalar = spyglass;
        } else {
            frameScalar = moveScalar;
        }
        if(holdingLeftMouseButton && !usingLongRangedTool) {
            frameScalar *= 0.5; //Don't move the camera so much while mining. It's annoying.
        }

        // The longest vector connects the center to the corner of the screen, so that is our maximum magnitude for
        // normalization. We use normalized screen size vector for resolution independent control
        double magnitudeMax = Math.sqrt(centerX*centerX + centerY*centerY);
        double normalizedX = gazeRawX / magnitudeMax;
        double normalizedY = gazeRawY / magnitudeMax;

        double moveX = normalizedX * frameScalar;
        double moveY = normalizedY * frameScalar;
        if (client.options.smoothCamera) {
            moveX = smoothX.getNewDeltaValue(moveX, moveScalar*deltaTime);
            moveY = smoothY.getNewDeltaValue(moveY, moveScalar*deltaTime);
        }

        // The player entity's needs their facing rotated.
        double invertY = 1.0;
        double moveMagnitude = Math.sqrt(normalizedX*normalizedX + normalizedY*normalizedY);
        if (client.options.invertMouseY().get()) {
            invertY = -1.0;
        }
        boolean notInDeadzone = (moveMagnitude > MidnightControlsConfig.eyeTrackerDeadzone) && !usingLongRangedTool;
        if (client.player != null && notInDeadzone) {
            client.player.turn(moveX, moveY * invertY);
            client.getTutorial().onMouse(moveX, moveY);
        }
    }
}
