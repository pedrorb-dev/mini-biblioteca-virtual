package com.biblioteca.springboot_biblioteca.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.springboot_biblioteca.entidades.Libro;

public interface LibroRepositorio extends JpaRepository<Libro, Long> {

}
