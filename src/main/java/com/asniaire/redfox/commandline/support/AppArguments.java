package com.asniaire.redfox.commandline.support;

import lombok.Data;

import java.util.Optional;

@Data
public class AppArguments {

    private final String url;

    private final String path;

    private final boolean fullParsing;

    private final Optional<Integer> searchingLevels;

    private final Optional<Integer> threads;

    private final boolean help;

}
