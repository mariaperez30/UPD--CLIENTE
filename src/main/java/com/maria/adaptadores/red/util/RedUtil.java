package com.maria.adaptadores.red.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class RedUtil {
    private RedUtil() {
    }

    public static String obtenerIpLocal() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (final UnknownHostException excepcion) {
            return "127.0.0.1";
        }
    }
}
