package com.AuraHealth.api.auradtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenFdaInfoDTO {

    /** false cuando openFDA no tiene datos para ese medicamento. */
    private boolean encontrado;

    private List<String> indications;
    private List<String> dosage;
    private List<String> purpose;
}
