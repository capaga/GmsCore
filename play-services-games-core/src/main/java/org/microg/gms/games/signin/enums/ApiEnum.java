package org.microg.gms.games.signin.enums;

public enum ApiEnum {

    UNKNOWN(0),
    SCOPE(1),
    EXPIRATION(2);

    public final int value;

    ApiEnum(int i) {
        this.value = i;
    }

    public static ApiEnum set(int i) {
        switch (i) {
            case 0:
                return UNKNOWN;
            case 1:
                return SCOPE;
            case 2:
                return EXPIRATION;
            default:
                return null;
        }
    }

}
