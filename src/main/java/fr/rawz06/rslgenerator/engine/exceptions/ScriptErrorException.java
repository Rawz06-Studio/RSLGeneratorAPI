package fr.rawz06.rslgenerator.engine.exceptions;

public class ScriptErrorException extends RuntimeException {
    public ScriptErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
