package fr.celexio.peaks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Spot.
 */
@Entity
@Table(name = "spots")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "spot")
public class Spot implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "verified")
    private Boolean verified;

    @OneToOne
    @JoinColumn(unique = true)
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Spot name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isVerified() {
        return verified;
    }

    public Spot verified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Location getLocation() {
        return location;
    }

    public Spot location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Spot spot = (Spot) o;
        if (spot.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), spot.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Spot{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", verified='" + isVerified() + "'" +
            "}";
    }
}
