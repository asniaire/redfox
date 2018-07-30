package com.asniaire.redfox;

import com.asniaire.redfox.commandline.api.AppCommandLine;
import com.asniaire.redfox.commandline.exceptions.InvalidArgumentException;
import com.asniaire.redfox.commandline.exceptions.ParsingException;
import com.asniaire.redfox.commandline.support.AppArguments;
import com.asniaire.redfox.crawler.*;
import com.asniaire.redfox.crawler.api.ImageCrawler;
import com.asniaire.redfox.crawler.exceptions.CrawlingException;
import com.asniaire.redfox.crawler.support.AppParamSupport;
import com.asniaire.redfox.crawler.support.stats.CrawlerStats;
import com.asniaire.redfox.persistence.EntityManagerHelper;
import com.asniaire.redfox.persistence.repository.JpaImageRepository;
import com.asniaire.redfox.persistence.repository.ImageRepository;
import com.asniaire.redfox.processor.api.ImageProcessor;
import com.asniaire.redfox.processor.ImageStorage;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Slf4j
public class Starter {

    public static void main(String[] args) {
        initialize();

        final AppCommandLine commandLine = parseCommandLine(args);

        final AppArguments arguments = getArguments(commandLine);

        showHelpIfRequested(commandLine, arguments);

        if (commandLine.isRequiredParametersPresent()) {
            startSearch(arguments);
        } else {
            log.error("Required arguments not present");
            System.err.println("Required arguments not present. Type -h to get help");
        }

        System.exit(0);
    }

    private static void initialize() {
        Properties properties = new Properties(System.getProperties());
        properties.put("com.mchange.v2.log.MLog", "com.mchange.v2.log.FallbackMLog");
        properties.put("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "OFF");
        System.setProperties(properties);
    }

    private static AppCommandLine parseCommandLine(String[] args) {
        try {
            return AppParamSupport.parseCommandLine(args);
        } catch (ParsingException ex) {
            log.error("Error parsing arguments '{}'", args, ex);
            System.err.println(String.format("Error reading arguments. %s. Type -h to get help",
                    ex.getMessage()));
            System.exit(1);
            return null;
        }
    }

    private static AppArguments getArguments(AppCommandLine commandLine) {
        try {
            return AppParamSupport.getArguments(commandLine);
        } catch (InvalidArgumentException ex) {
            log.error("Invalid argument", ex);
            System.err.println(String.format("Invalid argument: %s", ex.getMessage()));
            System.exit(1);
            return null;
        }
    }

    private static void showHelpIfRequested(AppCommandLine commandLine, AppArguments arguments) {
        if (arguments.isHelp()) {
            commandLine.printHelp();
            System.exit(0);
        }
    }

    private static void startSearch(AppArguments arguments) {
        final ImageCrawler imageCrawler = buildImageCrawler(arguments);

        System.out.println("Scanning...");
        final long startTs = System.currentTimeMillis();
        try {
            if (isNumberOfThreadsDefined(arguments)) {
                final Integer numThreads = arguments.getThreads().get();
                System.out.println(String.format("%d thread used", numThreads));
                imageCrawler.start(numThreads);
            } else {
                imageCrawler.start();
            }
        } catch (CrawlingException ex) {
            log.error("Invalid url '{}'", arguments.getUrl());
            System.err.println(String.format("Invalid url '%s'", arguments.getUrl()));
        }
        printStats(imageCrawler.getStats());
        final long endTs = System.currentTimeMillis();
        System.out.println(String.format("Finished. Search took %s milliseconds", endTs - startTs));
    }

    private static boolean isNumberOfThreadsDefined(AppArguments arguments) {
        return arguments.getThreads().isPresent();
    }

    private static ImageCrawler buildImageCrawler(AppArguments arguments) {
        final ImageRepository imageRepository = new JpaImageRepository(getEntityManagerFactory());
        final ImageProcessor imageProcessor = new ImageStorage(imageRepository, arguments.getPath());
        return new JsoupImageCrawler(
                imageProcessor,
                arguments.getUrl(),
                arguments.getSearchingLevels().orElse(1),
                arguments.isFullParsing());
    }

    private static EntityManagerFactory getEntityManagerFactory() {
        return EntityManagerHelper.INSTANCE.getEntityManagerFactory();
    }

    private static void printStats(CrawlerStats stats) {
        System.out.println(String.format(
                        "Scanned images: %d\n" +
                        "Processed images: %d\n" +
                        "Already processed images: %d\n" +
                        "Duplicated images: %d\n" +
                        "Invalid image url: %d\n" +
                        "Invalid image format: %d\n" +
                        "Errors processing image: %d\n" +
                        "Scanned links: %d\n" +
                        "Followed links: %d\n" +
                        "Duplicated links: %d\n",
                stats.getScannedImages(),
                stats.getProcessedImages(),
                stats.getAlreadyProcessedImages(),
                stats.getDuplicatedImageUrls(),
                stats.getInvalidImageUrl(),
                stats.getInvalidImageFormat(),
                stats.getErrorsProcessingImage(),
                stats.getScannedLinks(),
                stats.getFollowedLinks(),
                stats.getDuplicatedLinks()));
    }

}
