package com.asniaire.redfox.persistence;

import com.asniaire.redfox.crawler.support.image.ImageType;
import com.asniaire.redfox.persistence.exceptions.ImageDoesNotExistException;
import com.asniaire.redfox.persistence.model.Image;
import com.asniaire.redfox.persistence.repository.ImageRepository;
import com.asniaire.redfox.persistence.repository.JpaImageRepository;
import com.asniaire.redfox.persistence.repository.exceptions.RepositoryException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class ImageRepositoryTest extends JpaBaseTest {

    public static final String EXAMPLE_IMAGE_URL = "https://www.example.com/example.png";
    public static final String EXAMPLE_IMAGE_HASH = "examplehash";

    private static ImageRepository imageRepository;
    private static Image image;

    @BeforeClass
    public static void init() throws SQLException {
        JpaBaseTest.init();
        imageRepository = new JpaImageRepository(getEntityManagerFactory());
        image = buildExampleImage();
        imageRepository.save(image);
    }

    @Test(expected = RepositoryException.class)
    public void testDuplicateEntry() {
        final Image image2 = buildExampleImage();
        imageRepository.save(image2);
    }

    @Test
    public void testImageSave() {
        Date currentDate = new Date();
        final Image image = buildImage("https://www.example.com/example2.png", "image-hash");
        imageRepository.save(image);
        Assert.assertNotNull(image.getId());
        Assert.assertNotNull(image.getCreationTs());
        Assert.assertTrue(image.getCreationTs().getTime() > currentDate.getTime());
    }

    @Test
    public void testImageExists() {
        final boolean existsByUrl = imageRepository.existsByUrl(EXAMPLE_IMAGE_URL);
        Assert.assertTrue(existsByUrl);
        final boolean existsByHash = imageRepository.existsByHash(EXAMPLE_IMAGE_HASH);
        Assert.assertTrue(existsByHash);
    }

    @Test(expected = ImageDoesNotExistException.class)
    public void testNonExistingUrl() throws ImageDoesNotExistException {
        imageRepository.findByUrl("badurl");
    }

    @Test(expected = ImageDoesNotExistException.class)
    public void testNonExistingHash() throws ImageDoesNotExistException {
        imageRepository.findByHash("badhash");
    }

    @Test
    public void testOptionalNonExistingUrl() {
        final Optional<Image> maybeImage = imageRepository.maybeFindByUrl("badurl");
        Assert.assertFalse(maybeImage.isPresent());
    }

    @Test
    public void testOptionalNonExistingHash() {
        final Optional<Image> maybeImage = imageRepository.maybeFindByHash("badhash");
        Assert.assertFalse(maybeImage.isPresent());
    }

    @Test
    public void testExistingByUrl() throws ImageDoesNotExistException {
        final Image image = imageRepository.findByUrl(EXAMPLE_IMAGE_URL);
        Assert.assertNotNull(image);
        Assert.assertEquals(EXAMPLE_IMAGE_HASH, image.getHash());
        Assert.assertEquals(ImageType.JPG, image.getImageType());
    }

    @Test
    public void testExistingByHash() throws ImageDoesNotExistException {
        final Image image = imageRepository.findByHash(EXAMPLE_IMAGE_HASH);
        Assert.assertNotNull(image);
        Assert.assertEquals(EXAMPLE_IMAGE_URL, image.getUrl());
        Assert.assertEquals(ImageType.JPG, image.getImageType());
    }

    private static Image buildExampleImage() {
        return buildImage(EXAMPLE_IMAGE_URL, EXAMPLE_IMAGE_HASH);
    }

    private static Image buildImage(String url, String hash) {
        final Image image = new Image();
        image.setUrl(url);
        image.setPath("/whateverpath");
        image.setHash(hash);
        image.setUuid(UUID.randomUUID().toString());
        image.setImageType(ImageType.JPG);
        return image;
    }

}
