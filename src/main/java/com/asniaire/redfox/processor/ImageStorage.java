package com.asniaire.redfox.processor;

import com.asniaire.redfox.crawler.support.image.ImageInfo;
import com.asniaire.redfox.crawler.support.image.ImageType;
import com.asniaire.redfox.persistence.model.Image;
import com.asniaire.redfox.persistence.repository.ImageRepository;
import com.asniaire.redfox.processor.api.ImageProcessor;
import com.asniaire.redfox.processor.exceptions.ProcessingException;
import com.asniaire.redfox.processor.support.LocalStorage;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Processor that takes every image and store it in disk and database.
 * For that it needs as dependency a local storage service and a image repository, respectively.
 *
 * This class implements the interface {@link ImageProcessor}
 */
@Slf4j
public class ImageStorage implements ImageProcessor {

    private final ImageRepository imageRepository;

    private final LocalStorage localStorage;

    private final Lock lock = new ReentrantLock();

    public ImageStorage(ImageRepository imageRepository, String path) {
        Preconditions.checkNotNull(imageRepository, "imageRepository");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(path),
                "path cannot be null nor empty");
        this.imageRepository = imageRepository;
        this.localStorage = new LocalStorage(path);
    }

    public ImageStorage(ImageRepository imageRepository, LocalStorage localStorage) {
        Preconditions.checkNotNull(imageRepository, "imageRepository");
        Preconditions.checkNotNull(localStorage, "localStorage");
        this.imageRepository = imageRepository;
        this.localStorage = localStorage;
    }

    /**
     *
     * @param imageInfo Image info
     * @return
     * @throws ProcessingException
     */
    @Override
    public boolean process(ImageInfo imageInfo) throws ProcessingException {
        Preconditions.checkNotNull(imageInfo, "imageInfo");
        final String imageUrl = imageInfo.getUrl();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(imageUrl),
                "imageUrl cannot be null nor empty");
        final byte[] imageInputStream = imageInfo.getImageBytes();
        Preconditions.checkNotNull(imageInputStream);
        final ImageType imageType = imageInfo.getImageType();
        log.debug("Processing image info: [imageUrl='{}', imageType='{}']", imageUrl, imageType);

        final String imageHash = getImageHash(imageInputStream);

        lock.lock();
        try {
            if (!isAlreadyProcessed(imageUrl, imageHash)) {
                final String uuid = generateUuid();
                final String fileName = buildFileName(uuid, imageInfo.getImageType().getExtension());

                final String filePath = storeImageInPath(fileName, imageInfo);
                storeImageEntry(imageUrl, uuid, imageHash, filePath, imageInfo.getImageType());
                return true;
            } else {
                log.debug("Image with url '{}' already processed", imageUrl);
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    private String buildFileName(String uuid, String extension) {
        return String.format("%s.%s", uuid, extension);
    }

    private String getImageHash(byte[] bytes) {
        HashFunction hashFunction = Hashing.sha256();
        HashCode hashCode = hashFunction.newHasher()
                .putBytes(bytes)
                .hash();
        return hashCode.toString();
    }

    private boolean isAlreadyProcessed(String url, String hash) {
        return checkByUrl(url) || checkByHash(hash);
    }

    private boolean checkByUrl(String imageUrl) {
        log.debug("Checking if the image with imageUrl '{}' is already processed", imageUrl);
        final boolean existsByUrl = imageRepository.existsByUrl(imageUrl);
        if (existsByUrl) {
            log.debug("Image with imageUrl '{}' already processed", imageUrl);
            return true;
        } else {
            log.debug("Image with imageUrl '{}' has not been processed", imageUrl);
            return false;
        }
    }

    private boolean checkByHash(String imageHash) {
        log.debug("Checking if the image with hash '{}' is already processed", imageHash);
        final boolean existsByHash = imageRepository.existsByHash(imageHash);
        if (existsByHash) {
            log.debug("Image with hash '{}' already processed", imageHash);
            return true;
        } else {
            log.debug("Image with hash '{}' has not been processed", imageHash);
            return false;
        }
    }

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }

    private String storeImageInPath(String fileName, ImageInfo imageInfo) throws ProcessingException {
        try {
            return localStorage.storeFile(fileName, imageInfo.getImageBytes());
        } catch (IOException ex) {
            log.error("Error storing image '{}'", fileName, ex);
            throw new ProcessingException(
                    String.format("Error processing image '%s'", imageInfo.getUrl()), ex);
        }
    }

    private void storeImageEntry(
            String url,
            String uuid,
            String hash,
            String path,
            ImageType imageType) {
        final Image image = new Image();
        image.setUrl(url);
        image.setUuid(uuid);
        image.setHash(hash);
        image.setPath(path);
        image.setImageType(imageType);
        imageRepository.save(image);
    }

}
