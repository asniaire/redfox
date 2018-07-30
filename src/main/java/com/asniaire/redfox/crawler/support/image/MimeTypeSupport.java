package com.asniaire.redfox.crawler.support.image;

import com.asniaire.redfox.crawler.exceptions.NoImageDetectedException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import java.io.IOException;

@Slf4j
public class MimeTypeSupport {

    private final Tika detector;

    public MimeTypeSupport() {
        this.detector = new Tika();
    }

    public ImageType detectImageType(byte[] imageBytes, String fileName)
            throws NoImageDetectedException, IOException {
        Preconditions.checkNotNull(imageBytes, "imageBytes");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(fileName),
                "fileName cannot be null nor empty");

        final String mimeType = detectMimeType(imageBytes, fileName);
        final ImageType imageType = parseMimeType(mimeType);
        if (imageType == null) {
            log.debug("No image type detected for file name '{}'", fileName);
            throw new NoImageDetectedException(fileName);
        } else {
            log.debug("Image type '{}' detected for file name '{}'",
                    imageType.getMimeType(), fileName);
            return imageType;
        }

    }

    private String detectMimeType(byte[] imageBytes, String fileName) {
        Preconditions.checkNotNull(imageBytes);
        return detector.detect(imageBytes, fileName);
    }

    private ImageType parseMimeType(String mimeType) {
        return ImageType.fromMimeType(mimeType);
    }

}
