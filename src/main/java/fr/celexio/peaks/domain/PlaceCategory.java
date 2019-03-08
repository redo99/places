package fr.celexio.peaks.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PlaceCategory.
 */
@Entity
@Table(name = "placecategories")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "placecategory")
public class PlaceCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties("")
    private LabelTranslation name;

    @ManyToOne
    @JsonIgnoreProperties("")
    private TextTranslation description;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LabelTranslation getName() {
        return name;
    }

    public PlaceCategory name(LabelTranslation labelTranslation) {
        this.name = labelTranslation;
        return this;
    }

    public void setName(LabelTranslation labelTranslation) {
        this.name = labelTranslation;
    }

    public TextTranslation getDescription() {
        return description;
    }

    public PlaceCategory description(TextTranslation textTranslation) {
        this.description = textTranslation;
        return this;
    }

    public void setDescription(TextTranslation textTranslation) {
        this.description = textTranslation;
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
        PlaceCategory placeCategory = (PlaceCategory) o;
        if (placeCategory.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), placeCategory.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PlaceCategory{" +
            "id=" + getId() +
            "}";
    }
}
