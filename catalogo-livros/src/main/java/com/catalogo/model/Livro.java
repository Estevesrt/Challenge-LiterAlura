package com.catalogo.model;

import jakarta.persistence.*;

@Entity
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    // Apenas 'pt' ou 'en'
    @Column(length = 5)
    private String idioma;

    private Integer numeroDownloads;

    @ManyToOne(fetch = FetchType.EAGER) // garante que o autor sempre venha
    @JoinColumn(name = "autor_id")
    private Autor autor;

    public Livro() {}

    public Livro(String titulo, String idioma, Integer numeroDownloads, Autor autor) {
        this.titulo = titulo;
        this.idioma = idioma;
        this.numeroDownloads = numeroDownloads;
    }
    // Carrega o autor junto com o livro

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public Integer getNumeroDownloads() { return numeroDownloads; }
    public void setNumeroDownloads(Integer numeroDownloads) { this.numeroDownloads = numeroDownloads; }
    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) {
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Downloads: %d | Autor: %s",
                titulo, idioma, numeroDownloads, autor.getNome());
    }

}

