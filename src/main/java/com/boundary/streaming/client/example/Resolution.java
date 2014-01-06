package com.boundary.streaming.client.example;

/**
 * Resolution
 */
public enum Resolution {
    second {
        @Override
        public String getResolution() {
            return "1s";
        }
    },
    minute {
        @Override
        public String getResolution() {
            return "1m";
        }
    },
    hour {
        @Override
        public String getResolution() {
            return "1h";
        }
    };

    public abstract String getResolution();
}
