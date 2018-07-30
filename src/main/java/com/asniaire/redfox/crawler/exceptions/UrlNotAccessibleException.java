package com.asniaire.redfox.crawler.exceptions;

import lombok.Getter;

public class UrlNotAccessibleException extends Exception {

    private static final long serialVersionUID = 1L;

    @Getter private final String url;

    public UrlNotAccessibleException(String url) {
        super(String.format("Url '%s' cannot be open", url));
        this.url = url;
    }
}
