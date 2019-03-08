package fr.celexio.peaks.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A ActivityFamily.
 */
@Entity
@Table(name = "activityfamilies")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "activityfamily")
public class ActivityFamily implements Serializable {

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

    @OneToMany(mappedBy = "family")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Activity> activities = new HashSet<>();

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

    public ActivityFamily name(LabelTranslation labelTranslation) {
        this.name = labelTranslation;
        return this;
    }

    public void setName(LabelTranslation labelTranslation) {
        this.name = labelTranslation;
    }

    public TextTranslation getDescription() {
        return description;
    }

    public ActivityFamily description(TextTranslation textTranslation) {
        this.description = textTranslation;
        return this;
    }

    public void setDescription(TextTranslation textTranslation) {
        this.description = textTranslation;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public ActivityFamily activities(Set<Activity> activities) {
        this.activities = activities;
        return this;
    }

    public ActivityFamily addActivity(Activity activity) {
        this.activities.add(activity);
        activity.setFamily(this);
        return this;
    }

    public ActivityFamily removeActivity(Activity activity) {
        this.activities.remove(activity);
        activity.setFamily(null);
        return this;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
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
        ActivityFamily activityFamily = (ActivityFamily) o;
        if (activityFamily.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activityFamily.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ActivityFamily{" +
            "id=" + getId() +
            "}";
    }
}
