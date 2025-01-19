package com.example.demo.search;

import RegEx.DFA;
import RegEx.RegEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.library.BookRepository;
import com.example.demo.library.models.Book;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchEngine {

    private final BookRepository bookRepository;

    @Autowired
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
        List<Book> books = bookRepository.findByContentContaining(keyword);
        return rankResults(books, keyword); // Classement des résultats
    }

    // Recherche avancée par expression régulière dans le contenu des livres
    public List<Book> advancedSearch(String regex) {
        DFA minimizedDFA = RegEx.parseregex(regex);
        List<Book> allBooks = bookRepository.findAll();

        // Filtrer les livres dont le contenu correspond à l'expression régulière
        List<Book> matchedBooks = allBooks.stream()
                .filter(book -> matchesRegex(minimizedDFA, book.getContent()))
                .collect(Collectors.toList());

        // Classement des résultats en fonction du nombre de correspondances de l'expression régulière
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
        return RegEx.search(dfa, dfa.getInitialStateA(), text, 0);
    }
}