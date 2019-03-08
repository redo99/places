package fr.celexio.peaks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A SocialMedia.
 */
@Entity
@Table(name = "socialmedias")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "socialmedia")
public class SocialMedia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "facebook_link")
    private String facebookLink;

    @Column(name = "google_plus_link")
    private String googlePlusLink;

    @Column(name = "twitter_link")
    private String twitterLink;

    @Column(name = "instagram_link")
    private String instagramLink;

    @Column(name = "pinterest_link")
    private String pinterestLink;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public SocialMedia facebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
        return this;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getGooglePlusLink() {
        return googlePlusLink;
    }

    public SocialMedia googlePlusLink(String googlePlusLink) {
        this.googlePlusLink = googlePlusLink;
        return this;
    }

    public void setGooglePlusLink(String googlePlusLink) {
        this.googlePlusLink = googlePlusLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public SocialMedia twitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
        return this;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public SocialMedia instagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
        return this;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }

    public String getPinterestLink() {
        return pinterestLink;
    }

    public SocialMedia pinterestLink(String pinterestLink) {
        this.pinterestLink = pinterestLink;
        return this;
    }

    public void setPinterestLink(String pinterestLink) {
        this.pinterestLink = pinterestLink;
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
        SocialMedia socialMedia = (SocialMedia) o;
        if (socialMedia.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), socialMedia.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SocialMedia{" +
            "id=" + getId() +
            ", facebookLink='" + getFacebookLink() + "'" +
            ", googlePlusLink='" + getGooglePlusLink() + "'" +
            ", twitterLink='" + getTwitterLink() + "'" +
            ", instagramLink='" + getInstagramLink() + "'" +
            ", pinterestLink='" + getPinterestLink() + "'" +
            "}";
    }
}
