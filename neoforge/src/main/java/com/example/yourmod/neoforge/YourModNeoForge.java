package com.example.yourmod.neoforge;

import com.example.yourmod.YourMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

import static com.example.yourmod.YourMod.MOD_ID;

@Mod(value = MOD_ID)
public class YourModNeoForge {
    public YourModNeoForge() {
        YourMod.init();
    }

    @Mod(value = MOD_ID, dist = Dist.CLIENT)
    public static class YourModClientNeoforge {
        public YourModClientNeoforge() {
            YourMod.initClient();
        }
    }

    @Mod(value = MOD_ID, dist = Dist.DEDICATED_SERVER)
    public static class YourModServerNeoforge {
        public YourModServerNeoforge() {
            YourMod.initServer();
        }
    }
}