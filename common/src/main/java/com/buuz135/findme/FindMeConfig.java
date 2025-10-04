package com.buuz135.findme;

import java.awt.*;

public class FindMeConfig {

    public Common COMMON = new Common();
    public Client CLIENT = new Client();


    public static class Client {

        public int CONTAINER_TRACK_TIME = 30 * 20;
        public boolean CONTAINER_TRACKING = true;
        public String CONTAINER_HIGHLIGHT_COLOR = "#cf9d15";
        private transient Color currentColor = null;
        public String PARTICLE_HIGHLIGHT_COLOR = "#ffffff";
        private transient Color currentParticleColor = null;


        public Color getColor() {
            if (currentColor == null) {
                try {
                    currentColor = Color.decode(CONTAINER_HIGHLIGHT_COLOR.toLowerCase());
                } catch (NumberFormatException e) {
                    //FindMe.LOG.error("Unable to parse color value '" + CONTAINER_HIGHLIGHT_COLOR.get() + "'", e);
                    currentColor = Color.decode("#cf9d15");
                }
            }
            return currentColor;
        }

        public Color getParticleColor() {
            if (currentParticleColor == null) {
                try {
                    currentParticleColor = Color.decode(PARTICLE_HIGHLIGHT_COLOR.toLowerCase());
                } catch (NumberFormatException e) {
                    //FindMe.LOG.error("Unable to parse color value '" + PARTICLE_HIGHLIGHT_COLOR.get() + "'", e);
                    currentParticleColor = Color.decode("#ffffff");
                }
            }
            return currentParticleColor;
        }


    }

    public static class Common {
        public int RADIUS_RANGE = 8;
        public boolean IGNORE_ITEM_DAMAGE = false;

    }
}
