package com.carlaospa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carlaospa.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
