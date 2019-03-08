package fr.celexio.peaks.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Activity.
 */
@Entity
@Table(name = "activities")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "verified")
    private Boolean verified;

    @ManyToOne
    @JsonIgnoreProperties("activities")
    private ActivityFamily family;

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

    public Boolean isVerified() {
        return verified;
    }

    public Activity verified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public ActivityFamily getFamily() {
        return family;
    }

    public Activity family(ActivityFamily activityFamily) {
        this.family = activityFamily;
        return this;
    }

    public void setFamily(ActivityFamily activityFamily) {
        this.family = activityFamily;
    }

    public LabelTranslation getName() {
        return name;
    }

    public Activity name(LabelTranslation labelTranslation) {
        this.name = labelTranslation;
        return this;
    }

    public void setName(LabelTranslation labelTranslation) {
        this.name = labelTranslation;
    }

    public TextTranslation getDescription() {
        return description;
    }

    public Activity description(TextTranslation textTranslation) {
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
        Activity activity = (Activity) o;
        if (activity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", verified='" + isVerified() + "'" +
            "}";
    }
}
