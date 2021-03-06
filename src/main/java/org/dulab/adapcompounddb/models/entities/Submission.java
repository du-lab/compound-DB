package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.enums.MassSpectrometryType;
import org.hibernate.validator.constraints.URL;

@Entity
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_ATTRIBUTE_KEY = "submission";

    // *************************
    // ***** Entity Fields *****
    // *************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The field Name is required.")
    private String name;

    private String description;

    @NotNull(message = "Date/Time of submission is required.")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTime;

    @Enumerated(EnumType.STRING)
    private MassSpectrometryType massSpectrometryType;

    @Valid
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "Submission2SubmissionCategory",
            joinColumns = {@JoinColumn(name = "SubmissionId")},
            inverseJoinColumns = {@JoinColumn(name = "SubmissionCategoryId")})
    private List<SubmissionCategory> categories;

    @Valid
    @OneToMany(
            mappedBy = "submission",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<SubmissionTag> tags;

    @NotNull(message = "Submission: File list is required.")
    @Valid
    @OneToMany(
            mappedBy = "submission",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.ALL},
            orphanRemoval = true
    )
    private List<File> files;

    @NotNull(message = "You must log in to submit mass spectra to the library.")
    @Valid
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "UserPrincipalId", referencedColumnName = "Id")
    private UserPrincipal user;

    @URL(message = "Submission: The field Reference must be a valid URL.")
    private String reference;

    private String externalId;

    private boolean isPrivate;

    // *******************************
    // ***** Getters and Setters *****
    // *******************************

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "New Study";
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String desription) {
        this.description = desription;
    }

    public MassSpectrometryType getMassSpectrometryType() {
        return massSpectrometryType;
    }

    public void setMassSpectrometryType(MassSpectrometryType massSpectrometryType) {
        this.massSpectrometryType = massSpectrometryType;
    }

    public List<SubmissionCategory> getCategories() {
        return categories;
    }

    public void setCategories(final List<SubmissionCategory> categories) {
        this.categories = categories;
    }

    public SubmissionCategory getCategory(final SubmissionCategoryType type) {
        if (categories != null) {
            return getCategories().stream()
                    .filter(c -> c.getCategoryType() == type)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    public List<SubmissionTag> getTags() {
        return tags;
    }

    public void setTags(final List<SubmissionTag> tags) {
        this.tags = tags;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(final List<File> files) {
        this.files = files;
    }

    public UserPrincipal getUser() {
        return user;
    }

    public void setUser(final UserPrincipal user) {
        this.user = user;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(final Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    // *************************
    // ***** Other methods *****
    // *************************

    public boolean isAuthorized(final UserPrincipal user) {
        boolean authorized = false;
        if (user == null) {
            authorized = false;
        } else if (user.isAdmin()) {
            authorized = true;
        } else if (id != 0) {
            authorized = StringUtils.equals(user.getUsername(), this.getUser().getUsername());
        }

        return authorized;
    }

    @Transient
    public String getTagsAsString() {
        return tags == null ? "" : getTags()
                .stream()
                .map(SubmissionTag::getTagValue)
                .collect(Collectors.joining(", "))
                .trim();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Submission)) {
            return false;
        }
        return id == ((Submission) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public static Submission from(final HttpSession session) {
        return session == null ? null : (Submission) session.getAttribute(SESSION_ATTRIBUTE_KEY);
    }

    public static void assign(final HttpSession session, final Submission submission) {
        session.setAttribute(SESSION_ATTRIBUTE_KEY, submission);
    }

    public static void clear(final HttpSession session) {
        session.removeAttribute(SESSION_ATTRIBUTE_KEY);
    }
}
