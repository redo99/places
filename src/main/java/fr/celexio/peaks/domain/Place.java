package fr.celexio.peaks.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Place.
 */
@Entity
@Table(name = "places")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "place")
public class Place implements Serializable {

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

    @Column(name = "published")
    private Boolean published;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "google_place_id")
    private String googlePlaceId;

    @OneToOne
    @JoinColumn(unique = true)
    private Location location;

    @OneToOne
    @JoinColumn(unique = true)
    private Picture mainPicture;

    @OneToOne
    @JoinColumn(unique = true)
    private SocialMedia socialMedias;

    @OneToMany(mappedBy = "place")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Picture> pictures = new HashSet<>();

    @OneToMany(mappedBy = "place")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Video> videos = new HashSet<>();

    @OneToMany(mappedBy = "place")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PlaceReview> reviews = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("")
    private TextTranslation description;

    @ManyToOne
    @JsonIgnoreProperties("")
    private LabelTranslation shortDescription;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "place_category",
               joinColumns = @JoinColumn(name = "places_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "categories_id", referencedColumnName = "id"))
    private Set<PlaceCategory> categories = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "place_activity",
               joinColumns = @JoinColumn(name = "places_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "activities_id", referencedColumnName = "id"))
    private Set<Activity> activities = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "place_management",
               joinColumns = @JoinColumn(name = "places_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "managements_id", referencedColumnName = "id"))
    private Set<PlaceManagement> managements = new HashSet<>();

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

    public Place name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isVerified() {
        return verified;
    }

    public Place verified(Boolean verified) {
        this.verified = verified;
        return this;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean isPublished() {
        return published;
    }

    public Place published(Boolean published) {
        this.published = published;
        return this;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getPhone() {
        return phone;
    }

    public Place phone(String phone) {
        this.phone = phone;
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public Place email(String email) {
        this.email = email;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public Place website(String website) {
        this.website = website;
        return this;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getGooglePlaceId() {
        return googlePlaceId;
    }

    public Place googlePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
        return this;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

    public Location getLocation() {
        return location;
    }

    public Place location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Picture getMainPicture() {
        return mainPicture;
    }

    public Place mainPicture(Picture picture) {
        this.mainPicture = picture;
        return this;
    }

    public void setMainPicture(Picture picture) {
        this.mainPicture = picture;
    }

    public SocialMedia getSocialMedias() {
        return socialMedias;
    }

    public Place socialMedias(SocialMedia socialMedia) {
        this.socialMedias = socialMedia;
        return this;
    }

    public void setSocialMedias(SocialMedia socialMedia) {
        this.socialMedias = socialMedia;
    }

    public Set<Picture> getPictures() {
        return pictures;
    }

    public Place pictures(Set<Picture> pictures) {
        this.pictures = pictures;
        return this;
    }

    public Place addPicture(Picture picture) {
        this.pictures.add(picture);
        picture.setPlace(this);
        return this;
    }

    public Place removePicture(Picture picture) {
        this.pictures.remove(picture);
        picture.setPlace(null);
        return this;
    }

    public void setPictures(Set<Picture> pictures) {
        this.pictures = pictures;
    }

    public Set<Video> getVideos() {
        return videos;
    }

    public Place videos(Set<Video> videos) {
        this.videos = videos;
        return this;
    }

    public Place addVideo(Video video) {
        this.videos.add(video);
        video.setPlace(this);
        return this;
    }

    public Place removeVideo(Video video) {
        this.videos.remove(video);
        video.setPlace(null);
        return this;
    }

    public void setVideos(Set<Video> videos) {
        this.videos = videos;
    }

    public Set<PlaceReview> getReviews() {
        return reviews;
    }

    public Place reviews(Set<PlaceReview> placeReviews) {
        this.reviews = placeReviews;
        return this;
    }

    public Place addReview(PlaceReview placeReview) {
        this.reviews.add(placeReview);
        placeReview.setPlace(this);
        return this;
    }

    public Place removeReview(PlaceReview placeReview) {
        this.reviews.remove(placeReview);
        placeReview.setPlace(null);
        return this;
    }

    public void setReviews(Set<PlaceReview> placeReviews) {
        this.reviews = placeReviews;
    }

    public TextTranslation getDescription() {
        return description;
    }

    public Place description(TextTranslation textTranslation) {
        this.description = textTranslation;
        return this;
    }

    public void setDescription(TextTranslation textTranslation) {
        this.description = textTranslation;
    }

    public LabelTranslation getShortDescription() {
        return shortDescription;
    }

    public Place shortDescription(LabelTranslation labelTranslation) {
        this.shortDescription = labelTranslation;
        return this;
    }

    public void setShortDescription(LabelTranslation labelTranslation) {
        this.shortDescription = labelTranslation;
    }

    public Set<PlaceCategory> getCategories() {
        return categories;
    }

    public Place categories(Set<PlaceCategory> placeCategories) {
        this.categories = placeCategories;
        return this;
    }

    public Place addCategory(PlaceCategory placeCategory) {
        this.categories.add(placeCategory);
        return this;
    }

    public Place removeCategory(PlaceCategory placeCategory) {
        this.categories.remove(placeCategory);
        return this;
    }

    public void setCategories(Set<PlaceCategory> placeCategories) {
        this.categories = placeCategories;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public Place activities(Set<Activity> activities) {
        this.activities = activities;
        return this;
    }

    public Place addActivity(Activity activity) {
        this.activities.add(activity);
        return this;
    }

    public Place removeActivity(Activity activity) {
        this.activities.remove(activity);
        return this;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }

    public Set<PlaceManagement> getManagements() {
        return managements;
    }

    public Place managements(Set<PlaceManagement> placeManagements) {
        this.managements = placeManagements;
        return this;
    }

    public Place addManagement(PlaceManagement placeManagement) {
        this.managements.add(placeManagement);
        return this;
    }

    public Place removeManagement(PlaceManagement placeManagement) {
        this.managements.remove(placeManagement);
        return this;
    }

    public void setManagements(Set<PlaceManagement> placeManagements) {
        this.managements = placeManagements;
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
        Place place = (Place) o;
        if (place.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), place.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Place{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", verified='" + isVerified() + "'" +
            ", published='" + isPublished() + "'" +
            ", phone='" + getPhone() + "'" +
            ", email='" + getEmail() + "'" +
            ", website='" + getWebsite() + "'" +
            ", googlePlaceId='" + getGooglePlaceId() + "'" +
            "}";
    }
}
