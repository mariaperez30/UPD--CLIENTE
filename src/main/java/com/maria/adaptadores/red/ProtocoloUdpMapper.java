package com.maria.adaptadores.red;

import com.maria.dominio.excepciones.RespuestaServidorException;
import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.modelos.ResultadoConversion;

import java.util.Locale;
import java.util.Objects;

public final class ProtocoloUdpMapper {
    public static final String CONECTAR = "CONECTAR";
    public static final String DESCONECTAR = "DESCONECTAR";

    public String serializarConversion(final DatosConversion datos) {
        Objects.requireNonNull(datos, "Los datos de conversión son obligatorios.");
        return String.format(Locale.US, "CONVERTIR;%.2f", datos.peso().valor());
    }

    public void validarConexion(final String respuesta) {
        if (Objects.isNull(respuesta) || !respuesta.startsWith("CONECTADO_OK;")) {
            throw new RespuestaServidorException("Respuesta de conexión inesperada: " + respuesta);
        }
    }

    public ResultadoConversion parsearConversion(final String respuesta) {
        if (Objects.isNull(respuesta) || respuesta.isBlank()) {
            throw new RespuestaServidorException("El servidor envió una respuesta vacía.");
        }
        if (respuesta.startsWith("ERROR;")) {
            throw new RespuestaServidorException("Error del servidor: " + respuesta.substring(6));
        }
        final String[] partes = respuesta.split(";", -1);
        if (partes.length < 3 || (!"OK_CONVERSION".equals(partes[0]) && !"OK_CALCULO".equals(partes[0]))) {
            throw new RespuestaServidorException("Respuesta de conversión no estructurada: " + respuesta);
        }
        try {
            final double pesoKg = Double.parseDouble(partes[1].replace(',', '.'));
            final String pesoKgStr = partes[1];
            final String pesoLbsStr = partes[2];
            final String mensaje = partes.length >= 4 ? partes[3] : "Conversión exitosa";
            return new ResultadoConversion(pesoKg, pesoKgStr, pesoLbsStr, mensaje);
        } catch (final NumberFormatException excepcion) {
            throw new RespuestaServidorException("El peso en kilogramos recibido no es numérico.");
        }
    }
}
