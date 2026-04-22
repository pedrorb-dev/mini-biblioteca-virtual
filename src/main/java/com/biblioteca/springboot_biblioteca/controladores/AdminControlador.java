package com.biblioteca.springboot_biblioteca.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.biblioteca.springboot_biblioteca.entidades.Autor;
import com.biblioteca.springboot_biblioteca.entidades.Libro;
import com.biblioteca.springboot_biblioteca.repositorios.AutorRepositorio;
import com.biblioteca.springboot_biblioteca.repositorios.LibroRepositorio;
import com.biblioteca.springboot_biblioteca.servicios.AlmacenServicio;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
public class AdminControlador {
    @Autowired
    private AlmacenServicio servicio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private LibroRepositorio libroRepositorio;

    @GetMapping("")
    public ModelAndView verInicio(@PageableDefault(sort = "titulo", size = 5) Pageable pageable) {
        Page<Libro> libros = libroRepositorio.findAll(pageable);
        return new ModelAndView("admin/index").addObject("libros", libros);
    }

    @GetMapping("/libros/nuevo")
    public ModelAndView agregarLibro() {
        List<Autor> autores = autorRepositorio.findAll(Sort.by("nombre"));
        return new ModelAndView("admin/agregar_libro").addObject("autores", autores).addObject("libro", new Libro());
    }

    @PostMapping("/libros/nuevo")
    public ModelAndView registrarLibro(@Validated Libro libro, BindingResult bindingResult) {
        if (bindingResult.hasErrors() || libro.getPortada().isEmpty()) {
            if (libro.getPortada().isEmpty()) {
                bindingResult.rejectValue("portada", "MultipartNotEmpty");
            }

            List<Autor> autores = autorRepositorio.findAll(Sort.by("nombre"));
            return new ModelAndView("admin/agregar_libro").addObject("autores", autores).addObject("libro",
                    new Libro());

        }

        String rutaPortada = servicio.almacenarArchivo(libro.getPortada());
        libro.setRutaPortada(rutaPortada);

        libroRepositorio.save(libro);
        return new ModelAndView("redirect:/admin");
    }

    @GetMapping("/libros/{id}/editar")
    public ModelAndView editarLibro(@PathVariable Long id) {
        Libro libro = libroRepositorio.getOne(id);
        List<Autor> autores = autorRepositorio.findAll(Sort.by("nombre"));
        return new ModelAndView("admin/editar_libro").addObject("autores", autores).addObject("libro", libro);
    }

    @PostMapping("/libros/{id}/editar")
    public ModelAndView registrarLibroEditado(@PathVariable Long id, @Validated Libro libro,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Autor> autores = autorRepositorio.findAll(Sort.by("nombre"));
            return new ModelAndView("admin/editar_libro")
                    .addObject("libro", libro)
                    .addObject("autores", autores);
        }

        Libro libroBD = libroRepositorio.getOne(id);
        libroBD.setTitulo(libro.getTitulo());
        libroBD.setSinopsis(libro.getSinopsis());
        libroBD.setAutores(libro.getAutores());
        libroBD.setEditorial(libro.getEditorial());
        libroBD.setFecha(libro.getFecha());
        libroBD.setUrlLibro(libro.getUrlLibro());

        if (!libro.getPortada().isEmpty()) {
            servicio.eliminarArchivo(libroBD.getRutaPortada());
            String rutaPortada = servicio.almacenarArchivo(libro.getPortada());
            libroBD.setRutaPortada(rutaPortada);
        }

        libroRepositorio.save(libroBD);
        return new ModelAndView("redirect:/admin");
    }

    @PostMapping("/libros/{id}/eliminar")
    private String eliminarLibro(@PathVariable Long id) {
        Libro libro = libroRepositorio.getOne(id);
        libroRepositorio.delete(libro);
        servicio.eliminarArchivo(libro.getRutaPortada());
        return "redirect:/admin";
    }
}
