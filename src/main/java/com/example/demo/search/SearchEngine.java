package com.example.demo.search;




import RegEx.DFA;
import RegEx.NFA;
import RegEx.NFABuilder;
import RegEx.RegEx;
import RegEx.DFABuilder;
import RegEx.DFAMinimizer;
import RegEx.RegExTree;
import RegEx.StateA;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.library.BookRepository;
import com.example.demo.library.models.Book;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchEngine {

    private final BookRepository bookRepository;

    @Autowired
    public SearchEngine(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Fonction de recherche basique par mot-clé
    public List<Book> basicSearch(String keyword) {
        return bookRepository.findByContentContaining(keyword);
    }

    // Fonction de recherche avancée par expression régulière
    public List<Book> advancedSearch(String regex) {
        // Construire le NFA à partir de l'expression régulière
        
        DFA minimizedDFA = RegEx.parseregex(regex);

        // Récupérer tous les livres de la base de données
        List<Book> allBooks = bookRepository.findAll();

        // Filtrer les livres qui correspondent à l'expression régulière
        return allBooks.stream()
                .filter(book -> matchesRegex(minimizedDFA, book.getContent()))
                .collect(Collectors.toList());
    }

    // Fonction pour classer les résultats de recherche
    public List<Book> rankResults(List<Book> results, String keyword) {
        // Classer les résultats par nombre d'occurrences du mot-clé
        results.sort((b1, b2) -> {
            int count1 = countOccurrences(b1.getContent(), keyword);
            int count2 = countOccurrences(b2.getContent(), keyword);
            return Integer.compare(count2, count1); // Ordre décroissant
        });
        return results;
    }

    // Fonction pour suggérer des livres similaires
    public List<Book> suggestSimilarBooks(Book book) {
        // Implémenter une logique de suggestion basée sur le graphe de Jaccard ou d'autres critères
        // Par exemple, retourner les livres avec des titres ou auteurs similaires
        return bookRepository.findByTitleContainingOrAuthorContaining(book.getTitle(), book.getAuthor())
                .stream()
                .filter(b -> !b.equals(book))
                .collect(Collectors.toList());
    }

    // Méthode utilitaire pour compter les occurrences d'un mot dans un texte
    private int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    // Méthode utilitaire pour vérifier si un texte correspond à une expression régulière
    private boolean matchesRegex(DFA dfa, String text) {
        return RegEx.search(dfa, dfa.getInitialStateA(),text, 0);
    }


}