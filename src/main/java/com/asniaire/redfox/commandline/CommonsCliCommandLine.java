package com.asniaire.redfox.commandline;

import com.asniaire.redfox.commandline.api.AppCommandLine;
import com.asniaire.redfox.commandline.exceptions.ParsingException;
import com.asniaire.redfox.commandline.support.AppParam;
import com.asniaire.redfox.commandline.support.AppParamName;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CommonsCliCommandLine extends AppCommandLine {

    private CommandLine commandLine;

    private Options options;

    public CommonsCliCommandLine(List<AppParam> params) {
        super(params);
        buildOptions(params);
    }

    @Override
    protected void parseCommandLine(String[] args) throws ParsingException {
        try {
            CommandLineParser parser = new DefaultParser();
            commandLine = parser.parse(options, args);
        } catch (ParseException ex) {
            log.error("Error parsing arguments '{}", args, ex);
            throw new ParsingException(String.format("Error parsing arguments: %s. %s",
                    Arrays.toString(args), ex.getMessage()));
        }
    }

    private void buildOptions(List<AppParam> params) {
        options = new Options();
        params.forEach(param ->
                options.addOption(
                        param.option(),
                        param.longOption(),
                        param.hasArgument(),
                        param.description()));
    }

    @Override
    protected boolean existArgument(AppParamName paramName) {
        return commandLine.hasOption(paramName.tag());
    }

    @Override
    protected String getArgumentValue(AppParamName paramName) {
        return commandLine.getOptionValue(paramName.tag());
    }

    @Override
    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("redfox", options);
    }

}
