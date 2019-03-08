package fr.celexio.peaks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TextTranslation.
 */
@Entity
@Table(name = "texttranslations")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "texttranslation")
public class TextTranslation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "peaks_key", nullable = false)
    private String key;

    @Lob
    @Column(name = "en_value")
    private String enValue;

    @Lob
    @Column(name = "fr_value")
    private String frValue;

    @Lob
    @Column(name = "ar_value")
    private String arValue;

    @Lob
    @Column(name = "es_value")
    private String esValue;

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

    public TextTranslation key(String key) {
        this.key = key;
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEnValue() {
        return enValue;
    }

    public TextTranslation enValue(String enValue) {
        this.enValue = enValue;
        return this;
    }

    public void setEnValue(String enValue) {
        this.enValue = enValue;
    }

    public String getFrValue() {
        return frValue;
    }

    public TextTranslation frValue(String frValue) {
        this.frValue = frValue;
        return this;
    }

    public void setFrValue(String frValue) {
        this.frValue = frValue;
    }

    public String getArValue() {
        return arValue;
    }

    public TextTranslation arValue(String arValue) {
        this.arValue = arValue;
        return this;
    }

    public void setArValue(String arValue) {
        this.arValue = arValue;
    }

    public String getEsValue() {
        return esValue;
    }

    public TextTranslation esValue(String esValue) {
        this.esValue = esValue;
        return this;
    }

    public void setEsValue(String esValue) {
        this.esValue = esValue;
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
        TextTranslation textTranslation = (TextTranslation) o;
        if (textTranslation.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), textTranslation.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TextTranslation{" +
            "id=" + getId() +
            ", key='" + getKey() + "'" +
            ", enValue='" + getEnValue() + "'" +
            ", frValue='" + getFrValue() + "'" +
            ", arValue='" + getArValue() + "'" +
            ", esValue='" + getEsValue() + "'" +
            "}";
    }
}
