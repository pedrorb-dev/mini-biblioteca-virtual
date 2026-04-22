package com.biblioteca.springboot_biblioteca.controladores;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.springboot_biblioteca.entidades.Libro;
import com.biblioteca.springboot_biblioteca.repositorios.LibroRepositorio;
import com.biblioteca.springboot_biblioteca.servicios.AlmacenServicio;

@Controller
public class HomeControlador {
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AlmacenServicio servicio;

    @GetMapping("/libros")
    public ModelAndView listarLibros(
            @PageableDefault(sort = "fecha", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Libro> libros = libroRepositorio.findAll(pageable);
        return new ModelAndView("libros").addObject("libros", libros);
    }

    @GetMapping("/libros/{id}")
    public ModelAndView mostrarDetalles(@PathVariable Long id) {
        Libro libro = libroRepositorio.getOne(id);
        return new ModelAndView("libro").addObject("libro", libro);
    }

    @PostMapping("/descargar")
    public String procesoDescarga(@RequestParam String url,
            RedirectAttributes redirectAttributes) {
        try {
            if (url == null || url.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor, ingrese una URL válida");
                return "redirect:/";
            }
            redirectAttributes.addFlashAttribute("url", url);
            return "redirect:/descargar-archivo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "error al procesar la descarga");
            return "redirect:/";
        }
    }

    @GetMapping("/descargar-archivo")
    public ResponseEntity<StreamingResponseBody> descargarArchivo(@ModelAttribute("url") String url) {
        try {
            String nombre = servicio.nombreArchivo(url);

            StreamingResponseBody stream = otpStream -> {
                HttpURLConnection conexion = (HttpURLConnection) new URL(url).openConnection();
                conexion.setConnectTimeout(5000);
                conexion.setReadTimeout(30000);
                conexion.setRequestProperty("User-Agent", "Mozilla/5.0");

                try (InputStream is = conexion.getInputStream()) {
                    byte[] buffer = new byte[16000]; // 16kb
                    int bytes;
                    while ((bytes = is.read(buffer)) != -1) {
                        otpStream.write(buffer, 0, bytes);
                        otpStream.flush();
                    }
                }
            };

            HttpHeaders cabecera = new HttpHeaders();
            cabecera.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            cabecera.setContentDispositionFormData("atachment", nombre);

            return ResponseEntity.ok().headers(cabecera).body(stream);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
