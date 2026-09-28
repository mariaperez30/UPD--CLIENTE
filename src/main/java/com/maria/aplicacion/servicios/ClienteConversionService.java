package com.maria.aplicacion.servicios;

import com.maria.aplicacion.dto.ConectarCommand;
import com.maria.aplicacion.dto.ConvertirPesoCommand;
import com.maria.aplicacion.mapper.ClienteMapper;
import com.maria.aplicacion.puertos.entrada.ConvertirPesoInputPort;
import com.maria.aplicacion.puertos.entrada.GestionarConexionInputPort;
import com.maria.dominio.enums.EstadoConexion;
import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.modelos.EventoCliente;
import com.maria.dominio.modelos.ResultadoConversion;
import com.maria.dominio.puertos.salida.ClienteUdpPort;
import com.maria.dominio.puertos.salida.PuertoNotificacionCliente;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ClienteConversionService implements GestionarConexionInputPort, ConvertirPesoInputPort {
    private final ClienteUdpPort clienteUdp;
    private final PuertoNotificacionCliente notificador;
    private final ClienteMapper mapper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean conectado = false;

    public ClienteConversionService(
            final ClienteUdpPort clienteUdp,
            final PuertoNotificacionCliente notificador,
            final ClienteMapper mapper) {
        this.clienteUdp = Objects.requireNonNull(clienteUdp, "El puerto de cliente UDP es obligatorio.");
        this.notificador = Objects.requireNonNull(notificador, "El puerto de notificación es obligatorio.");
        this.mapper = Objects.requireNonNull(mapper, "El mapper del cliente es obligatorio.");
    }

    @Override
    public void conectar(final ConectarCommand comando) {
        Objects.requireNonNull(comando, "El comando de conexión es obligatorio.");
        notificador.notificarEstado(EstadoConexion.CONECTANDO, comando.host() + ":" + comando.puerto());
        executor.submit(() -> {
            try {
                clienteUdp.conectar(mapper.toDestino(comando));
                conectado = true;
                notificador.notificarEstado(EstadoConexion.CONECTADO, comando.host() + ":" + comando.puerto());
                notificador.notificarEvento(new EventoCliente("RED", "Conectado al servidor UDP exitosamente."));
            } catch (final Exception excepcion) {
                conectado = false;
                notificador.notificarEstado(EstadoConexion.DESCONECTADO, "");
                notificador.notificarError(excepcion.getMessage());
            }
        });
    }

    @Override
    public void desconectar() {
        executor.submit(() -> {
            try {
                clienteUdp.desconectar();
            } finally {
                conectado = false;
                notificador.notificarEstado(EstadoConexion.DESCONECTADO, "");
                notificador.notificarEvento(new EventoCliente("RED", "Desconectado del servidor UDP."));
            }
        });
    }

    @Override
    public boolean estaConectado() {
        return conectado;
    }

    @Override
    public void convertir(final ConvertirPesoCommand comando) {
        Objects.requireNonNull(comando, "El comando de conversión es obligatorio.");
        if (!conectado) {
            notificador.notificarError("Debe conectar primero con el servidor UDP.");
            return;
        }
        executor.submit(() -> {
            try {
                final DatosConversion datos = mapper.toDatosConversion(comando);
                final ResultadoConversion resultado = clienteUdp.enviarConversion(datos);
                notificador.notificarResultado(resultado);
                notificador.notificarEvento(new EventoCliente(
                        "CONVERSION",
                        "Respuesta recibida: " + resultado.pesoLibrasFormateado() + " lbs = " + resultado.pesoKgFormateado() + " kg"
                ));
            } catch (final Exception excepcion) {
                notificador.notificarError(excepcion.getMessage());
            }
        });
    }
}
