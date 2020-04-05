package com.buuz135.findme.proxy;


import com.buuz135.findme.FindMe;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.awt.*;

public class FindMeConfig {

    public static Common COMMON = new Common();
    public static Client CLIENT = new Client();

    private static abstract class ConfigClass {
        public ForgeConfigSpec SPEC;

        public abstract void onConfigReload (ModConfig.ConfigReloading event);
    }

    public static class Client extends ConfigClass {
        public static final Color DEFAULT_COLOR = Color.decode("#cf9d15");
        private Color currentColor = null;

        public ForgeConfigSpec.ConfigValue<Integer> CONTAINER_TRACK_TIME;
        public ForgeConfigSpec.ConfigValue<Boolean> CONTAINER_TRACKING;
        public ForgeConfigSpec.ConfigValue<String> CONTAINER_HIGHLIGHT;

        // This exists purely to prevent frequent checks of CONTAINER_TRACKING on the client side.
        private int doTracking = -1;

        public Client() {
            final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
            BUILDER.push("client");
            CONTAINER_TRACK_TIME = BUILDER.comment("How many ticks the searched-for item will be highlighted in containers").defineInRange("CONTAINER_TRACK_TIME", 30 * 20, 0, Integer.MAX_VALUE);
            CONTAINER_TRACKING = BUILDER.comment("Whether or not tracked items will have their backgrounds highlighted").define("CONTAINER_TRACKING", true);
            CONTAINER_HIGHLIGHT = BUILDER.comment("Colour used to highlight container background slots when a tracked item is contained within").define("CONTAINER_HIGHLIGHT", "#cf9d15");
            BUILDER.pop();
            SPEC = BUILDER.build();
        }

        public Color getColor() {
            if (currentColor == null) {
                try {
                    currentColor = Color.decode(CONTAINER_HIGHLIGHT.get().toLowerCase());
                } catch (NumberFormatException e) {
                    FindMe.LOG.error("Unable to parse color value '" + CONTAINER_HIGHLIGHT.get() + "'", e);
                    currentColor = DEFAULT_COLOR;
                }
            }
            return currentColor;
        }

        public boolean doTracking () {
            if (doTracking == -1) {
                doTracking = CONTAINER_TRACKING.get() ? 1 : 0;
            }
            return doTracking == 1;
        }

        public void onConfigReload (ModConfig.ConfigReloading event) {
            if (event.getConfig().getType() == ModConfig.Type.CLIENT) {
                SPEC.setConfig(event.getConfig().getConfigData());
                currentColor = null;
                doTracking = -1;
            }
        }
    }

    public static class Common {
        public ForgeConfigSpec.ConfigValue<Integer> RADIUS_RANGE;
        public ForgeConfigSpec.ConfigValue<Boolean> IGNORE_ITEM_DAMAGE;

        public ForgeConfigSpec SPEC;

        public Common() {
            final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
            BUILDER.push("COMMON");
            RADIUS_RANGE = BUILDER.comment("Checking radius centered on the player").defineInRange("RADIUS_RANGE", 8, 0, Integer.MAX_VALUE);
            IGNORE_ITEM_DAMAGE = BUILDER.comment("Checks item damage or not").define("IGNORE_ITEM_DAMAGE", false);
            SPEC = BUILDER.build();
            BUILDER.pop();
        }

        public void onConfigReload (ModConfig.ConfigReloading event) {
            if (event.getConfig().getType() == ModConfig.Type.COMMON) {
                SPEC.setConfig(event.getConfig().getConfigData());
            }
        }
    }
}
