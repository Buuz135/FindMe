package com.buuz135.findme.proxy;


import net.minecraftforge.common.ForgeConfigSpec;

public class FindMeConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static Common COMMON = new Common();

    public static class Common {
        public ForgeConfigSpec.ConfigValue<Integer> RADIUS_RANGE;
        public ForgeConfigSpec.ConfigValue<Boolean> IGNORE_ITEM_DAMAGE;

        public Common() {
            BUILDER.push("COMMON");
            RADIUS_RANGE = BUILDER.comment("Checking radius centered on the player").defineInRange("RADIUS_RANGE", 8, 0, Integer.MAX_VALUE);
            IGNORE_ITEM_DAMAGE = BUILDER.comment("Checks item damage or not").define("IGNORE_ITEM_DAMAGE", false);
            BUILDER.pop();
        }
    }

}
