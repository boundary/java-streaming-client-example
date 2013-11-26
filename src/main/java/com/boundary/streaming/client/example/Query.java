package com.boundary.streaming.client.example;

public interface Query {

    public String getQueryUrl();

    /**
     * Query.Type
     */
    public enum Type {

        total {
            @Override
            public String getQueryParameter() {
                return "total";
            }
        },
        ip {
            @Override
            public String getQueryParameter() {
                return "ip";
            }
        },
        portProtocol {
            @Override
            public String getQueryParameter() {
                return "port_protocol";
            }
        },
        country {
            @Override
            public String getQueryParameter() {
                return "country";
            }
        },
        asn {
            @Override
            public String getQueryParameter() {
                return "asn";
            }
        };

        public abstract String getQueryParameter();
    }

    /**
     * Query.Resolution
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

}
