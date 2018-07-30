package com.asniaire.redfox.processor;

import com.asniaire.redfox.crawler.support.image.ImageInfo;
import com.asniaire.redfox.crawler.support.image.ImageType;
import com.asniaire.redfox.persistence.repository.ImageRepository;
import com.asniaire.redfox.processor.api.ImageProcessor;
import com.asniaire.redfox.processor.exceptions.ProcessingException;
import com.asniaire.redfox.processor.support.LocalStorage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.io.IOException;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ImageProcessorTest {

    public static final String EXAMPLE_URL = "http://www.example.com";

    @Mock private ImageRepository imageRepository;
    @Mock private LocalStorage localStorage;

    @Test
    public void testAlreadyProcessedUrl() throws ProcessingException, IOException {
        final String fileName = "example.jpg";
        final String imageUrl = buildExampleUrl(fileName);
        final byte[] imageBytes = buildFakeImage();
        when(imageRepository.existsByUrl(imageUrl)).thenReturn(true);
        ImageProcessor processor = new ImageStorage(imageRepository, localStorage);
        final boolean isProcessed = processor.process(buildImageInfo(imageBytes, ImageType.JPG, imageUrl));
        Assert.assertFalse(isProcessed);
        verify(localStorage, never()).storeFile(fileName, imageBytes);
        verify(imageRepository, times(1)).existsByUrl(imageUrl);
        verify(imageRepository, never()).existsByHash(anyString());
        verify(imageRepository, never()).save(any());
    }

    @Test
    public void testImageProcessed() throws ProcessingException, IOException {
        final String path = "/tmp";
        final String fileName = "example.jpg";
        final String imageUrl = buildExampleUrl(fileName);
        final byte[] imageBytes = buildFakeImage();
        when(imageRepository.existsByUrl(imageUrl)).thenReturn(false);
        when(localStorage.storeFile(fileName, imageBytes)).thenReturn(String.format("%s/%s", path, fileName));
        ImageProcessor processor = new ImageStorage(imageRepository, localStorage);
        final boolean isProcessed = processor.process(buildImageInfo(imageBytes, ImageType.JPG, imageUrl));
        Assert.assertTrue(isProcessed);
        verify(imageRepository, times(1)).existsByUrl(imageUrl);
        verify(imageRepository, times(1)).existsByHash(anyString());
        verify(localStorage, times(1)).storeFile(anyString(), any());
        verify(imageRepository, times(1)).save(any());
    }

    @Test(expected = Exception.class)
    public void testErrorProcessing() throws ProcessingException, IOException {
        final String fileName = "example.jpg";
        final String imageUrl = buildExampleUrl(fileName);
        final byte[] imageBytes = buildFakeImage();
        when(imageRepository.existsByUrl(imageUrl)).thenReturn(false);
        when(localStorage.storeFile(fileName, imageBytes)).thenThrow(new ProcessingException(""));
        ImageProcessor processor = new ImageStorage(imageRepository, localStorage);
        processor.process(buildImageInfo(imageBytes, ImageType.JPG, imageUrl));
        verify(imageRepository, times(1)).existsByUrl(imageUrl);
        verify(imageRepository, times(1)).existsByHash(anyString());
        verify(localStorage, times(1)).storeFile(anyString(), any());
        verify(imageRepository, never()).save(any());
    }

    private String buildExampleUrl(String fileName) {
        return String.format("%s/%s", EXAMPLE_URL, fileName);
    }

    private byte[] buildFakeImage() {
        return new byte[] {};
    }

    private ImageInfo buildImageInfo(byte[] imageBytes, ImageType imageType, String url) {
        return new ImageInfo(imageBytes, imageType, url);
    }
}
