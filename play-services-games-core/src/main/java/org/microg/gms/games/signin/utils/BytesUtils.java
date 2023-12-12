package org.microg.gms.games.signin.utils;

import android.util.Base64;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BytesUtils {
    public static String bytesToHex(byte[] bytes) {
        return new BigInteger(1, bytes).toString(16);
    }

    public static String bytesToBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String generateSessionId() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(System.currentTimeMillis());
        byte[] bArr = new byte[16];
        secureRandom.nextBytes(bArr);
        return Base64.encodeToString(bArr, 11);
    }

    public static byte[] base64ToBytes(String base64) {
        return Base64.decode(base64, Base64.URL_SAFE);
    }
}
