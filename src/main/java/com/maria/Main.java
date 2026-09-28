package com.maria;

import com.maria.adaptadores.notificacion.AdaptadorNotificacionCliente;
import com.maria.adaptadores.red.AdaptadorClienteUdp;
import com.maria.adaptadores.red.CanalUdp;
import com.maria.adaptadores.red.ProtocoloUdpMapper;
import com.maria.aplicacion.mapper.ClienteMapper;
import com.maria.aplicacion.servicios.ClienteConversionService;
import com.maria.entrypoint.gui.ClienteFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    private static final System.Logger LOG = System.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(final String[] args) {
        aplicarLookAndFeel();

        final CanalUdp canal = new CanalUdp();
        final ProtocoloUdpMapper protocoloMapper = new ProtocoloUdpMapper();
        final AdaptadorClienteUdp clienteUdp = new AdaptadorClienteUdp(canal, protocoloMapper);

        final AdaptadorNotificacionCliente notificador = new AdaptadorNotificacionCliente();
        final ClienteMapper clienteMapper = new ClienteMapper();

        final ClienteConversionService servicio = new ClienteConversionService(clienteUdp, notificador, clienteMapper);

        SwingUtilities.invokeLater(() -> {
            final ClienteFrame frame = new ClienteFrame(servicio, servicio);
            notificador.registrarObservador(frame);
            frame.setVisible(true);
        });
    }

    private static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final ClassNotFoundException
                       | InstantiationException
                       | IllegalAccessException
                       | UnsupportedLookAndFeelException excepcion) {
            LOG.log(System.Logger.Level.DEBUG, "No se pudo aplicar la apariencia del sistema.", excepcion);
        }
    }
}
