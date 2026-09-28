package com.maria;

import com.maria.adaptadores.notificacion.AdaptadorNotificacionCliente;
import com.maria.adaptadores.notificacion.ObservadorCliente;
import com.maria.adaptadores.red.util.RedUtil;
import com.maria.dominio.enums.EstadoConexion;
import com.maria.dominio.modelos.EventoCliente;
import com.maria.dominio.modelos.ResultadoConversion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificacionYUtilTest {

    @Test
    @DisplayName("Debe probar RedUtil ip local")
    void debeProbarRedUtil() {
        assertNotNull(RedUtil.obtenerIpLocal());
    }

    @Test
    @DisplayName("Debe notificar observadores en AdaptadorNotificacionCliente")
    void debeNotificarObservadores() {
        final AdaptadorNotificacionCliente notificador = new AdaptadorNotificacionCliente();
        final AtomicBoolean notificado = new AtomicBoolean(false);

        final ObservadorCliente obs = new ObservadorCliente() {
            @Override
            public void onEstado(EstadoConexion estado, String endpoint) {
                notificado.set(true);
            }

            @Override
            public void onEvento(EventoCliente evento) {
            }

            @Override
            public void onResultado(ResultadoConversion resultado) {
            }

            @Override
            public void onError(String mensaje) {
            }
        };

        notificador.registrarObservador(obs);
        notificador.notificarEstado(EstadoConexion.CONECTADO, "127.0.0.1:9876");
        assertTrue(notificado.get());

        notificador.removerObservador(obs);
    }
}
