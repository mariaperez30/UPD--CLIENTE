package com.maria.dominio.modelos;

public record ResultadoConversion(
        double pesoKg,
        String pesoKgFormateado,
        String pesoLibrasFormateado,
        String mensaje
) {
}
