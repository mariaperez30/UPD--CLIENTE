package com.maria.aplicacion.puertos.entrada;

import com.maria.aplicacion.dto.ConvertirPesoCommand;

public interface ConvertirPesoInputPort {
    void convertir(ConvertirPesoCommand comando);
}
