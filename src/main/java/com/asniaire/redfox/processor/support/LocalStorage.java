package com.asniaire.redfox.processor.support;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class LocalStorage {

    private final Path storagePath;

    public LocalStorage(String path) {
        storagePath = getSystemPath(path);
    }

    private Path getSystemPath(String path) {
        log.debug("Checking system path '{}' exists", path);
        return Paths.get(path);
    }

    public String storeFile(String fileName, byte[] fileBytes) throws IOException {
        final Path systemPath = buildImageSystemPath(fileName);
        storeImageInPath(systemPath, fileBytes);
        return systemPath.toString();
    }

    private Path buildImageSystemPath(String uuid) {
        return storagePath.resolve(uuid);
    }

    private void storeImageInPath(Path filePath, byte[] fileBytes) throws IOException {
        Files.write(filePath, fileBytes);
    }

}
