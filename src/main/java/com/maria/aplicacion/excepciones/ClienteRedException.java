package com.maria.aplicacion.excepciones;

import java.io.Serial;

public class ClienteRedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ClienteRedException(final String mensaje) {
        super(mensaje);
    }

    public ClienteRedException(final String mensaje, final Throwable causa) {
        super(mensaje, causa);
    }
}
