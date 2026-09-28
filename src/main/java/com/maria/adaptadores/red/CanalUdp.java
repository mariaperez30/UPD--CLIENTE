package com.maria.adaptadores.red;

import com.maria.aplicacion.excepciones.ClienteRedException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class CanalUdp {
    private DatagramSocket socket;

    public void abrir() {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket();
                socket.setSoTimeout(3000);
            }
        } catch (final IOException excepcion) {
            throw new ClienteRedException("No se pudo abrir el socket UDP del cliente", excepcion);
        }
    }

    public String enviarYRecibir(final String host, final int puerto, final String mensaje) {
        abrir();
        try {
            final byte[] envio = mensaje.getBytes(StandardCharsets.UTF_8);
            final InetAddress ip = InetAddress.getByName(host);
            final DatagramPacket paqueteEnvio = new DatagramPacket(envio, envio.length, ip, puerto);
            socket.send(paqueteEnvio);

            final byte[] recepcion = new byte[4096];
            final DatagramPacket paqueteRecepcion = new DatagramPacket(recepcion, recepcion.length);
            socket.receive(paqueteRecepcion);
            return new String(paqueteRecepcion.getData(), 0, paqueteRecepcion.getLength(), StandardCharsets.UTF_8);
        } catch (final IOException excepcion) {
            throw new ClienteRedException("Error en la comunicación UDP con " + host + ":" + puerto, excepcion);
        }
    }

    public void cerrar() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean estaAbierto() {
        return socket != null && !socket.isClosed();
    }
}
