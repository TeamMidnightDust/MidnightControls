package eu.midnightdust.midnightcontrols.neoforge.event;

import eu.midnightdust.midnightcontrols.ControlsMode;
import net.minecraft.entity.player.PlayerEntity;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class PlayerChangeControlsModeEvent extends Event implements IModBusEvent {
    public PlayerChangeControlsModeEvent(PlayerEntity player, ControlsMode controlsMode) {

    }
}
