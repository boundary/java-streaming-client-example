package com.boundary.streaming.client.example;

/**
 * Type
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
