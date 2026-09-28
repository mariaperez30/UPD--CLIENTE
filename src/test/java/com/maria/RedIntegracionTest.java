package com.maria;

import com.maria.adaptadores.red.AdaptadorClienteUdp;
import com.maria.adaptadores.red.CanalUdp;
import com.maria.adaptadores.red.ProtocoloUdpMapper;
import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.modelos.ResultadoConversion;
import com.maria.dominio.vo.DestinoServidor;
import com.maria.dominio.vo.Peso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RedIntegracionTest {

    @Test
    @DisplayName("Debe realizar integración real UDP entre Cliente y Mock DatagramSocket Server")
    void debeIntegrarUdpConMockServer() throws Exception {
        final int puertoTest = 9988;
        final Thread mockServerThread = new Thread(() -> {
            try (DatagramSocket serverSocket = new DatagramSocket(puertoTest)) {
                final byte[] buf = new byte[1024];
                while (!Thread.currentThread().isInterrupted()) {
                    final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    serverSocket.receive(packet);
                    final String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    byte[] res;
                    if ("CONECTAR".equals(msg)) {
                        res = "CONECTADO_OK;Listo".getBytes(StandardCharsets.UTF_8);
                    } else if (msg.startsWith("CONVERTIR;")) {
                        res = "OK_CONVERSION;45.36;100.00;Exito".getBytes(StandardCharsets.UTF_8);
                    } else {
                        res = "DESCONECTADO_OK;Bye".getBytes(StandardCharsets.UTF_8);
                    }
                    final DatagramPacket outPacket = new DatagramPacket(res, res.length, packet.getAddress(), packet.getPort());
                    serverSocket.send(outPacket);
                    if ("DESCONECTAR".equals(msg)) {
                        break;
                    }
                }
            } catch (IOException ignored) {
            }
        });
        mockServerThread.start();
        Thread.sleep(100);

        final CanalUdp canal = new CanalUdp();
        final ProtocoloUdpMapper mapper = new ProtocoloUdpMapper();
        final AdaptadorClienteUdp cliente = new AdaptadorClienteUdp(canal, mapper);

        cliente.conectar(new DestinoServidor("127.0.0.1", puertoTest));

        final ResultadoConversion res = cliente.enviarConversion(new DatosConversion(new Peso(100.0)));
        assertNotNull(res);
        assertEquals(45.36, res.pesoKg(), 0.001);

        cliente.desconectar();
        mockServerThread.interrupt();
    }
}
