package com.biblioteca.springboot_biblioteca.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import com.biblioteca.springboot_biblioteca.entidades.Autor;

public interface AutorRepositorio extends JpaRepository<Autor, Long> {

}
