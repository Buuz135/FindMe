package com.buuz135.findme.proxy;

import com.buuz135.findme.FindMe;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = FindMe.MOD_ID)
public class FindMeConfig {

    @Config.Comment("Checking radius centered on the player")
    @Config.RangeInt(min = 0)
    public static int RADIUS_RANGE = 8;

    @Config.Comment("Whether ignore item damage on search or not")
    public static boolean IGNORE_ITEM_DAMAGE = false;

    @Mod.EventBusSubscriber(modid = FindMe.MOD_ID)
    private static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(FindMe.MOD_ID)) {
                ConfigManager.sync(FindMe.MOD_ID, Config.Type.INSTANCE);
            }
        }
    }
}
