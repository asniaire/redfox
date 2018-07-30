package com.asniaire.redfox.commandline.support;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class AppParam {

    private final AppParamName name;

    private final String option;

    private final String longOption;

    private final boolean hasArgument;

    private final String description;

    private final boolean optional;

}
