package com.example.demo.library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.library.models.Book;
import com.example.demo.search.SearchEngine;



@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private SearchEngine searchEngine;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
   
    public void resetBookIdCounter() {
        // Vérifiez si la table existe
        String tableName = "books";
        String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
        Integer tableCount = jdbcTemplate.queryForObject(checkTableQuery, Integer.class, tableName);

        if (tableCount != null && tableCount > 0) {
            // Réinitialiser l'auto-incrément si la table existe
            jdbcTemplate.execute("ALTER TABLE books AUTO_INCREMENT = 1;");
            System.out.println("Auto-increment reset for table 'book'.");
        } else {
            System.out.println("Table 'book' does not exist. Skipping auto-increment reset.");
        }
    }
    
    @Override
    public void run(String... args) throws IOException {
    	long countBeforeDelete = bookRepository.count();
        System.out.println("Nombre d'éléments avant suppression : " + countBeforeDelete);

        // Supprimer tous les éléments
        bookRepository.deleteAll();
        resetBookIdCounter();

        // Vérifier le nombre d'éléments après la suppression
        long countAfterDelete = bookRepository.count();
        System.out.println("Nombre d'éléments après suppression : " + countAfterDelete);
       
        File folder = new File("./f");


        if (!folder.exists() || !folder.isDirectory()) {
        
            return;
        }

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    String content = Files.readString(file.toPath());
                    String title = extractTitle(content);
                    String author = extractAuthor(content);

                    Book book = new Book(title, author, content);
                    bookRepository.save(book);
                    
                } catch (IOException e) {
          
                }
            }
        }
        
        List<Book> books = bookRepository.findAll();
        int i = 0;
        for (Book book : books) {
            searchEngine.indexBook(book);
            System.out.println(i);
            i++;
        }
        System.out.println("Finish");
    }

    private String extractTitle(String content) {
        return Stream.of(content.split("\n"))
                     .filter(line -> line.toLowerCase().startsWith("title:"))
                     .map(line -> line.replaceFirst("(?i)title:", "").trim())
                     .findFirst()
                     .orElse("Unknown Title");
    }

    private String extractAuthor(String content) {
        return Stream.of(content.split("\n"))
                     .filter(line -> line.toLowerCase().startsWith("author:"))
                     .map(line -> line.replaceFirst("(?i)author:", "").trim())
                     .findFirst()
                     .orElse("Unknown Author");
    }
}
