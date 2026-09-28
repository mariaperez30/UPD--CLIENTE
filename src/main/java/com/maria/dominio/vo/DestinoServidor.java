package com.maria.dominio.vo;

import com.maria.dominio.excepciones.DestinoIncorrectoException;
import java.util.Objects;

public record DestinoServidor(String host, int puerto) {
    public DestinoServidor {
        if (Objects.isNull(host) || host.isBlank()) {
            throw new DestinoIncorrectoException("El host del servidor es obligatorio.");
        }
        if (puerto <= 0 || puerto > 65535) {
            throw new DestinoIncorrectoException("El puerto del servidor debe estar entre 1 y 65535.");
        }
    }
}
