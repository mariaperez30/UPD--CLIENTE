package com.maria.adaptadores.red;

import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.modelos.ResultadoConversion;
import com.maria.dominio.puertos.salida.ClienteUdpPort;
import com.maria.dominio.vo.DestinoServidor;

import java.util.Objects;

public final class AdaptadorClienteUdp implements ClienteUdpPort {
    private final CanalUdp canal;
    private final ProtocoloUdpMapper mapper;
    private DestinoServidor destinoActual;

    public AdaptadorClienteUdp(final CanalUdp canal, final ProtocoloUdpMapper mapper) {
        this.canal = Objects.requireNonNull(canal, "El canal UDP es obligatorio.");
        this.mapper = Objects.requireNonNull(mapper, "El mapper del protocolo UDP es obligatorio.");
    }

    @Override
    public void conectar(final DestinoServidor destino) {
        this.destinoActual = Objects.requireNonNull(destino, "El destino es obligatorio.");
        final String respuesta = canal.enviarYRecibir(destino.host(), destino.puerto(), ProtocoloUdpMapper.CONECTAR);
        mapper.validarConexion(respuesta);
    }

    @Override
    public void desconectar() {
        if (destinoActual != null) {
            try {
                canal.enviarYRecibir(destinoActual.host(), destinoActual.puerto(), ProtocoloUdpMapper.DESCONECTAR);
            } catch (final Exception ignored) {
                // Desconexión limpia tolerante a fallos
            }
        }
        canal.cerrar();
        destinoActual = null;
    }

    @Override
    public ResultadoConversion enviarConversion(final DatosConversion datos) {
        if (destinoActual == null) {
            throw new IllegalStateException("No hay una sesión activa con el servidor.");
        }
        final String trama = mapper.serializarConversion(datos);
        final String respuesta = canal.enviarYRecibir(destinoActual.host(), destinoActual.puerto(), trama);
        return mapper.parsearConversion(respuesta);
    }
}
