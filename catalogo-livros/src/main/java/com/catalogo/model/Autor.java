package com.catalogo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private Integer anoNascimento;
    private Integer anoFalecimento; // pode ser null (ainda vivo)

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Livro> livros;

    public Autor() {}

    public Autor(String nome, Integer anoNascimento, Integer anoFalecimento) {
        this.nome = nome;
        this.anoNascimento = anoNascimento;
        this.anoFalecimento = anoFalecimento;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getAnoNascimento() { return anoNascimento; }
    public void setAnoNascimento(Integer anoNascimento) { this.anoNascimento = anoNascimento; }
    public Integer getAnoFalecimento() { return anoFalecimento; }
    public void setAnoFalecimento(Integer anoFalecimento) { this.anoFalecimento = anoFalecimento; }
    public List<Livro> getLivros() { return livros; }
    public void setLivros(List<Livro> livros) { this.livros = livros; }

    @Override
    public String toString() {
        String nasc = anoNascimento != null ? String.valueOf(anoNascimento) : "?";
        String fal = anoFalecimento != null ? String.valueOf(anoFalecimento) : "?";
        return nome + " (" + nasc + " - " + fal + ")";
    }
}
