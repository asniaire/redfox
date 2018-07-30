package com.asniaire.redfox.processor.api;

import com.asniaire.redfox.crawler.api.ImageCrawler;
import com.asniaire.redfox.crawler.support.image.ImageInfo;
import com.asniaire.redfox.processor.exceptions.ProcessingException;

/**
 * Interface used by {@link ImageCrawler} to process every detected image.
 * Process method can be call from different threads, so the implementation needs to take
 * this into account to avoid race conditions or whatever other concurrency issue.
 */
public interface ImageProcessor {

    boolean process(ImageInfo imageInfo) throws ProcessingException;

}
