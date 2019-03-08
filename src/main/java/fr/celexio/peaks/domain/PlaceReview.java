package fr.celexio.peaks.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A PlaceReview.
 */
@Entity
@Table(name = "placereviews")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "placereview")
public class PlaceReview implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "content")
    private String content;

    @NotNull
    @Max(value = 5)
    @Column(name = "score", nullable = false)
    private Integer score;

    @ManyToOne
    @JsonIgnoreProperties("reviews")
    private Place place;

    @ManyToOne
    @JsonIgnoreProperties("")
    private Member reviewer;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public PlaceReview content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getScore() {
        return score;
    }

    public PlaceReview score(Integer score) {
        this.score = score;
        return this;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Place getPlace() {
        return place;
    }

    public PlaceReview place(Place place) {
        this.place = place;
        return this;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public Member getReviewer() {
        return reviewer;
    }

    public PlaceReview reviewer(Member member) {
        this.reviewer = member;
        return this;
    }

    public void setReviewer(Member member) {
        this.reviewer = member;
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
        PlaceReview placeReview = (PlaceReview) o;
        if (placeReview.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), placeReview.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PlaceReview{" +
            "id=" + getId() +
            ", content='" + getContent() + "'" +
            ", score=" + getScore() +
            "}";
    }
}
