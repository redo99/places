package fr.celexio.peaks.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Member.
 */
@Entity
@Table(name = "members")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "member")
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @ManyToOne
    @JsonIgnoreProperties("")
    private MemberCategory category;

    @ManyToOne
    @JsonIgnoreProperties("")
    private MemberStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public Member user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MemberCategory getCategory() {
        return category;
    }

    public Member category(MemberCategory memberCategory) {
        this.category = memberCategory;
        return this;
    }

    public void setCategory(MemberCategory memberCategory) {
        this.category = memberCategory;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public Member status(MemberStatus memberStatus) {
        this.status = memberStatus;
        return this;
    }

    public void setStatus(MemberStatus memberStatus) {
        this.status = memberStatus;
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
        Member member = (Member) o;
        if (member.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Member{" +
            "id=" + getId() +
            "}";
    }
}
