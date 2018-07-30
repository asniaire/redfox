package com.asniaire.redfox.crawler.api;

import com.asniaire.redfox.crawler.exceptions.CrawlingException;
import com.asniaire.redfox.crawler.exceptions.NoImageDetectedException;
import com.asniaire.redfox.crawler.exceptions.UrlNotAccessibleException;
import com.asniaire.redfox.crawler.support.image.ImageInfo;
import com.asniaire.redfox.crawler.support.image.ImageType;
import com.asniaire.redfox.crawler.support.image.MimeTypeSupport;
import com.asniaire.redfox.crawler.support.stats.CrawlerStats;
import com.asniaire.redfox.crawler.support.stats.StatsSupport;
import com.asniaire.redfox.processor.api.ImageProcessor;
import com.asniaire.redfox.processor.exceptions.ProcessingException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * This abstract class scans a given url for searching all the images and delegates the processing
 * to the {@link ImageProcessor} received as parameter. For that, it provides a {@link ImageInfo}
 * Subclasses need to inherit this class to provide the implementation of the abstract methods
 * required for getting images and links from a url.
 *
 * The scanning avoid following links that does not belong to the same domain of the original url.
 *
 * Every image is analyzed to check if they are really a image.
 *
 */
@RequiredArgsConstructor
@Slf4j
public abstract class ImageCrawler {

    private static final int DEFAULT_NUM_THREADS = 4;

    @NonNull private final ImageProcessor imageProcessor;
    @NonNull private final String url;
    private final Integer searchingLevels;
    private final boolean isFullParsing;

    private List<String> visitedLinks;
    private List<String> visitedImages;

    private ExecutorService executorService;

    private final MimeTypeSupport mimeTypeSupport = new MimeTypeSupport();
    private StatsSupport stats;

    private String domain;

