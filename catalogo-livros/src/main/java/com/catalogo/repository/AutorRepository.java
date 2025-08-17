package com.catalogo.repository;

import com.catalogo.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByNome(String nome);

    // Derived queries para "vivos em um ano":
    // Caso com falecimento conhecido (>= ano)
    List<Autor> findByAnoNascimentoLessThanEqualAndAnoFalecimentoGreaterThanEqual(Integer ano1, Integer ano2);

    // Caso ainda vivos (falecimento null)
    List<Autor> findByAnoNascimentoLessThanEqualAndAnoFalecimentoIsNull(Integer ano);
}
