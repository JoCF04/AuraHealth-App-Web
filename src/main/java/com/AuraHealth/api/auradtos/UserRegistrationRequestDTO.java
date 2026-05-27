package com.AuraHealth.api.auradtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Formato de correo electrónico inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private String birthDate;

    private String gender;

    private String preferredLanguage = "es";

    @Pattern(regexp = "USER|DOCTOR|ADMIN",
             message = "Rol inválido. Valores aceptados: USER, DOCTOR, ADMIN")
    private String role = "USER";
}