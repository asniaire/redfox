package com.asniaire.redfox.commandline.exceptions;

import lombok.Getter;

@Getter
public class InvalidArgumentException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String paramName;
    private final String paramValue;

    public InvalidArgumentException(String paramName, String paramValue) {
        super(String.format("Invalid value '%s' for %s param", paramValue, paramName));
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public InvalidArgumentException(String paramName, String paramValue, Throwable ex) {
        super(String.format("Invalid value '%s' for %s param", paramValue, paramName), ex);
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

}
