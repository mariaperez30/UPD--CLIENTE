package com.maria.dominio.modelos;

import java.time.LocalDateTime;

public record EventoCliente(
        LocalDateTime fechaHora,
        String categoria,
        String descripcion
) {
    public EventoCliente(final String categoria, final String descripcion) {
        this(LocalDateTime.now(), categoria, descripcion);
    }
}
