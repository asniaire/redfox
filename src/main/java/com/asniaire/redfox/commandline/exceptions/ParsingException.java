package com.asniaire.redfox.commandline.exceptions;

public class ParsingException extends Exception {

    private static final long serialVersionUID = 1L;

    public ParsingException(String message) {
        super(message);
    }

    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
