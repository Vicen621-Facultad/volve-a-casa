package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE, name = "AuthController")
@Tag(name = "Autenticación", description = "API para autenticación de usuarios")
public class AuthController {
    private UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "Autenticar usuario", description = "Autentica un usuario mediante email y contraseña. Retorna un token en el header si la autenticación es exitosa. "
            +
            "El token generado tiene el formato: {userId}123456 y debe ser usado en los endpoints que requieren autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa - Token incluido en el header 'token'", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\": \"Autenticación exitosa\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado - El email no está registrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Usuario no encontrado\", \"message\": \"El correo ingresado no corresponde a ningún usuario registrado\"}"))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Credenciales incorrectas o usuario deshabilitado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Acceso denegado\", \"message\": \"credenciales incorrectas o usuario deshabilitado\"}")))
    })
    // TODO: Testear este metodo
    @PostMapping
    public ResponseEntity<Map<String, String>> authenticateUser(
            @Parameter(description = "Email del usuario", required = true, example = "usuario@example.com") @RequestHeader("email") String mail,
            @Parameter(description = "Contraseña del usuario", required = true, example = "password123") @RequestHeader("password") String password) {
        Optional<User> optionalUser = userRepository.findByEmail(mail);
        Map<String, String> response = new HashMap<>();
        if (optionalUser.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            response.put("message", "El correo ingresado no corresponde a ningún usuario registrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = optionalUser.get();

        if (!user.checkPassword(password) || !user.isEnabled()) {
            response.put("error", "Acceso denegado");
            response.put("message", "credenciales incorrectas o usuario deshabilitado");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        response.put("message", "Autenticación exitosa");
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", optionalUser.get().getId() + "123456");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
