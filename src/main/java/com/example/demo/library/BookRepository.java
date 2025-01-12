package com.example.demo.library;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.library.models.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Rechercher des livres dont le contenu contient un mot-clé
    List<Book> findByContentContaining(String keyword);

    // Rechercher des livres dont le titre ou l'auteur contient un mot-clé
    List<Book> findByTitleContainingOrAuthorContaining(String title, String author);
}
