
package com.controlgastos.controller;

import com.controlgastos.model.Gasto;
import com.controlgastos.repository.GastoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private final GastoRepository gastoRepository;

    public GastoController(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    // Listar todos los gastos
    @GetMapping
    public List<Gasto> listarGastos() {
        return gastoRepository.findAll();
    }

    // Buscar un gasto por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Gasto> obtenerGasto(@PathVariable Long id) {
        return gastoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Mensaje de bienvenida
    @GetMapping("/bienvenida")
    public String bienvenida() {
        return "Bienvenido a GeoGasto - Sistema de Control de Gastos";
    }

    // Calcular el total de gastos
    @GetMapping("/total")
    public String total() {
        double total = gastoRepository.findAll()
                .stream()
                .mapToDouble(g -> g.getMonto() != null ? g.getMonto() : 0.0)
                .sum();

        return "Total gastado: S/ " + String.format("%.2f", total);
    }

    // Filtrar por categoría
    @GetMapping("/categoria/{categoria}")
    public List<Gasto> porCategoria(@PathVariable String categoria) {
        return gastoRepository.findByCategoriaIgnoreCase(categoria);
    }

    // Registrar un gasto
    @PostMapping
    public ResponseEntity<?> registrarGasto(@RequestBody Gasto gasto) {

        if (gasto.getDescripcion() == null
                || gasto.getDescripcion().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La descripción es obligatoria"));
        }

        if (gasto.getMonto() == null || gasto.getMonto() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "El monto debe ser mayor que cero"));
        }

        if (gasto.getCategoria() == null
                || gasto.getCategoria().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La categoría es obligatoria"));
        }

        gasto.setId(null);

        Gasto guardado = gastoRepository.save(gasto);

        return ResponseEntity.status(201).body(guardado);
    }

    // Actualizar un gasto
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarGasto(
            @PathVariable Long id,
            @RequestBody Gasto datos) {

        var encontrado = gastoRepository.findById(id);

        if (encontrado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (datos.getDescripcion() == null
                || datos.getDescripcion().isBlank()
                || datos.getMonto() == null
                || datos.getMonto() <= 0
                || datos.getCategoria() == null
                || datos.getCategoria().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Revisa la descripción, el monto y la categoría"));
        }

        Gasto gasto = encontrado.get();

        gasto.setDescripcion(datos.getDescripcion());
        gasto.setMonto(datos.getMonto());
        gasto.setCategoria(datos.getCategoria());
        gasto.setLatitud(datos.getLatitud());
        gasto.setLongitud(datos.getLongitud());

        if (datos.getFecha() != null) {
            gasto.setFecha(datos.getFecha());
        }

        Gasto actualizado = gastoRepository.save(gasto);

        return ResponseEntity.ok(actualizado);
    }

    // Eliminar un gasto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable Long id) {

        if (!gastoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        gastoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
