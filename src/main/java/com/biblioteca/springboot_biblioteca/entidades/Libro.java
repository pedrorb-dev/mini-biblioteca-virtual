package com.biblioteca.springboot_biblioteca.entidades;

import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_libro;

    @Column(length = 45)
    private String titulo;

    @Column(length = 45)
    private String editorial;

    @NotNull
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinTable(name = "libros_autores", joinColumns = @JoinColumn(name = "id_libro"), inverseJoinColumns = @JoinColumn(name = "id_autor"), uniqueConstraints = @UniqueConstraint(columnNames = {
            "id_libro", "id_autor" }))
    private List<Autor> autores;

    @NotNull
    @DateTimeFormat(iso = ISO.DATE)
    private LocalDate fecha;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @NotBlank
    private String urlLibro;
    private String rutaPortada;

    @Transient
    private MultipartFile portada;

    public Libro() {
        super();
        autores = new ArrayList<>();
    }

    public Libro(Long id_libro, String titulo, String editorial, @NotEmpty List<Autor> autores,
            @NotNull LocalDate fecha, @NotBlank String sinopsis, @NotBlank String urlLibro, String rutaPortada,
            MultipartFile portada) {
        this.id_libro = id_libro;
        this.titulo = titulo;
        this.editorial = editorial;
        this.autores = autores;
        this.fecha = fecha;
        this.sinopsis = sinopsis;
        this.urlLibro = urlLibro;
        this.rutaPortada = rutaPortada;
        this.portada = portada;
    }

    public Libro(String titulo, String editorial, @NotEmpty List<Autor> autores, @NotNull LocalDate fecha,
            @NotBlank String sinopsis, @NotBlank String urlLibro, String rutaPortada, MultipartFile portada) {
        this.titulo = titulo;
        this.editorial = editorial;
        this.autores = autores;
        this.fecha = fecha;
        this.sinopsis = sinopsis;
        this.urlLibro = urlLibro;
        this.rutaPortada = rutaPortada;
        this.portada = portada;
    }

    public Libro(Long id_libro) {
        super();
        this.id_libro = id_libro;
    }

    public Long getId_libro() {
        return id_libro;
    }

    public void setId_libro(Long id_libro) {
        this.id_libro = id_libro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getUrlLibro() {
        return urlLibro;
    }

    public void setUrlLibro(String urlLibro) {
        this.urlLibro = urlLibro;
    }

    public String getRutaPortada() {
        return rutaPortada;
    }

    public void setRutaPortada(String rutaPortada) {
        this.rutaPortada = rutaPortada;
    }

    public MultipartFile getPortada() {
        return portada;
    }

    public void setPortada(MultipartFile portada) {
        this.portada = portada;
    }
}
