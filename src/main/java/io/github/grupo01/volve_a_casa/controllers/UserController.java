package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.UserCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.UserUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE, name = "UserRestController")
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios ordenados alfabéticamente por nombre. "
            +
            "Tests: UserControllerTest.listAllUsersOrderByName_whenEmpty_returnsNoContent(), " +
            "UserControllerTest.listAllUsersOrderByName_whenUsersExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "204", description = "No hay usuarios registrados")
    })
    @GetMapping
    public ResponseEntity<List<User>> listAllUsersOrderByName() {
        List<User> users = userRepository.findAll(Sort.by("name"));
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en el sistema. El email debe ser único. "
            +
            "Tests: UserControllerTest.createUser_whenUserDoesNotExist_returnsCreated(), " +
            "UserControllerTest.createUser_whenUserExists_returnsConflict()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserCreateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    // TODO: testear este metodo entero
    @PostMapping
    public ResponseEntity<?> createUser(
            @Parameter(description = "Datos del usuario a crear", required = true) @RequestBody UserCreateDTO userCreateDTO) {
        Map<String, String> response = new HashMap<>();
        if (!userCreateDTO.isValid()) {
            response.put("error", "Datos inválidos");
            response.put("message", "Faltan campos obligatorios para crear el usuario");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (userRepository.findByEmail(userCreateDTO.email()).isPresent()) {
            response.put("error", "Email repetido");
            response.put("message", "El email ya está siendo utilizado por otro usuario");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        userRepository.save(new User(userCreateDTO));
        return new ResponseEntity<>(userCreateDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico (requiere token de autenticación en formato {userId}123456). "
            +
            "Tests: UserControllerTest.getUserById_whenUserExistsAndTokenValid_returnsOk(), " +
            "UserControllerTest.getUserById_whenTokenInvalid_returnsUnauthorized(), " +
            "UserControllerTest.getUserById_whenUserDoesNotExist_returnsNotFound()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o no proporcionado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "Token de autenticación (formato: {userId}123456)", required = true, example = "1123456") @RequestHeader("token") String token,
            @Parameter(description = "ID del usuario", required = true, example = "1") @PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();
        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            response.put("error", "User no encontrado");
            response.put("message", "No existe un usuario con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(user.get());
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario. Solo puede actualizar el usuario autenticado mediante token. "
            +
            "Tests: UserControllerTest.updateUser_whenTokenValid_returnsOk()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o no proporcionado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "Token de autenticación (formato: {userId}123456)", required = true, example = "1123456") @RequestHeader("token") String token,
            @Parameter(description = "Datos actualizados del usuario", required = true) @RequestBody UserUpdateDTO updatedData) {
        Map<String, String> response = new HashMap<>();

        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Optional<User> optionalUser = userRepository.findById(Long.valueOf(token.replace("123456", "")));

        if (optionalUser.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        user.updateFromDTO(updatedData);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    private boolean checkToken(String token) {
        return token != null && token.endsWith("123456")
                && userRepository.existsById(Long.valueOf(token.replace("123456", "")));
    }

}
