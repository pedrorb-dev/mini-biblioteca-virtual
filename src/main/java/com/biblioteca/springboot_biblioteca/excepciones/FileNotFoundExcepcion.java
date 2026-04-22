package com.biblioteca.springboot_biblioteca.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class FileNotFoundExcepcion extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FileNotFoundExcepcion(String mensaje) {
        super(mensaje);
    }

    public FileNotFoundExcepcion(String mensaje, Throwable excep) {
        super(mensaje, excep);
    }
}
