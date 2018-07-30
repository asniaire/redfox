package com.asniaire.redfox.commandline.support;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum AppParamName {
    URL("url"),
    PATH("path"),
    FULL_SEARCH("full-search"),
    LEVELS("levels"),
    THREADS("threads"),
    HELP("help");

    private final String tag;

}
