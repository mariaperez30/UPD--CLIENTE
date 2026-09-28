package com.maria;

import com.maria.aplicacion.dto.ConectarCommand;
import com.maria.aplicacion.dto.ConvertirPesoCommand;
import com.maria.aplicacion.mapper.ClienteMapper;
import com.maria.aplicacion.servicios.ClienteConversionService;
import com.maria.dominio.enums.EstadoConexion;
import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.modelos.EventoCliente;
import com.maria.dominio.modelos.ResultadoConversion;
import com.maria.dominio.puertos.salida.ClienteUdpPort;
import com.maria.dominio.puertos.salida.PuertoNotificacionCliente;
import com.maria.dominio.vo.DestinoServidor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteServiceTest {

    private MockClienteUdpPort clienteUdp;
    private MockPuertoNotificacionCliente notificador;
    private ClienteConversionService servicio;

    @BeforeEach
    void setUp() {
        clienteUdp = new MockClienteUdpPort();
        notificador = new MockPuertoNotificacionCliente();
        final ClienteMapper mapper = new ClienteMapper();
        servicio = new ClienteConversionService(clienteUdp, notificador, mapper);
    }

    @Test
    @DisplayName("Debe ejecutar conectar y desconectar")
    void debeConectarYDesconectar() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        notificador.latch = latch;

        servicio.conectar(new ConectarCommand("127.0.0.1", 9876));
        assertTrue(latch.await(2, TimeUnit.SECONDS));
        assertTrue(servicio.estaConectado());

        final CountDownLatch latchDes = new CountDownLatch(1);
        notificador.latch = latchDes;
        servicio.desconectar();
        assertTrue(latchDes.await(2, TimeUnit.SECONDS));
        assertTrue(!servicio.estaConectado());
    }

    @Test
    @DisplayName("Debe ejecutar caso de uso de conversión")
    void debeEjecutarConversion() throws Exception {
        final CountDownLatch latchCon = new CountDownLatch(1);
        notificador.latch = latchCon;
        servicio.conectar(new ConectarCommand("127.0.0.1", 9876));
        latchCon.await(2, TimeUnit.SECONDS);

        final CountDownLatch latchRes = new CountDownLatch(1);
        notificador.latchRes = latchRes;

        servicio.convertir(new ConvertirPesoCommand(100.0));
        assertTrue(latchRes.await(2, TimeUnit.SECONDS));
        assertTrue(notificador.resultadoRecibido.get());
    }

    static class MockClienteUdpPort implements ClienteUdpPort {
        @Override
        public void conectar(DestinoServidor destino) {
        }

        @Override
        public void desconectar() {
        }

        @Override
        public ResultadoConversion enviarConversion(DatosConversion datos) {
            return new ResultadoConversion(45.36, "45.36", "100.00", "OK");
        }
    }

    static class MockPuertoNotificacionCliente implements PuertoNotificacionCliente {
        CountDownLatch latch;
        CountDownLatch latchRes;
        AtomicBoolean resultadoRecibido = new AtomicBoolean(false);

        @Override
        public void notificarEstado(EstadoConexion estado, String endpoint) {
            if (estado == EstadoConexion.CONECTADO || estado == EstadoConexion.DESCONECTADO) {
                if (latch != null) latch.countDown();
            }
        }

        @Override
        public void notificarEvento(EventoCliente evento) {
        }

        @Override
        public void notificarResultado(ResultadoConversion resultado) {
            resultadoRecibido.set(true);
            if (latchRes != null) latchRes.countDown();
        }

        @Override
        public void notificarError(String mensaje) {
        }
    }
}
