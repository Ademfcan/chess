package chessserver.Enums;

import java.util.concurrent.TimeUnit;

public enum Gametype {
    REGULAR10(10, TimeUnit.MINUTES, Gamemode.REGULAR, "reg10") {
        @Override
        String stringValue() {
            return this.getStrVersion();
        }
    },
    REGULAR30(30, TimeUnit.MINUTES, Gamemode.REGULAR, "reg30") {
        @Override
        String stringValue() {
            return this.getStrVersion();
        }
    },
    REGULARUNLIMITED(10000, TimeUnit.MINUTES, Gamemode.REGULAR, "regUn") {
        @Override
        String stringValue() {
            return this.getStrVersion();
        }
    };

    private final int length; // in minutes
    private final Gamemode mode;
    private final String strVersion;
    private final TimeUnit timeUnit;

    Gametype(int length, TimeUnit timeUnit, Gamemode mode, String strVersion) {
        this.length = length;
        this.timeUnit = timeUnit;
        this.mode = mode;
        this.strVersion = strVersion;
    }

    public static Gametype getType(String string) {
        for (Gametype g : Gametype.values()) {
            if (g.getStrVersion().equals(string)) {
                return g;
            }
        }
        return null;

    }

    abstract String stringValue();

    public int getLength() {
        return length;
    }

    public Gamemode getMode() {
        return mode;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public String getStrVersion() {
        return strVersion;
    }
}
