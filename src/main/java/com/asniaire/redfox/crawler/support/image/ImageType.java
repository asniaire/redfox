package com.asniaire.redfox.crawler.support.image;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ImageType {

    PNG("image/png", "png"),
    JPG("image/jpeg", "jpg");

    private final String mimeType;
    private final String extension;

    private static final ImmutableMap<String, ImageType> mimeTypes;

    static {
        ImmutableMap.Builder<String, ImageType> builder = ImmutableMap.builder();
        Arrays.stream(ImageType.values())
                .forEach(imageType -> builder.put(imageType.mimeType, imageType));
        mimeTypes = builder.build();
    }

    public static ImageType fromMimeType(String mimeType) {
        return mimeTypes.get(mimeType);
    }

}
