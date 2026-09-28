package com.maria;

import com.maria.adaptadores.red.ProtocoloUdpMapper;
import com.maria.dominio.excepciones.DestinoIncorrectoException;
import com.maria.dominio.excepciones.PesoIncorrectoException;
import com.maria.dominio.excepciones.RespuestaServidorException;
import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.modelos.ResultadoConversion;
import com.maria.dominio.vo.DestinoServidor;
import com.maria.dominio.vo.Peso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DominioYProtocoloTest {

    @Test
    @DisplayName("Debe validar VOs de dominio del cliente")
    void debeValidarVos() {
        final Peso peso = new Peso(100.0);
        assertEquals(100.0, peso.valor());

        assertThrows(PesoIncorrectoException.class, () -> new Peso(0.0));
        assertThrows(PesoIncorrectoException.class, () -> new Peso(-5.0));

        final DestinoServidor destino = new DestinoServidor("localhost", 9876);
        assertEquals("localhost", destino.host());
        assertEquals(9876, destino.puerto());

        assertThrows(DestinoIncorrectoException.class, () -> new DestinoServidor(null, 9876));
        assertThrows(DestinoIncorrectoException.class, () -> new DestinoServidor("localhost", -1));
    }

    @Test
    @DisplayName("Debe serializar y parsear trampas UDP en ProtocoloUdpMapper")
    void debeProbarProtocoloUdpMapper() {
        final ProtocoloUdpMapper mapper = new ProtocoloUdpMapper();
        final DatosConversion datos = new DatosConversion(new Peso(150.0));

        final String trama = mapper.serializarConversion(datos);
        assertEquals("CONVERTIR;150.00", trama);

        mapper.validarConexion("CONECTADO_OK;Listo");
        assertThrows(RespuestaServidorException.class, () -> mapper.validarConexion("ERROR;Conexion rechazada"));

        final ResultadoConversion resultado = mapper.parsearConversion("OK_CONVERSION;68.04;150.00;Exito");
        assertNotNull(resultado);
        assertEquals(68.04, resultado.pesoKg(), 0.001);
        assertEquals("68.04", resultado.pesoKgFormateado());

        assertThrows(RespuestaServidorException.class, () -> mapper.parsearConversion("ERROR;Fallo interno"));
        assertThrows(RespuestaServidorException.class, () -> mapper.parsearConversion("OK_CONVERSION;abc;150.00;Exito"));
    }
}
