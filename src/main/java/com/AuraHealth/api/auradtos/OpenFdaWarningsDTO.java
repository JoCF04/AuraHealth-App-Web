package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenFdaWarningsDTO {

    /** false cuando openFDA no tiene datos para ese medicamento. */
    private boolean encontrado;

    private List<String> warnings;
    private List<String> doNotUse;
    private List<String> askDoctor;
}
