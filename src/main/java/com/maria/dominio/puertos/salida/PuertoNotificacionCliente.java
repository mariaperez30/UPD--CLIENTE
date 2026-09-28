package com.maria.dominio.puertos.salida;

import com.maria.dominio.enums.EstadoConexion;
import com.maria.dominio.modelos.EventoCliente;
import com.maria.dominio.modelos.ResultadoConversion;

public interface PuertoNotificacionCliente {
    void notificarEstado(EstadoConexion estado, String endpoint);
    void notificarEvento(EventoCliente evento);
    void notificarResultado(ResultadoConversion resultado);
    void notificarError(String mensaje);
}
