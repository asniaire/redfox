package com.asniaire.redfox.commandline;

import com.asniaire.redfox.commandline.api.AppCommandLine;
import com.asniaire.redfox.commandline.exceptions.InvalidArgumentException;
import com.asniaire.redfox.commandline.exceptions.ParsingException;
import com.asniaire.redfox.commandline.support.AppParamName;
import org.junit.Assert;
import org.junit.Test;
import static com.asniaire.redfox.crawler.support.AppParamSupport.buildApplicationParams;

public class CommandLineTest {

    @Test(expected = IllegalStateException.class)
    public void testAlreadyNotParsedCommandLine() throws InvalidArgumentException {
        AppCommandLine commandLine = new CommonsCliCommandLine(buildApplicationParams());
        commandLine.isArgumentPresent(AppParamName.URL);
    }

    @Test
    public void testHelpPresent() throws ParsingException, InvalidArgumentException {
        final AppCommandLine commandLine = new CommonsCliCommandLine(buildApplicationParams());
        final String[] args = new String[] {"-h"};
        commandLine.parse(args);
        final boolean help = commandLine.isArgumentPresent(AppParamName.HELP);
        Assert.assertTrue(help);
    }

    @Test
    public void testNotRequiredArgsPresent() throws ParsingException, InvalidArgumentException {
        final AppCommandLine commandLine = new CommonsCliCommandLine(buildApplicationParams());
        String[] args = new String[] {"-l 2"};
        commandLine.parse(args);
        Assert.assertFalse(commandLine.isRequiredParametersPresent());

        args = new String[] {"-u http://www.google.com"};
        commandLine.parse(args);
        Assert.assertFalse(commandLine.isRequiredParametersPresent());
    }

    @Test
    public void testRequiredArgsPresent() throws ParsingException, InvalidArgumentException {
        final AppCommandLine commandLine = new CommonsCliCommandLine(buildApplicationParams());
        final String exampleUrl = "https://www.google.com";
        final String examplePath = "/tmp";
        String[] args = new String[] {
                String.format("-u=%s", exampleUrl),
                String.format("-p=%s", examplePath)
        };
        commandLine.parse(args);
        Assert.assertTrue(commandLine.isArgumentPresent(AppParamName.URL));
        Assert.assertTrue(commandLine.isArgumentPresent(AppParamName.PATH));
        Assert.assertEquals(exampleUrl, commandLine.getStringArgument(AppParamName.URL));
        Assert.assertEquals(examplePath, commandLine.getStringArgument(AppParamName.PATH));
        Assert.assertTrue(commandLine.isRequiredParametersPresent());
    }

    @Test
    public void testThreadsParamPresent() throws ParsingException, InvalidArgumentException {
        final AppCommandLine commandLine = new CommonsCliCommandLine(buildApplicationParams());
        final String exampleUrl = "https://www.google.com";
        final String examplePath = "/tmp";
        final int numThreads = 6;
        String[] args = new String[] {
                String.format("-u=%s", exampleUrl),
                String.format("-p=%s", examplePath),
                String.format("-t=%s", numThreads)
        };
        commandLine.parse(args);
        Assert.assertTrue(commandLine.isArgumentPresent(AppParamName.THREADS));
        Assert.assertEquals(numThreads, commandLine.getIntegerArgument(AppParamName.THREADS).intValue());
    }

    @Test(expected = ParsingException.class)
    public void testInvalidParam() throws ParsingException {
        final AppCommandLine commandLine = new CommonsCliCommandLine(buildApplicationParams());
        final String exampleUrl = "https://www.google.com";
        final String examplePath = "/tmp";
        String[] args = new String[] {
                String.format("-u=%s", exampleUrl),
                String.format("-p=%s", examplePath),
                "-l"
        };
        commandLine.parse(args);
    }

}
