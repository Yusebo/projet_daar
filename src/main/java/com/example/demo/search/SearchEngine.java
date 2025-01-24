package com.example.demo.search;

import RegEx.DFA;
import RegEx.RegEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.library.BookRepository;
import com.example.demo.library.models.Book;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchEngine {
	@Autowired
    private final BookRepository bookRepository;
	
    private final Map<String, Set<Long>> invertedIndex = new HashMap<>();
    
    public SearchEngine(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public BookRepository getBookRepository() {
    	return this.bookRepository;
    }

    // Recherche basique par mot-clé dans le contenu des livres
    public List<Book> basicSearch(String keyword) {
        String normalizedKeyword = keyword.toLowerCase();
        Set<Long> bookIds = invertedIndex.getOrDefault(normalizedKeyword, Collections.emptySet());
        return rankResults(bookRepository.findAllById(bookIds),keyword);
    }

    // Recherche avancée par expression régulière dans le contenu des livres
    public List<Book> advancedSearch(String regex) {
        DFA minimizedDFA = RegEx.parseregex(regex);
        Set<Long> candidateBookIds = new HashSet<>();

        // Utiliser l'index pour filtrer les livres contenant des mots-clés pertinents
        for (String keyword : invertedIndex.keySet()) {
            if (matchesRegex(minimizedDFA, keyword)) {
                candidateBookIds.addAll(invertedIndex.get(keyword));
            }
        }

        // Filtrer les livres candidats en fonction du contenu complet
        List<Book> matchedBooks = bookRepository.findAllById(candidateBookIds).stream()
                .filter(book -> matchesRegex(minimizedDFA, book.getContent()))
                .collect(Collectors.toList());

        return rankResultsByRegex(matchedBooks, minimizedDFA);
    }
    
    public List<Book> suggestSimilarBooks(Book book) {
        return bookRepository.findByTitleContainingOrAuthorContaining(book.getTitle(), book.getAuthor())
                .stream()
                .filter(b -> !b.equals(book))
                .collect(Collectors.toList());
    }
    

    // Classement des résultats par nombre d'occurrences du mot-clé dans le contenu
    private List<Book> rankResults(List<Book> results, String keyword) {
        results.sort((b1, b2) -> {
            int count1 = countOccurrences(b1.getContent(), keyword);
            int count2 = countOccurrences(b2.getContent(), keyword);
            return Integer.compare(count2, count1); // Ordre décroissant
        });
        return results;
    }

    // Classement des résultats par nombre de correspondances de l'expression régulière dans le contenu
    private List<Book> rankResultsByRegex(List<Book> results, DFA dfa) {
        results.sort((b1, b2) -> {
            int count1 = countRegexMatches(dfa, b1.getContent());
            int count2 = countRegexMatches(dfa, b2.getContent());
            return Integer.compare(count2, count1); // Ordre décroissant
        });
        return results;
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

    // Méthode utilitaire pour compter les correspondances d'une expression régulière dans un texte
    private int countRegexMatches(DFA dfa, String text) {
        int count = 0;
        int position = 0;
        while (position < text.length()) {
            if (RegEx.searchsimple(dfa, dfa.getInitialStateA(), text, position)) {
                count++;
                position++; // Avancer d'un caractère après une correspondance
            } else {
                position++;
            }
        }
        return count;
    }

    // Méthode utilitaire pour vérifier si un texte correspond à une expression régulière
    private boolean matchesRegex(DFA dfa, String text) {
        int position = 0;
        while (position < text.length()) {
            if (RegEx.searchsimple(dfa, dfa.getInitialStateA(), text, position)) {
                return true;
            } else {
                position++;
            }
        }
        return false;

    }
    
    public void indexBook(Book book) {
        String[] words = book.getContent().split("\\s+"); // Divise le contenu en mots
        for (String word : words) {
            String normalizedWord = word.toLowerCase();
            invertedIndex.computeIfAbsent(normalizedWord, k -> new HashSet<>()).add(book.getId());
        }
    }
}