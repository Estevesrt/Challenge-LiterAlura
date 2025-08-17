package com.catalogo.service;

import com.catalogo.model.Autor;
import com.catalogo.model.Livro;
import com.catalogo.repository.AutorRepository;
import com.catalogo.repository.LivroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class GutendexService {

    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public GutendexService(AutorRepository autorRepository, LivroRepository livroRepository) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
    }

    /** Busca na Gutendex e persiste APENAS o primeiro resultado compatível (pt/en). */
    public Optional<Livro> buscarSalvarPrimeiroResultadoPorTitulo(String titulo) throws IOException {
        String query = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String urlStr = "https://gutendex.com/books/?search=" + query;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);

        try (Scanner sc = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8)) {
            StringBuilder json = new StringBuilder();
            while (sc.hasNextLine()) json.append(sc.nextLine());
            JsonNode root = mapper.readTree(json.toString());
            JsonNode results = root.get("results");
            if (results == null || !results.isArray() || results.size() == 0) {
                return Optional.empty();
            }

            // Primeiro resultado
            JsonNode first = results.get(0);
            if (first == null) return Optional.empty();

            String tituloLivro = first.path("title").asText(null);
            JsonNode langs = first.path("languages");
            String idioma = (langs.isArray() && langs.size() > 0) ? langs.get(0).asText(null) : null;
            Integer downloads = first.hasNonNull("download_count") ? first.get("download_count").asInt() : 0;

            // Restringe a pt/en
            if (idioma == null || !(idioma.equals("pt") || idioma.equals("en"))) {
                return Optional.empty();
            }

            // Autor (primeiro)
            JsonNode autorJson = first.path("authors");
            String nomeAutor = null;
            Integer nascimento;
            Integer falecimento;
            if (autorJson.isArray() && autorJson.size() > 0) {
                JsonNode a = autorJson.get(0);
                nomeAutor = a.path("name").asText(null);
                if (a.hasNonNull("birth_year")) nascimento = a.get("birth_year").asInt();
                else {
                    nascimento = null;
                }
                if (a.hasNonNull("death_year")) falecimento = a.get("death_year").asInt();
                else {
                    falecimento = null;
                }
            } else {
                falecimento = null;
                nascimento = null;
                nomeAutor = "Autor Desconhecido";
            }

            // Reaproveita autor se já existir
            String finalNomeAutor = nomeAutor;
            Autor autor = autorRepository.findByNome(nomeAutor)
                    .orElseGet(() -> autorRepository.save(new Autor(finalNomeAutor, nascimento, falecimento)));

            // Evita duplicar mesmo título (case-insensitive)
            Optional<Livro> existente = livroRepository.findFirstByTituloIgnoreCase(tituloLivro);
            if (existente.isPresent()) {
                return existente;
            }

            Livro livro = new Livro(tituloLivro, idioma, downloads, autor);
            return Optional.of(livroRepository.save(livro));
        }
    }

    public List<Livro> listarPorIdioma(String idioma) {
        return livroRepository.findByIdioma(idioma);
    }
}
