package com.asniaire.redfox.crawler.support.stats;

import java.util.concurrent.atomic.AtomicInteger;

public class StatsSupport {

    private final AtomicInteger scannedImages;
    private final AtomicInteger processedImages;
    private final AtomicInteger alreadyProcessedImages;
    private final AtomicInteger duplicatedImageUrls;
    private final AtomicInteger invalidImageUrls;
    private final AtomicInteger invalidImageFormats;
    private final AtomicInteger errorsProcessingImage;
    private final AtomicInteger scannedLinks;
    private final AtomicInteger followedLinks;
    private final AtomicInteger duplicatedLinks;

    public StatsSupport() {
        scannedImages = new AtomicInteger();
        processedImages = new AtomicInteger();
        alreadyProcessedImages = new AtomicInteger();
        duplicatedImageUrls = new AtomicInteger();
        invalidImageUrls = new AtomicInteger();
        invalidImageFormats = new AtomicInteger();
        errorsProcessingImage = new AtomicInteger();
        scannedLinks = new AtomicInteger();
        followedLinks = new AtomicInteger();
        duplicatedLinks = new AtomicInteger();
    }

    public void incScannedImages(int amount) {
        scannedImages.addAndGet(amount);
    }

    public void incProcessedImages(int amount) {
        processedImages.addAndGet(amount);
    }

    public void incDuplicatedImageUrls(int amount) {
        duplicatedImageUrls.addAndGet(amount);
    }

    public void incAlreadyProcessedImages(int amount) {
        alreadyProcessedImages.addAndGet(amount);
    }

    public void incInvalidImageUrls(int amount) {
        invalidImageUrls.addAndGet(amount);
    }

    public void incInvalidImageFormats(int amount) {
        invalidImageFormats.addAndGet(amount);
    }

    public void incErrorsProcessingImage(int amount) {
        errorsProcessingImage.addAndGet(amount);
    }

    public void incScannedLinks(int amount) {
        scannedLinks.addAndGet(amount);
    }

    public void incFollowedLinks(int amount) {
        followedLinks.addAndGet(amount);
    }

    public void incDuplicatedLinks(int amount) {
        duplicatedLinks.addAndGet(amount);
    }

    public CrawlerStats getStats() {
        return new CrawlerStats(
                scannedImages.get(),
                processedImages.get(),
                alreadyProcessedImages.get(),
                duplicatedImageUrls.get(),
                invalidImageUrls.get(),
                invalidImageFormats.get(),
                errorsProcessingImage.get(),
                scannedLinks.get(),
                followedLinks.get(),
                duplicatedLinks.get());
    }

}
