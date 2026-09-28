package com.maria.entrypoint.gui;

import com.maria.adaptadores.notificacion.ObservadorCliente;
import com.maria.adaptadores.red.util.RedUtil;
import com.maria.aplicacion.dto.ConectarCommand;
import com.maria.aplicacion.dto.ConvertirPesoCommand;
import com.maria.aplicacion.puertos.entrada.ConvertirPesoInputPort;
import com.maria.aplicacion.puertos.entrada.GestionarConexionInputPort;
import com.maria.dominio.enums.EstadoConexion;
import com.maria.dominio.excepciones.DominioException;
import com.maria.dominio.modelos.EventoCliente;
import com.maria.dominio.modelos.ResultadoConversion;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class ClienteFrame extends JFrame implements ObservadorCliente {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final transient GestionarConexionInputPort conexion;
    private final transient ConvertirPesoInputPort conversion;
    private final JTextField host = new JTextField("127.0.0.1");
    private final JTextField puerto = new JTextField("9876");
    private final JTextField peso = new JTextField();
    private final JButton conectar = new JButton("Conectar");
    private final JButton calcular = new JButton("Convertir a Kg");
    private final JLabel estado = new JLabel("● DESCONECTADO");
    private final JLabel resultado = new JLabel("Kilogramos: --");
    private final JTextArea logs = new JTextArea();

    public ClienteFrame(final GestionarConexionInputPort conexion, final ConvertirPesoInputPort conversion) {
        super("Cliente UDP - Conversión de Libras a Kilogramos (Arquitectura Hexagonal)");
        this.conexion = Objects.requireNonNull(conexion);
        this.conversion = Objects.requireNonNull(conversion);
        construirInterfaz();
    }

    private void construirInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 500);
        setLocationRelativeTo(null);
        final JPanel red = new JPanel(new GridLayout(3, 2, 8, 8));
        red.setBorder(BorderFactory.createTitledBorder("Red - IP local: " + RedUtil.obtenerIpLocal()));
        red.add(new JLabel("Host del servidor:"));
        red.add(host);
        red.add(new JLabel("Puerto:"));
        red.add(puerto);
        red.add(estado);
        red.add(conectar);
        final JPanel datos = new JPanel(new GridLayout(2, 2, 8, 8));
        datos.setBorder(BorderFactory.createTitledBorder("Datos de Entrada (Conversión de Peso)"));
        datos.add(new JLabel("Peso en libras (lbs):"));
        datos.add(peso);
        datos.add(resultado);
        datos.add(calcular);
        final JPanel centro = new JPanel(new GridLayout(2, 1, 8, 8));
        centro.add(red);
        centro.add(datos);
        logs.setEditable(false);
        final JButton limpiar = new JButton("Limpiar log");
        limpiar.addActionListener(evento -> logs.setText(""));
        final JPanel sur = new JPanel(new BorderLayout());
        sur.add(limpiar, BorderLayout.NORTH);
        sur.add(new JScrollPane(logs), BorderLayout.CENTER);
        final JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        contenido.add(centro, BorderLayout.NORTH);
        contenido.add(sur, BorderLayout.CENTER);
        add(contenido);
        conectar.addActionListener(evento -> alternarConexion());
        calcular.addActionListener(evento -> solicitarConversion());
        habilitarConversion(false);
    }

    private void alternarConexion() {
        if (conexion.estaConectado()) {
            conexion.desconectar();
            return;
        }
        try {
            conexion.conectar(new ConectarCommand(host.getText(), Integer.parseInt(puerto.getText().trim())));
        } catch (final NumberFormatException | DominioException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    private void solicitarConversion() {
        try {
            final double valorPeso = Double.parseDouble(peso.getText().trim().replace(',', '.'));
            calcular.setEnabled(false);
            conversion.convertir(new ConvertirPesoCommand(valorPeso));
        } catch (final NumberFormatException | DominioException excepcion) {
            mostrarError(excepcion.getMessage());
        }
    }

    @Override
    public void onEstado(final EstadoConexion nuevoEstado, final String endpoint) {
        SwingUtilities.invokeLater(() -> {
            estado.setText("● " + nuevoEstado + (endpoint.isBlank() ? "" : " - " + endpoint));
            final boolean conectado = nuevoEstado == EstadoConexion.CONECTADO;
            host.setEditable(!conectado);
            puerto.setEditable(!conectado);
            conectar.setText(conectado ? "Desconectar" : "Conectar");
            habilitarConversion(conectado);
        });
    }

    @Override
    public void onEvento(final EventoCliente evento) {
        SwingUtilities.invokeLater(() -> logs.append("[" + evento.fechaHora().format(FECHA) + "] ["
                + evento.categoria() + "] " + evento.descripcion() + System.lineSeparator()));
    }

    @Override
    public void onResultado(final ResultadoConversion valor) {
        SwingUtilities.invokeLater(() -> {
            resultado.setText("Kilogramos: " + valor.pesoKgFormateado() + " kg");
            calcular.setEnabled(true);
        });
    }

    @Override
    public void onError(final String mensaje) {
        SwingUtilities.invokeLater(() -> {
            habilitarConversion(conexion.estaConectado());
            mostrarError(mensaje);
        });
    }

    private void habilitarConversion(final boolean habilitado) {
        peso.setEnabled(habilitado);
        calcular.setEnabled(habilitado);
    }

    private void mostrarError(final String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Cliente UDP", JOptionPane.WARNING_MESSAGE);
    }
}
