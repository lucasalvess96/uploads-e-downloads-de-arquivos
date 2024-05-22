package com.arquivos.uploadsdownloads.pdf.comum;

public class ErroRequest extends RuntimeException {
    public ErroRequest(String message, Throwable cause) {
        super(message, cause);
    }
}
