package com.asniaire.redfox.persistence.model;

import com.asniaire.redfox.crawler.support.image.ImageType;
import com.asniaire.redfox.persistence.model.support.Sizes;
import com.asniaire.redfox.persistence.model.support.UuidSupport;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQueries({
        @NamedQuery(
                name = Image.FIND_BY_URL,
                query = "select model from Image model where url = :url"),
        @NamedQuery(
                name = Image.FIND_BY_HASH,
                query = "select model from Image model where hash = :hash")
})
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "image__uuid_uidx", columnNames = "uuid"),
                @UniqueConstraint(name = "image__hash_uidx", columnNames = "hash")
        },
        indexes = {
                @Index(name = "image__creation_ts_idx", columnList = "creationTs")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@ToString
public class Image implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String FIND_BY_URL = "findByUrl";
    public static final String FIND_BY_HASH = "findByHash";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTs;

    @NotNull
    @UuidSupport.CheckedUuid
    private String uuid;

    @NotNull
    @Size(min = Sizes.URL_MIN_LEN, max = Sizes.URL_MAX_LEN)
    private String url;

    @NotNull
    @Size(min = Sizes.PATH_MIN_LEN, max = Sizes.PATH_MAX_LEN)
    private String path;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ImageType imageType;

    @NotNull
    @Size(min = Sizes.HASH_MIN_LEN, max = Sizes.HASH_MAX_LEN)
    private String hash;

    @PrePersist
    public void prePersist() {
        this.creationTs = new Date();
    }

}
