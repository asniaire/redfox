package com.asniaire.redfox.commandline.api;

import com.asniaire.redfox.commandline.exceptions.InvalidArgumentException;
import com.asniaire.redfox.commandline.exceptions.ParsingException;
import com.asniaire.redfox.commandline.support.AppParam;
import com.asniaire.redfox.commandline.support.AppParamName;
import com.asniaire.redfox.commandline.support.ArgumentSupplier;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AppCommandLine {

    private boolean isParsed;

    @Getter(AccessLevel.PROTECTED) private final List<AppParam> params;

    public AppCommandLine(List<AppParam> params) {
        this.params = ImmutableList.copyOf(params);
    }

    public void parse(String[] args) throws ParsingException {
        Preconditions.checkNotNull(args, "args");
        parseCommandLine(args);
        isParsed = true;
    }

    public boolean isRequiredParametersPresent() {
        return params.stream()
                .filter(param -> !param.optional())
                .allMatch(param -> existArgument(param.name()));
    }

    protected abstract void parseCommandLine(String[] args) throws ParsingException;

    public String getStringArgument(AppParamName paramName) throws InvalidArgumentException {
        Preconditions.checkNotNull(paramName, "paramName");
        return getArgumentIfParsedArgs(() -> {
            final String paramValue = getArgumentValue(paramName);
            log.debug("Returning string value '{}' for paramName '{}'", paramValue, paramName.tag());
            return paramValue;
        });
    }

    public Integer getIntegerArgument(AppParamName paramName) throws InvalidArgumentException {
        Preconditions.checkNotNull(paramName, "paramName");
        return getArgumentIfParsedArgs(() -> {
            final String paramValue = getArgumentValue(paramName);
            Integer integerValue = convertToInteger(paramName.tag(), paramValue);
            log.debug("Returning integer value '{}' for paramName '{}'", paramValue, paramName.tag());
            return integerValue;
        });
    }

    private Integer convertToInteger(String paramName, String paramValue)
            throws InvalidArgumentException{
        try {
            return Integer.parseInt(paramValue);
        } catch (NumberFormatException ex) {
            throw new InvalidArgumentException(paramName, paramValue, ex);
        }
    }

    protected abstract String getArgumentValue(AppParamName arg);

    public Optional<Integer> getIntegerArgumentIfPresent(AppParamName paramName)
            throws InvalidArgumentException {
        Preconditions.checkNotNull(paramName, "paramName");
        if (isArgumentPresent(paramName)) {
            return Optional.of(getIntegerArgument(paramName));
        } else {
            return Optional.empty();
        }
    }

    public boolean isArgumentPresent(AppParamName paramName) throws InvalidArgumentException {
        Preconditions.checkNotNull(paramName, "paramName");
        return getArgumentIfParsedArgs(() -> {
            final boolean paramValue = existArgument(paramName);
            log.debug("Is present paramName '{}'? {}", paramName.tag(), paramValue);
            return paramValue;
        });
    }

    protected abstract boolean existArgument(AppParamName arg);

    private <T> T getArgumentIfParsedArgs(ArgumentSupplier<T> supplier) throws InvalidArgumentException {
        if (isParsed) {
            return supplier.get();
        } else {
            throw new IllegalStateException("Arguments has not been parsed");
        }
    }

    public abstract void printHelp();

}
