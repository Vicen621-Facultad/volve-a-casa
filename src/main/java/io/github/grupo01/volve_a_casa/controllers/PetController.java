package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.controllers.dto.PetCreateDTO;
import io.github.grupo01.volve_a_casa.controllers.dto.PetUpdateDTO;
import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping(value = "/pets", produces = MediaType.APPLICATION_JSON_VALUE, name = "PetRestController")
@Tag(name = "Mascotas", description = "API para gestión de mascotas")
public class PetController {
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Autowired
    public PetController(PetRepository petRepository, UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Crear mascota", description = "Permite crear una nueva mascota asociada al usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mascota creada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createPet(@RequestHeader("token") String token, @RequestBody PetCreateDTO dto) {
        Map<String, Object> response = new HashMap<>();

        if (!dto.isValid()) {
            response.put("error", "Datos invalidos");
            response.put("message", "Faltan campos obligatorios.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User creator = optionalUser.get();

        try {
            Pet newPet = Pet.builder()
                    .nombre(dto.name())
                    .tamano(dto.size())
                    .descripcion(dto.description())
                    .color(dto.color())
                    .raza(dto.race())
                    .peso(dto.weight())
                    .latitud(dto.latitude())
                    .longitud(dto.longitude())
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(dto.type())
                    .creador(creator)
                    .agregarFoto("foto_default_base64")
                    .build();
            petRepository.save(newPet);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPet);

        } catch (Exception e) {
            response.put("error", "Error al crear mascota");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(summary = "Actualizar mascota", description = "Actualiza los datos de una mascota creada por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota actualizada exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(
            @Parameter(description = "Token de autenticación", required = true) @RequestHeader("token") String token,
            @Parameter(description = "ID de la mascota", required = true) @PathVariable Long id,
            @RequestBody PetUpdateDTO dto) {
        Map<String, String> response = new HashMap<>();
        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Pet pet = optionalPet.get();
        if (!pet.getCreator().equals(optionalUser.get())) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        pet.updateFromDTO(dto);

        petRepository.save(pet);
        return ResponseEntity.ok(pet);
    }

    @Operation(summary = "Listar mis mascotas", description = "Obtiene todas las mascotas creadas por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @GetMapping("/mine")
    public ResponseEntity<?> getMyPets(
            @Parameter(description = "Token de autenticación", required = true) @RequestHeader("token") String token) {
        Map<String, String> response = new HashMap<>();
        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        User user = optionalUser.get();
        return ResponseEntity.ok(user.getCreatedPets());
    }

    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota creada por el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota eliminada correctamente"),
            @ApiResponse(responseCode = "401", description = "Token inválido"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(
            @Parameter(description = "Token de autenticación", required = true) @RequestHeader("token") String token,
            @Parameter(description = "ID de la mascota", required = true) @PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        Optional<User> optionalUser = getUserFromToken(token);
        if (optionalUser.isEmpty()) {
            response.put("error", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<Pet> optionalPet = petRepository.findById(id);
        if (optionalPet.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Pet pet = optionalPet.get();
        if (!pet.getCreator().equals(optionalUser.get())) {
            response.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        petRepository.delete(pet);
        response.put("message", "Mascota eliminada correctamente");
        return ResponseEntity.ok(response);
    }

    private Optional<User> getUserFromToken(String token) {
        if (token == null || !token.endsWith("123456"))
            return Optional.empty();
        try {
            Long id = Long.valueOf(token.replace("123456", ""));
            return userRepository.findById(id);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Operation(summary = "Listar mascotas perdidas", description = "Obtiene todas las mascotas marcadas como perdidas (estados: PERDIDO_PROPIO o PERDIDO_AJENO). "
            +
            "Tests: PetControllerTest.listAllLostPets_whenEmpty_returnsNoContent(), " +
            "PetControllerTest.listAllLostPets_whenPetsExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas perdidas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "204", description = "No hay mascotas perdidas registradas")
    })
    @GetMapping("/lost")
    public ResponseEntity<List<Pet>> listAllLostPets() {
        List<Pet> lostPets = petRepository.findAllLostPets();

        if (lostPets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(lostPets, HttpStatus.OK);
    }

    @Operation(summary = "Obtener mascota por ID", description = "Obtiene los detalles de una mascota específica mediante su ID. "
            +
            "Tests: PetControllerTest.getPetById_whenPetExists_returnsOk(), " +
            "PetControllerTest.getPetById_whenPetDoesNotExist_returnsNotFound()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mascota encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(
            @Parameter(description = "ID de la mascota", required = true, example = "1") @PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();

        Optional<Pet> petOptional = petRepository.findById(id);
        if (petOptional.isEmpty()) {
            response.put("error", "Mascota no encontrada");
            response.put("message", "No se encontró una mascota con el ID proporcionado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(petOptional.get());
    }

    @Operation(summary = "Listar todas las mascotas", description = "Obtiene todas las mascotas registradas en el sistema, sin importar su estado. "
            +
            "Tests: PetControllerTest.listAllPets_whenEmpty_returnsNoContent(), " +
            "PetControllerTest.listAllPets_whenPetsExist_returnsOkAndList()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mascotas obtenida exitosamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Pet.class))),
            @ApiResponse(responseCode = "204", description = "No hay mascotas registradas")
    })
    @GetMapping
    public ResponseEntity<List<Pet>> listAllPets() {
        List<Pet> pets = petRepository.findAll();
        if (pets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }
}
