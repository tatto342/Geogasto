
package com.controlgastos.repository;

import com.controlgastos.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByCategoriaIgnoreCase(String categoria);
}
