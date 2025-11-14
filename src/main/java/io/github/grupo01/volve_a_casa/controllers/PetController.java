package io.github.grupo01.volve_a_casa.controllers;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import java.util.List;

@RestController
@RequestMapping(value = "/pets", produces = MediaType.APPLICATION_JSON_VALUE, name = "PetRestController")
@Tag(name = "Mascotas", description = "API para gestión de mascotas")
public class PetController {
    private final PetRepository petRepository;

    @Autowired
    public PetController(PetRepository petRepository) {
        this.petRepository = petRepository;
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
