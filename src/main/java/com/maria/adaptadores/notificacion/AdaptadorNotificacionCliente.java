package com.maria.adaptadores.notificacion;

import com.maria.dominio.enums.EstadoConexion;
import com.maria.dominio.modelos.EventoCliente;
import com.maria.dominio.modelos.ResultadoConversion;
import com.maria.dominio.puertos.salida.PuertoNotificacionCliente;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AdaptadorNotificacionCliente implements PuertoNotificacionCliente {
    private final List<ObservadorCliente> observadores = new CopyOnWriteArrayList<>();

    public void registrarObservador(final ObservadorCliente observador) {
        if (Objects.nonNull(observador)) {
            observadores.add(observador);
        }
    }

    public void removerObservador(final ObservadorCliente observador) {
        if (Objects.nonNull(observador)) {
            observadores.remove(observador);
        }
    }

    @Override
    public void notificarEstado(final EstadoConexion estado, final String endpoint) {
        observadores.forEach(obs -> obs.onEstado(estado, endpoint));
    }

    @Override
    public void notificarEvento(final EventoCliente evento) {
        observadores.forEach(obs -> obs.onEvento(evento));
    }

    @Override
    public void notificarResultado(final ResultadoConversion resultado) {
        observadores.forEach(obs -> obs.onResultado(resultado));
    }

    @Override
    public void notificarError(final String mensaje) {
        observadores.forEach(obs -> obs.onError(mensaje));
    }
}
