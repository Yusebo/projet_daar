package com.example.demo.controller;

import com.example.demo.library.BookRepository;
import com.example.demo.library.models.Book;
import com.example.demo.search.SearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/books")
public class BookController {
	
	@Autowired
    private final SearchEngine searchEngine;

   
    public BookController(BookRepository bookRepository) {
        this.searchEngine = new SearchEngine(bookRepository);
    }

    // Récupérer tous les livres
    @GetMapping("/")
    public List<Book> getAllBooks() {
        return searchEngine.getAllBooks();
    }

    // Recherche basique par mot-clé
    @GetMapping("/search")
    public List<Book> basicSearch(@RequestParam String keyword) {
        return searchEngine.basicSearch(keyword);
    }

    // Recherche avancée par expression régulière
    @GetMapping("/advanced-search")
    public List<Book> advancedSearch(@RequestParam String regex) {
        return searchEngine.advancedSearch(regex);
    }


    // Suggérer des livres similaires
    @GetMapping("/suggest-similar")
    public List<Book> suggestSimilarBooks(@RequestParam Long bookId) {
        Book book = searchEngine.getBookRepository().findById(bookId)
                .orElseThrow(() -> new RuntimeException("Livre non trouvé avec l'ID : " + bookId));
        return searchEngine.suggestSimilarBooks(book);
    }
}