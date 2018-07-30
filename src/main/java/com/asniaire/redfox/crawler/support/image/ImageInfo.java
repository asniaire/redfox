package com.asniaire.redfox.crawler.support.image;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class ImageInfo {

    @NonNull private final byte[] imageBytes;

    @NonNull private final ImageType imageType;

    @NonNull private final String url;

}
