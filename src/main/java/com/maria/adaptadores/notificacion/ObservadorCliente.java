package com.maria.adaptadores.notificacion;

import com.maria.dominio.enums.EstadoConexion;
import com.maria.dominio.modelos.EventoCliente;
import com.maria.dominio.modelos.ResultadoConversion;

public interface ObservadorCliente {
    void onEstado(EstadoConexion estado, String endpoint);
    void onEvento(EventoCliente evento);
    void onResultado(ResultadoConversion resultado);
    void onError(String mensaje);
}
