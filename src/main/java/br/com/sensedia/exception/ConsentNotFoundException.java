package br.com.sensedia.exception;

public class ConsentNotFoundException extends RuntimeException {
    public ConsentNotFoundException(String message) {
        super(message);
    }
}
