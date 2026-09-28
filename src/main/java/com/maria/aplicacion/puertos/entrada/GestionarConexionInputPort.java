package com.maria.aplicacion.puertos.entrada;

import com.maria.aplicacion.dto.ConectarCommand;

public interface GestionarConexionInputPort {
    void conectar(ConectarCommand comando);
    void desconectar();
    boolean estaConectado();
}
