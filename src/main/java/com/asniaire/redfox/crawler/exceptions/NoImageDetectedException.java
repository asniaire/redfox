package com.asniaire.redfox.crawler.exceptions;

import lombok.Getter;

public class NoImageDetectedException extends Exception {

    private static final long serialVersionUID = 1L;

    @Getter private final String fileName;

    public NoImageDetectedException(String fileName) {
        super(String.format("Image with fileName '%s' is not an image", fileName));
        this.fileName = fileName;
    }

}
