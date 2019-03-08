package fr.celexio.peaks.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Location.
 */
@Entity
@Table(name = "locations")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "location")
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "peaks_number")
    private String number;

    @Column(name = "address_one")
    private String addressOne;

    @Column(name = "address_two")
    private String addressTwo;

    @Column(name = "region_name")
    private String regionName;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    @Column(name = "country_iso_code")
    private String countryISOCode;

    @Column(name = "lat")
    private Float lat;

    @Column(name = "lng")
    private Float lng;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public Location number(String number) {
        this.number = number;
        return this;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAddressOne() {
        return addressOne;
    }

    public Location addressOne(String addressOne) {
        this.addressOne = addressOne;
        return this;
    }

    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }

    public String getAddressTwo() {
        return addressTwo;
    }

    public Location addressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
        return this;
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
    }

    public String getRegionName() {
        return regionName;
    }

    public Location regionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Location postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public Location city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryISOCode() {
        return countryISOCode;
    }

    public Location countryISOCode(String countryISOCode) {
        this.countryISOCode = countryISOCode;
        return this;
    }

    public void setCountryISOCode(String countryISOCode) {
        this.countryISOCode = countryISOCode;
    }

    public Float getLat() {
        return lat;
    }

    public Location lat(Float lat) {
        this.lat = lat;
        return this;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public Location lng(Float lng) {
        this.lng = lng;
        return this;
    }

    public void setLng(Float lng) {
        this.lng = lng;
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
        Location location = (Location) o;
        if (location.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), location.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Location{" +
            "id=" + getId() +
            ", number='" + getNumber() + "'" +
            ", addressOne='" + getAddressOne() + "'" +
            ", addressTwo='" + getAddressTwo() + "'" +
            ", regionName='" + getRegionName() + "'" +
            ", postalCode='" + getPostalCode() + "'" +
            ", city='" + getCity() + "'" +
            ", countryISOCode='" + getCountryISOCode() + "'" +
            ", lat=" + getLat() +
            ", lng=" + getLng() +
            "}";
    }
}
