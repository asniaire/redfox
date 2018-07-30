package com.asniaire.redfox.crawler.support.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class CrawlerStats {

    private final int scannedImages;

    private final int processedImages;

    private final int alreadyProcessedImages;

    private final int duplicatedImageUrls;

    private final int invalidImageUrl;

    private final int invalidImageFormat;

    private final int errorsProcessingImage;

    private final int scannedLinks;

    private final int followedLinks;

    private final int duplicatedLinks;

}
