package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.SightingCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.SightingResponseDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.SightingRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/sightings", produces = MediaType.APPLICATION_JSON_VALUE, name = "SightingRestController")
@Tag(name = "Avistamientos", description = "API para gestión de avistamientos de mascotas. Permite reportar y consultar avistamientos.")
public class SightingController {
    private final SightingRepository sightingRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public SightingController(SightingRepository sightingRepository, PetRepository petRepository,
            UserRepository userRepository) {
        this.sightingRepository = sightingRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Listar todos los avistamientos", description = "Obtiene todos los avistamientos ordenados por fecha descendente (más recientes primero). "
            +
            "Tests: SightingControllerTest.listAllSightings_whenEmpty_returnsNoContent(), " +
            "SightingControllerTest.listAllSightings_whenSightingsExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No hay avistamientos registrados")
    })
    @GetMapping
    public ResponseEntity<List<SightingResponseDTO>> listAllSightings() {
        List<Sighting> sightings = sightingRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));

        if (sightings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<SightingResponseDTO> response = sightings.stream()
                .map(SightingResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Obtener avistamientos por mascota", description = "Obtiene todos los avistamientos de una mascota específica. "
            +
            "Tests: SightingControllerTest.getSightingsByPetId_whenPetDoesNotExist_returnsNotFound(), " +
            "SightingControllerTest.getSightingsByPetId_whenPetExistsButNoSightings_returnsNoContent(), " +
            "SightingControllerTest.getSightingsByPetId_whenSightingsExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avistamientos de la mascota obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "La mascota no tiene avistamientos registrados"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Mascota no encontrada\", \"message\": \"No existe una mascota con el ID proporcionado\"}")))
    })
    @GetMapping("/pet/{petId}")
    public ResponseEntity<?> getSightingsByPetId(
            @Parameter(description = "ID de la mascota", required = true, example = "1") @PathVariable("petId") Long petId) {
        Map<String, String> response = new HashMap<>();

        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            response.put("message", "No existe una mascota con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Pet pet = petOptional.get();
        List<Sighting> sightings = pet.getSightings();

        if (sightings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<SightingResponseDTO> responseDTO = sightings.stream()
                .map(SightingResponseDTO::fromEntity)
                .collect(Collectors.toList());

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @Operation(summary = "Crear un avistamiento", description = "Registra un nuevo avistamiento de una mascota. Requiere token de autenticación. "
            +
            "Tests: SightingControllerTest.createSighting_whenTokenDoesNotEndWith123456_returnsUnauthorized(), " +
            "SightingControllerTest.createSighting_whenUserDoesNotExist_returnsUnauthorized(), " +
            "SightingControllerTest.createSighting_whenDataInvalid_returnsBadRequest(), " +
            "SightingControllerTest.createSighting_whenUserNotFound_returnsNotFound(), " +
            "SightingControllerTest.createSighting_whenPetNotFound_returnsNotFound(), " +
            "SightingControllerTest.createSighting_whenValidData_returnsCreated()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avistamiento creado exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Datos inválidos\", \"message\": \"Faltan campos obligatorios para crear el avistamiento\"}"))),
            @ApiResponse(responseCode = "401", description = "Token inválido o no proporcionado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Token inválido\", \"message\": \"El token proporcionado no es válido\"}"))),
            @ApiResponse(responseCode = "404", description = "Usuario reportador o mascota no encontrados", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<?> createSighting(
            @Parameter(description = "Token de autenticación (formato: {userId}123456)", required = true, example = "1123456") @RequestHeader("token") String token,
            @Parameter(description = "Datos del avistamiento a crear", required = true) @RequestBody SightingCreateDTO sightingDTO) {

        Map<String, String> response = new HashMap<>();
        if (!checkToken(token)) {
            response.put("error", "Token inválido");
            response.put("message", "El token proporcionado no es válido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!sightingDTO.isValid()) {
            response.put("error", "Datos inválidos");
            response.put("message", "Faltan campos obligatorios para crear el avistamiento");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Long userId = Long.valueOf(token.replace("123456", ""));
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            response.put("error", "Usuario no encontrado");
            response.put("message", "El usuario reportador no existe");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Optional<Pet> petOptional = petRepository.findById(sightingDTO.petId());
        if (petOptional.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            response.put("message", "No existe una mascota con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User reporter = userOptional.get();
        Pet pet = petOptional.get();

        Sighting sighting = Sighting.builder()
                .mascota(pet)
                .reportador(reporter)
                .latitud(sightingDTO.latitude())
                .longitud(sightingDTO.longitude())
                .fotoBase64(sightingDTO.photoBase64())
                .fecha(sightingDTO.date())
                .comentario(sightingDTO.comment() != null ? sightingDTO.comment() : "")
                .build();

        Sighting savedSighting = sightingRepository.save(sighting);
        SightingResponseDTO responseDTO = SightingResponseDTO.fromEntity(savedSighting);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener avistamiento por ID", description = "Obtiene los detalles de un avistamiento específico mediante su ID. "
            +
            "Tests: SightingControllerTest.getSightingById_whenSightingDoesNotExist_returnsNotFound(), " +
            "SightingControllerTest.getSightingById_whenSightingExists_returnsOk()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Avistamiento encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SightingResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Avistamiento no encontrado", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Avistamiento no encontrado\", \"message\": \"No existe un avistamiento con el ID proporcionado\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getSightingById(
            @Parameter(description = "ID del avistamiento", required = true, example = "1") @PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();

        Optional<Sighting> sightingOptional = sightingRepository.findById(id);
        if (sightingOptional.isEmpty()) {
            response.put("error", "Avistamiento no encontrado");
            response.put("message", "No existe un avistamiento con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        SightingResponseDTO responseDTO = SightingResponseDTO.fromEntity(sightingOptional.get());
        return ResponseEntity.ok(responseDTO);
    }

    private boolean checkToken(String token) {
        return token != null &&
                token.endsWith("123456") &&
                userRepository.existsById(Long.valueOf(token.replace("123456", "")));
    }
}
