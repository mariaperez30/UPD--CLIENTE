package com.maria.dominio.excepciones;

import java.io.Serial;

public class PesoIncorrectoException extends DominioException {
    @Serial
    private static final long serialVersionUID = 1L;

    public PesoIncorrectoException(final String mensaje) {
        super(mensaje);
    }
}
