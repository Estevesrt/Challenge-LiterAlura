package com.catalogo;

import com.catalogo.model.Autor;
import com.catalogo.model.Livro;
import com.catalogo.repository.AutorRepository;
import com.catalogo.repository.LivroRepository;
import com.catalogo.service.GutendexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Menu implements CommandLineRunner {

    private final GutendexService gutendexService;
    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public Menu(GutendexService gutendexService, LivroRepository livroRepository, AutorRepository autorRepository) {
        this.gutendexService = gutendexService;
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;
        do {
            System.out.println("\n=== Catálogo de Livros ===");
            System.out.println("1) Buscar livros pelo título");
            System.out.println("2) Listar livros registrados");
            System.out.println("3) Listar autores registrados");
            System.out.println("4) Listar autores vivos em um determinado ano");
            System.out.println("5) Listar livros em um determinado idioma");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");

            while (!scanner.hasNextInt()) {
                System.out.print("Digite um número válido: ");
                scanner.next();
            }
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> buscarPorTitulo(scanner);
                case 2 -> listarLivros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivos(scanner);
                case 5 -> listarPorIdioma(scanner);
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void buscarPorTitulo(Scanner scanner) {
        System.out.print("Digite o título: ");
        String titulo = scanner.nextLine().trim();
        if (titulo.isEmpty()) {
            System.out.println("Título não pode ser vazio.");
            return;
        }
        try {
            var salvo = gutendexService.buscarSalvarPrimeiroResultadoPorTitulo(titulo);
            if (salvo.isPresent()) {
                System.out.println("Livro salvo/encontrado: " + salvo.get());
            } else {
                System.out.println("Nenhum livro compatível encontrado (apenas pt/en são aceitos).");
            }
        } catch (IOException e) {
            System.out.println("Erro ao consultar Gutendex: " + e.getMessage());
        }
    }

    private void listarLivros() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado.");
        } else {
            livros.forEach(System.out::println);
        }
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor registrado.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivos(Scanner scanner) {
        System.out.print("Ano: ");
        while (!scanner.hasNextInt()) {
            System.out.print("Digite um ano válido: ");
            scanner.next();
        }
        int ano = scanner.nextInt();
        scanner.nextLine();

        // Usa SOMENTE derived queries do repositório:
        var comFalecimento = autorRepository
                .findByAnoNascimentoLessThanEqualAndAnoFalecimentoGreaterThanEqual(ano, ano);
        var semFalecimento = autorRepository
                .findByAnoNascimentoLessThanEqualAndAnoFalecimentoIsNull(ano);

        // Combina e remove duplicados
        List<Autor> vivos = new ArrayList<>();
        vivos.addAll(comFalecimento);
        vivos.addAll(semFalecimento);
        vivos = vivos.stream().distinct().collect(Collectors.toList());

        if (vivos.isEmpty()) {
            System.out.println("Nenhum autor correspondente.");
        } else {
            vivos.forEach(System.out::println);
        }
    }

    private void listarPorIdioma(Scanner scanner) {
        System.out.print("Idioma (pt/en): ");
        String idioma = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
        if (!idioma.equals("pt") && !idioma.equals("en")) {
            System.out.println("Idioma inválido. Use 'pt' ou 'en'.");
            return;
        }
        var livros = gutendexService.listarPorIdioma(idioma);
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado nesse idioma.");
        } else {
            livros.forEach(System.out::println);
        }
    }
}
