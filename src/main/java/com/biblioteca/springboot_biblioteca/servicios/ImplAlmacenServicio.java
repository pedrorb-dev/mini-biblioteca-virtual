package com.biblioteca.springboot_biblioteca.servicios;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.biblioteca.springboot_biblioteca.excepciones.AlmacenExcepcion;
import com.biblioteca.springboot_biblioteca.excepciones.FileNotFoundExcepcion;

import jakarta.annotation.PostConstruct;

@Service
public class ImplAlmacenServicio implements AlmacenServicio {
    @Value("${storage.location}")
    private String storage;

    @PostConstruct
    @Override
    public void iniciarAlmacenArchivo() {
        try {
            Files.createDirectories(Paths.get(storage));
        } catch (IOException e) {
            throw new AlmacenExcepcion("No se puede iniciar el almacen");
        }
    }

    @Override
    public String almacenarArchivo(MultipartFile archivo) {
        String nombre = archivo.getOriginalFilename();
        if (nombre.isEmpty()) {
            throw new AlmacenExcepcion("El archivo no puede ser vacío");
        }

        try {
            InputStream inputStream = archivo.getInputStream();
            Files.copy(inputStream, Paths.get(storage).resolve(nombre), StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            throw new AlmacenExcepcion("Error al almacenar el archivo " + nombre, e);
        }
        return nombre;
    }

    @Override
    public Path cargarArchivo(String nombre) {
        return Paths.get(storage).resolve(nombre);
    }

    @Override
    public Resource cargarRecurso(String nombre) {
        try {
            Path archivo = cargarArchivo(nombre);
            Resource recurso = new UrlResource(archivo.toUri());

            if (recurso.exists() || recurso.isReadable()) {
                return recurso;
            } else {
                throw new FileNotFoundExcepcion("No se pudo encontrar el archivo " + nombre);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundExcepcion("No se pudo encontrar el archivo " + nombre);
        }
    }

    @Override
    public void eliminarArchivo(String nombre) {
        Path archivo = cargarArchivo(nombre);
        try {
            FileSystemUtils.deleteRecursively(archivo);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public byte[] descargarArchivo(String url) throws Exception {
        try (CloseableHttpClient chc = HttpClients.createDefault()) {
            HttpGet hg = new HttpGet(url);
            return chc.execute(hg, respuesta -> {
                int status = respuesta.getCode();
                if (status >= 200 && status < 300) {
                    return EntityUtils.toByteArray(respuesta.getEntity());
                } else {
                    throw new RuntimeException("Error en tiempo de ejecución");
                }
            });
        }
    }

    @Override
    public String nombreArchivo(String url) {
        try {
            URL conexion = new URL(url);
            String path = conexion.getPath();
            String nombre = path.substring(path.lastIndexOf("/") + 1);
            nombre = URLDecoder.decode(nombre, StandardCharsets.UTF_8.name());
            if (nombre.isEmpty()) {
                nombre = "archivo.pdf";
            }
            return nombre;
        } catch (Exception e) {
            return "archivo.pdf";
        }
    }

}
