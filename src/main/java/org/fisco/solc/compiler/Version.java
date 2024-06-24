package org.fisco.solc.compiler;

public enum Version {
    V0_4_25("0.4.25"),
    V0_5_2("0.5.2"),
    V0_6_10("0.6.10"),
    V0_8_11("0.8.11");

    private String version;

    Version(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.version;
    }
}
