package com.biblioteca.springboot_biblioteca.servicios;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AlmacenServicio {
    public void iniciarAlmacenArchivo();

    public String almacenarArchivo(MultipartFile archivo);

    public Path cargarArchivo(String nombre);

    public Resource cargarRecurso(String nombre);

    public void eliminarArchivo(String nombre);

    public byte[] descargarArchivo(String url) throws Exception;

    public String nombreArchivo(String url);
}
