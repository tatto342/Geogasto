package com.controlgastos.controller;

import com.controlgastos.model.Gasto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    private final List<Gasto> gastos = new ArrayList<>();

    public GastoController() {
        // Datos de ejemplo en memoria
        gastos.add(new Gasto(1L, "Almuerzo en el centro", 25.50,
                -12.0464, -77.0428, LocalDateTime.now(), "Alimentación"));
        gastos.add(new Gasto(2L, "Taxi a la oficina", 15.00,
                -12.0892, -77.0334, LocalDateTime.now(), "Transporte"));
        gastos.add(new Gasto(3L, "Cena con amigos", 45.00,
                -12.1284, -77.0303, LocalDateTime.now(), "Ocio"));
        gastos.add(new Gasto(4L, "Farmacia", 32.50,
                -12.0567, -77.0389, LocalDateTime.now(), "Salud"));
        gastos.add(new Gasto(5L, "Curso de inglés", 120.00,
                -12.0709, -77.0411, LocalDateTime.now(), "Educación"));
    }

    @GetMapping
    public List<Gasto> listarGastos() {
        return gastos;
    }


    @GetMapping("/{id}")
    public Gasto obtenerGasto(@PathVariable Long id) {
        for (Gasto g : gastos) {
            if (g.getId().equals(id)) {
                return g;
            }
        }
        return null;
    }

    @GetMapping("/bienvenida")
    public String bienvenida() {
        return "Bienvenido a GeoGasto - Sistema de Control de Gastos";
    }


    @GetMapping("/total")
    public String total() {
        double total = gastos.stream()
                .mapToDouble(Gasto::getMonto)
                .sum();
        return "Total gastado: S/ " + String.format("%.2f", total);
    }

    @GetMapping("/categoria/{categoria}")
    public List<Gasto> porCategoria(@PathVariable String categoria) {
        List<Gasto> resultado = new ArrayList<>();
        for (Gasto g : gastos) {
            if (g.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(g);
            }
        }
        return resultado;
    }
}