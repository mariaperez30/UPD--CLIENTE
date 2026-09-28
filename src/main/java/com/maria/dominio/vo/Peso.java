package com.maria.dominio.vo;

import com.maria.dominio.excepciones.PesoIncorrectoException;

/**
 * Objeto de valor que representa el peso en libras en el cliente.
 */
public record Peso(double valor) {
    public Peso {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new PesoIncorrectoException("El peso debe ser mayor que cero.");
        }
    }
}
