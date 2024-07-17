/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols;

import eu.midnightdust.lib.util.PlatformFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the MidnightControls mod.
 *
 * @author LambdAurora & Motschen
 * @version 1.8.0
 * @since 1.0.0
 */
public class MidnightControls {
    public static boolean isExtrasLoaded;

    public static final Logger logger = LogManager.getLogger("MidnightControls");

    public static void init() {
        isExtrasLoaded = PlatformFunctions.isModLoaded("midnightcontrols-extra");
        log("Initializing MidnightControls...");
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info the message to print
     */
    public static void log(String info) {
        logger.info("[MidnightControls] {}", info);
    }

    /**
     * Prints a warning to the terminal.
     *
     * @param warning the warning to print
     */
    public static void warn(String warning) {
        logger.warn("[MidnightControls] {}", warning);
    }
}
