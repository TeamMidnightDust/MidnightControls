package eu.midnightdust.midnightcontrols.neoforge;

import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

import static eu.midnightdust.midnightcontrols.MidnightControlsConstants.NAMESPACE;

@Mod(value = NAMESPACE)
public class MidnightControlsNeoForge {
    public MidnightControlsNeoForge() {
        MidnightControls.init();
    }

    @Mod(value = NAMESPACE, dist = Dist.CLIENT)
    public static class YourModClientNeoforge {
        public YourModClientNeoforge() {
            MidnightControlsClient.initClient();
        }
    }
}