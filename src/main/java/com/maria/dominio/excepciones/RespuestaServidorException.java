package com.maria.dominio.excepciones;

import java.io.Serial;

public class RespuestaServidorException extends DominioException {
    @Serial
    private static final long serialVersionUID = 1L;

    public RespuestaServidorException(final String mensaje) {
        super(mensaje);
    }
}
