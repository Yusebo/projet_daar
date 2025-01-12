package com.example.demo.library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.library.models.Book;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

@Component
public class BookLoader implements CommandLineRunner {

    private final BookRepository bookRepository;

    public BookLoader(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws IOException {
    	
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
