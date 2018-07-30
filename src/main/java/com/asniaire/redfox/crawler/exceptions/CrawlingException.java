package com.asniaire.redfox.crawler.exceptions;

import lombok.Getter;

public class CrawlingException extends Exception {

    private static final long serialVersionUID = 1L;

    @Getter private final String url;

    public CrawlingException(String url) {
        super(String.format("Url '%s' cannot be crawled", url));
        this.url = url;
    }

}