    public ImageCrawler(ImageProcessor imageProcessor, String url) {
        Preconditions.checkNotNull(imageProcessor, "imageProcessor");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url),
                "url cannot be null nor empty");
        this.imageProcessor = imageProcessor;
        this.url = url;
        this.searchingLevels = 1;
        this.isFullParsing = false;
    }

    public void start(int numThreads) throws CrawlingException {
        startProcess(numThreads);
    }

    public void start() throws CrawlingException {
        startProcess(DEFAULT_NUM_THREADS);
    }

    private void startProcess(int numThreads) throws CrawlingException {
        initialize(numThreads);
        log.debug("Start scanning for root url '{}'", url);
        stats.incScannedLinks(1);
        scan(url, 1);
        waitProcessing();
        log.debug("Finished scanning for root url '{}'", url);
    }

    private void initialize(int numThreads) throws CrawlingException {
        visitedLinks = new ArrayList<>();
        visitedImages = new ArrayList<>();
        stats = new StatsSupport();
        executorService = Executors.newFixedThreadPool(numThreads);
        domain = validateAndExtractDomain(url);
    }

    private String validateAndExtractDomain(String url) throws CrawlingException {
        try {
            URL webUrl = new URL(url);
            return webUrl.getHost();
        } catch (MalformedURLException ex) {
            log.debug("Error checking url '{}'", url, ex);
            throw new CrawlingException(url);
        }
    }

    private void scan(String url, int currentLevel) {
        log.debug("Started scanning for url '{}'", url);
        markLinkAsVisited(url);
        try {
            openUrl(url);
        } catch (UrlNotAccessibleException ex) {
            log.error("Url '{}' cannot be open", url, ex);
            return;
        }
        processImages();
        followLinks(currentLevel);
        log.debug("Finished scanning for url '{}'", url);
    }

    private boolean isReachedLimitLevel(int currentLevel) {
        return searchingLevels != null && currentLevel >= searchingLevels;
    }

    private void markLinkAsVisited(String url) {
        stats.incFollowedLinks(1);
        visitedLinks.add(url);
    }

    private void markImageAsVisited(String url) {
        visitedImages.add(url);
    }

    protected abstract void openUrl(String url) throws UrlNotAccessibleException;

    private void processImages() {
        final List<String> images = getImages();
        stats.incScannedImages(images.size());
        images.stream()
                .filter(this::isNotImageAlreadyVisited)
                .forEach(this::asyncProcess);
    }

    private void asyncProcess(String imageUrl) {
        executorService.submit(() -> {
            markImageAsVisited(imageUrl);
            extractImageInfo(imageUrl).ifPresent(this::processImage);
        });
    }

    protected abstract List<String> getImages();

    private Optional<ImageInfo> extractImageInfo(String imageUrl) {
        final byte[] imageBytes;
        try {
            imageBytes = downloadImage(imageUrl);
        } catch (IOException ex) {
            log.debug("Error opening image url '{}'", imageUrl, ex);
            stats.incInvalidImageUrls(1);
            return Optional.empty();
        }
        final Optional<ImageType> maybeImageType = getImageTypeIfValid(imageBytes, imageUrl);
        if (maybeImageType.isPresent()) {
            return Optional.of(createImageInfo(imageBytes, maybeImageType.get(), imageUrl));
        } else {
            stats.incInvalidImageFormats(1);
            return Optional.empty();
        }
    }

    private byte[] downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        try(InputStream inputStream = url.openStream()) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    private ImageInfo createImageInfo(byte[] imageBytes, ImageType imageType, String imageUrl) {
        return new ImageInfo(imageBytes, imageType, imageUrl);
    }

    private Optional<ImageType> getImageTypeIfValid(byte[] imageBytes, String imageUrl) {
        try {
            final ImageType imageType = mimeTypeSupport.detectImageType(
                    imageBytes, parseFileName(imageUrl));
            log.debug("Url '{}' contains a valid image format", imageUrl);
            return Optional.of(imageType);
        } catch (NoImageDetectedException ex) {
            log.debug("Url '{}' does not contains a valid image format", imageUrl);
            return Optional.empty();
        } catch (MalformedURLException ex) {
            log.debug("Error parsing file name from url '{}'", imageUrl);
            return Optional.empty();
        } catch (IOException ex) {
            log.debug("Error reading mime type from url '{}'", imageUrl);
            return Optional.empty();
        }
    }

    private String parseFileName(String url) throws MalformedURLException {
        final URL urlPath = new URL(url);
        String fileName = urlPath.getFile();
        if (urlPath.getQuery() != null) {
            fileName = fileName.replace(urlPath.getQuery(), "");
        }
        log.debug("Extracted file name '{}' from url '{}'", fileName, url);
        return fileName;
    }

    private void processImage(ImageInfo imageInfo) {
        try {
            final boolean isProcessed = imageProcessor.process(imageInfo);
            if (isProcessed) {
                stats.incProcessedImages(1);
            } else {
                stats.incAlreadyProcessedImages(1);
            }
        } catch (ProcessingException ex) {
            log.error("Error processing image with url '{}'", imageInfo.getUrl());
            stats.incErrorsProcessingImage(1);
        }
    }

    private void followLinks(int currentLevel) {
        if (!isReachedLimitLevel(currentLevel) || isFullParsing) {
            final List<String> links = getLinks();
            stats.incScannedLinks(links.size());
            links.stream()
                    .filter(link -> !Strings.isNullOrEmpty(link))
                    .filter(this::belongToInitialDomain)
                    .filter(this::isNotLinkAlreadyVisited)
                    .forEach(link -> scan(link, currentLevel + 1));
        }
    }

    protected abstract List<String> getLinks();

    private boolean belongToInitialDomain(String url) {
        try {
            final URL webUrl = new URL(url);
            return domain.equals(webUrl.getHost());
        } catch (MalformedURLException ex) {
            log.debug("Error getting domain from url '{}'", url, ex);
            return false;
        }
    }

    private boolean isNotLinkAlreadyVisited(String url) {
        if (visitedLinks.contains(url)) {
            log.debug("Link '{}' already visited", url);
            stats.incDuplicatedLinks(1);
            return false;
        } else {
            log.debug("Link '{}' has not been visited", url);
            return true;
        }
    }

    private boolean isNotImageAlreadyVisited(String url) {
        if (visitedImages.contains(url)) {
            log.debug("Image url '{}' already visited", url);
            stats.incDuplicatedImageUrls(1);
            return false;
        } else {
            log.debug("Image url '{}' has not been visited", url);
            return true;
        }
    }

    private void waitProcessing() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException ex) {
            log.error("Aborted. Scanning is taking too long");
        }
    }

    public CrawlerStats getStats() {
        if (stats == null) {
            throw new IllegalStateException("Scanning has not been started");
        } else {
            final CrawlerStats stats = this.stats.getStats();
            log.debug("Returning stats: {}", stats);
            return stats;
        }
    }

}
