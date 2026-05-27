package com.AuraHealth.api.auracontrollers;

import com.AuraHealth.api.auraentities.User;
import com.AuraHealth.api.auradtos.JwtRequestDTO;
import com.AuraHealth.api.auradtos.JwtResponseDTO;
import com.AuraHealth.api.aurasecurity.JwtTokenUtil;
import com.AuraHealth.api.aurasecurity.JwtUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth · Autenticación JWT",
     description = "HU02 — Login con token JWT. Usar token en: Authorization: Bearer <token>")
public class AuthController {

    private final AuthenticationManager  authenticationManager;
    private final JwtTokenUtil           jwtTokenUtil;
    private final JwtUserDetailsService  userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenUtil jwtTokenUtil,
                          JwtUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil          = jwtTokenUtil;
        this.userDetailsService    = userDetailsService;
    }

    // ── HU02 — Login ─────────────────────────────────────────────────────────

    @Operation(summary = "HU02 — Iniciar sesión y obtener token JWT",
               description = "Acceso: PÚBLICO — no requiere token.")
    @ApiResponses({
        @ApiResponse(responseCode = "200",
            description = "Login exitoso. Copiar 'token' y usarlo en el botón Authorize de Swagger."),
        @ApiResponse(responseCode = "401", description = "Credenciales incorrectas.")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody JwtRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        User user = userDetailsService.getUser(dto.getEmail());
        String token = jwtTokenUtil.generateToken(userDetails);
        String role = user.getRoles().stream()
                .map(r -> r.getName())
                .findFirst()
                .orElse("ROLE_USER");

        return ResponseEntity.ok(new JwtResponseDTO(token, user.getId(), user.getEmail(), role));
    }
}
