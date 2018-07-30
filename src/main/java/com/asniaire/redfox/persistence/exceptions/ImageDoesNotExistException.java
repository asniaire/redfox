package com.asniaire.redfox.persistence.exceptions;

public class ImageDoesNotExistException extends Exception {

    private static final long serialVersionUID = 1L;

    private ImageDoesNotExistException(String message) {
        super(message);
    }

    public static ImageDoesNotExistException ofUrl(String url) {
        return new ImageDoesNotExistException(
                String.format("Image with url '%s' does not exist", url));
    }

    public static ImageDoesNotExistException ofHash(String hash) {
        return new ImageDoesNotExistException(
                String.format("Image with hash '%s' does not exist", hash));
    }
}
