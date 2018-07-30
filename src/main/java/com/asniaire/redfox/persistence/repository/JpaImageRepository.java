package com.asniaire.redfox.persistence.repository;

import com.asniaire.redfox.persistence.exceptions.ImageDoesNotExistException;
import com.asniaire.redfox.persistence.model.Image;
import com.asniaire.redfox.persistence.repository.exceptions.RepositoryException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class JpaImageRepository implements ImageRepository {

    private static final String URL_PARAM = "url";
    private static final String HASH_PARAM = "hash";

    private final EntityManagerFactory emf;

    public JpaImageRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void save(Image image) {
        log.debug("Saving image: '{}'", image);
        executeTransaction(em -> em.persist(image));
    }

    @Override
    public Image findByUrl(String url) throws ImageDoesNotExistException {
        return maybeFindByUrl(url).orElseThrow(() -> ImageDoesNotExistException.ofUrl(url));
    }

    @Override
    public Optional<Image> maybeFindByUrl(String url) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url),
                "url cannot be null nor empty");
        final Image image = executeQuery(em -> {
            try {
                return getQueryByUrl(em, url).getSingleResult();
            } catch (NoResultException ex) {
                log.debug("No single result found for url '{}'", url);
                return null;
            }
        });
        return Optional.ofNullable(image);
    }

    @Override
    public Image findByHash(String hash) throws ImageDoesNotExistException {
        return maybeFindByHash(hash).orElseThrow(() -> ImageDoesNotExistException.ofHash(hash));
    }

    @Override
    public Optional<Image> maybeFindByHash(String hash) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(hash),
                "hash cannot be null nor empty");
        final Image image = executeQuery(em -> {
            try {
                return getQueryByHash(em, hash).getSingleResult();
            } catch (NoResultException ex) {
                log.debug("No single result found for hash '{}'", hash);
                return null;
            }
        });
        return Optional.ofNullable(image);
    }

    @Override
    public boolean existsByUrl(String url) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url),
                "url cannot be null nor empty");
        final List<Image> images = executeQuery(em -> getQueryByUrl(em, url).getResultList());
        return images.size() > 0;

    }

    @Override
    public boolean existsByHash(String hash) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(hash),
                "hash cannot be null nor empty");
        final List<Image> images = executeQuery(em -> getQueryByHash(em, hash).getResultList());
        return images.size() > 0;
    }

    private TypedQuery<Image> getQueryByUrl(EntityManager em, String url) {
        return em.createNamedQuery(Image.FIND_BY_URL, Image.class)
                .setParameter(URL_PARAM, url);
    }

    private TypedQuery<Image> getQueryByHash(EntityManager em, String hash) {
        return em.createNamedQuery(Image.FIND_BY_HASH, Image.class)
                .setParameter(HASH_PARAM, hash);
    }

    private <T> T executeQuery(Function<EntityManager, T> queryFunction) {
        final EntityManager em = getEntityManager();
        T result = queryFunction.apply(em);
        em.close();
        return result;
    }

    private void executeTransaction(Consumer<EntityManager> transactionConsumer) {
        final EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            transactionConsumer.accept(em);
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RepositoryException(ex);
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}
