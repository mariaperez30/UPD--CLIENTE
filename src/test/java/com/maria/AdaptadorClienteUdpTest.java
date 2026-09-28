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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdaptadorClienteUdpTest {

    @Test
    @DisplayName("Debe conectar y enviar trama de conversión mediante adaptador")
    void debeConectarYEnviar() {
        final MockCanalUdp canal = new MockCanalUdp();
        final ProtocoloUdpMapper mapper = new ProtocoloUdpMapper();
        final AdaptadorClienteUdp adaptador = new AdaptadorClienteUdp(canal, mapper);

        final DestinoServidor destino = new DestinoServidor("127.0.0.1", 9876);
        adaptador.conectar(destino);

        final ResultadoConversion res = adaptador.enviarConversion(new DatosConversion(new Peso(100.0)));
        assertNotNull(res);
        assertEquals(45.36, res.pesoKg(), 0.001);

        adaptador.desconectar();
    }

    @Test
    @DisplayName("Debe fallar al enviar sin haber conectado")
    void debeFallarSinConectar() {
        final CanalUdp canal = new CanalUdp();
        final ProtocoloUdpMapper mapper = new ProtocoloUdpMapper();
        final AdaptadorClienteUdp adaptador = new AdaptadorClienteUdp(canal, mapper);

        assertThrows(IllegalStateException.class, () -> adaptador.enviarConversion(new DatosConversion(new Peso(100.0))));
    }

    static class MockCanalUdp extends CanalUdp {
        @Override
        public String enviarYRecibir(String host, int puerto, String mensaje) {
            if ("CONECTAR".equals(mensaje)) {
                return "CONECTADO_OK;Servidor Listo";
            }
            if (mensaje.startsWith("CONVERTIR;")) {
                return "OK_CONVERSION;45.36;100.00;Exito";
            }
            return "DESCONECTADO_OK;Bye";
        }
    }
}
