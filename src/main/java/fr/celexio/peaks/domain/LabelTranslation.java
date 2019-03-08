package fr.celexio.peaks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A LabelTranslation.
 */
@Entity
@Table(name = "labeltranslations")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "labeltranslation")
public class LabelTranslation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "peaks_key", nullable = false)
    private String key;

    @Column(name = "en_value")
    private String enValue;

    @Column(name = "fr_value")
    private String frValue;

    @Column(name = "ar_value")
    private String arValue;

    @Column(name = "sp_value")
    private String spValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public LabelTranslation key(String key) {
        this.key = key;
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEnValue() {
        return enValue;
    }

    public LabelTranslation enValue(String enValue) {
        this.enValue = enValue;
        return this;
    }

    public void setEnValue(String enValue) {
        this.enValue = enValue;
    }

    public String getFrValue() {
        return frValue;
    }

    public LabelTranslation frValue(String frValue) {
        this.frValue = frValue;
        return this;
    }

    public void setFrValue(String frValue) {
        this.frValue = frValue;
    }

    public String getArValue() {
        return arValue;
    }

    public LabelTranslation arValue(String arValue) {
        this.arValue = arValue;
        return this;
    }

    public void setArValue(String arValue) {
        this.arValue = arValue;
    }

    public String getSpValue() {
        return spValue;
    }

    public LabelTranslation spValue(String spValue) {
        this.spValue = spValue;
        return this;
    }

    public void setSpValue(String spValue) {
        this.spValue = spValue;
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
        LabelTranslation labelTranslation = (LabelTranslation) o;
        if (labelTranslation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), labelTranslation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LabelTranslation{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", enValue='" + getEnValue() + "'" +
            ", frValue='" + getFrValue() + "'" +
            ", arValue='" + getArValue() + "'" +
            ", spValue='" + getSpValue() + "'" +
            "}";
    }
}
