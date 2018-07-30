package com.asniaire.redfox.commandline.support;

import com.asniaire.redfox.commandline.exceptions.InvalidArgumentException;

@FunctionalInterface
public interface ArgumentSupplier<T> {

    T get() throws InvalidArgumentException;

}
