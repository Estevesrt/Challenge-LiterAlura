package com.catalogo.repository;

import com.catalogo.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByIdioma(String idioma);
    Optional<Livro> findFirstByTituloIgnoreCase(String titulo);
}
