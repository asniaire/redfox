package com.asniaire.redfox.persistence.repository;

import com.asniaire.redfox.persistence.exceptions.ImageDoesNotExistException;
import com.asniaire.redfox.persistence.model.Image;
import java.util.Optional;

public interface ImageRepository {

    void save(Image image);

    Optional<Image> maybeFindByUrl(String url);

    Optional<Image> maybeFindByHash(String hash);

    Image findByUrl(String url) throws ImageDoesNotExistException;

    Image findByHash(String hash) throws ImageDoesNotExistException;

    boolean existsByUrl(String url);

    boolean existsByHash(String hash);

}
