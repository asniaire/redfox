package com.asniaire.redfox.crawler.support;

import com.asniaire.redfox.commandline.api.AppCommandLine;
import com.asniaire.redfox.commandline.CommonsCliCommandLine;
import com.asniaire.redfox.commandline.exceptions.InvalidArgumentException;
import com.asniaire.redfox.commandline.exceptions.ParsingException;
import com.asniaire.redfox.commandline.support.AppArguments;
import com.asniaire.redfox.commandline.support.AppParam;
import com.asniaire.redfox.commandline.support.AppParamName;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AppParamSupport {

    public static AppArguments getArguments(AppCommandLine commandLine) throws InvalidArgumentException {
        return new AppArguments(
                commandLine.getStringArgument(AppParamName.URL),
                commandLine.getStringArgument(AppParamName.PATH),
                commandLine.isArgumentPresent(AppParamName.FULL_SEARCH),
                commandLine.getIntegerArgumentIfPresent(AppParamName.LEVELS),
                commandLine.getIntegerArgumentIfPresent(AppParamName.THREADS),
                commandLine.isArgumentPresent(AppParamName.HELP));
    }

    public static AppCommandLine parseCommandLine(String[] args) throws ParsingException {
        AppCommandLine commandLine = new CommonsCliCommandLine(buildApplicationParams());
        commandLine.parse(args);
        return commandLine;
    }

    public static List<AppParam> buildApplicationParams() {
        List<AppParam> params = new ArrayList<>();
        params.add(new AppParam(
                AppParamName.URL,
                "u",
                "url",
                true,
                "url to scan (required)",
                false));
        params.add(new AppParam(
                AppParamName.PATH,
                "p",
                "path",
                true,
                "local path to store the images (required)",
                false));
        params.add(new AppParam(
                AppParamName.FULL_SEARCH,
                "fs",
                "full-search",
                false,
                "if the execution has to keep finding images recursively - default: false",
                true));
        params.add(new AppParam(
                AppParamName.LEVELS,
                "l",
                "levels",
                true,
                "how many levels has to keep finding images recursively " +
                        "- default: 1 (except if full-search = true)",
                true));
        params.add(new AppParam(
                AppParamName.THREADS,
                "t",
                "threads",
                true,
                "number of threads (default = 4)",
                true));
        params.add(new AppParam(
                AppParamName.HELP,
                "h",
                "help",
                false,
                "show the command line help",
                true));
        return params;
    }

}
