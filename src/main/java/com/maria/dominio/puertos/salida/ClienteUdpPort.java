package com.maria.dominio.puertos.salida;

import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.modelos.ResultadoConversion;
import com.maria.dominio.vo.DestinoServidor;

public interface ClienteUdpPort {
    void conectar(DestinoServidor destino);
    void desconectar();
    ResultadoConversion enviarConversion(DatosConversion datos);
}
