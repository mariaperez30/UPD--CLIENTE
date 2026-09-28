package com.maria.aplicacion.mapper;

import com.maria.aplicacion.dto.ConectarCommand;
import com.maria.aplicacion.dto.ConvertirPesoCommand;
import com.maria.dominio.modelos.DatosConversion;
import com.maria.dominio.vo.DestinoServidor;
import com.maria.dominio.vo.Peso;
import java.util.Objects;

public final class ClienteMapper {
    public DestinoServidor toDestino(final ConectarCommand comando) {
        Objects.requireNonNull(comando, "El comando de conexión no puede ser nulo.");
        return new DestinoServidor(comando.host(), comando.puerto());
    }

    public DatosConversion toDatosConversion(final ConvertirPesoCommand comando) {
        Objects.requireNonNull(comando, "El comando de conversión no puede ser nulo.");
        return new DatosConversion(new Peso(comando.pesoLibras()));
    }
}
