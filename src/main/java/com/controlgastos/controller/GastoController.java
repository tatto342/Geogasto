package com.controlgastos.controller;

import com.controlgastos.model.Gasto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
public class GastoController {

    @GetMapping
    public List<Gasto> listarGastos() {
        List<Gasto> gastos = new ArrayList<>();
        gastos.add(new Gasto(1L, "Almuerzo ejecutivo", 25.50, "Alimentación", "Av. Grau - Piura"));
        gastos.add(new Gasto(2L, "Pasaje en moto/taxi", 15.00, "Transporte", "Centro Comercial Plaza de Armas"));
        return gastos;
    }
}