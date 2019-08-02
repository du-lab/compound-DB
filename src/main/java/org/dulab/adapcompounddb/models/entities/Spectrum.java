package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.dulab.adapcompounddb.models.ChromatographyType;

@Entity
@SqlResultSetMapping(name = "SpectrumScoreMapping", columns = { @ColumnResult(name = "SpectrumId", type = Long.class),
        @ColumnResult(name = "Score", type = Double.class) })
public class Spectrum implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String NAME_PROPERTY_NAME = "Name";
    private static final String PRECURSOR_MASS_PROPERTY_NAME = "PrecursorMZ";
    private static final String RETENTION_TIME_PROPERTY_NAME = "RT";
    private static final String SIGNIFICANCE_PROPERTY_NAME = "ANOVA_P_VALUE";

    // *************************
    // ***** Entity fields *****
    // *************************

    private String name = null;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FileId", referencedColumnName = "Id")
    private File file;

    @NotNull(message = "Spectrum: peak list is required.")
    @Valid
    @OneToMany(targetEntity = Peak.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<Peak> peaks;

    @OneToMany(targetEntity = SpectrumProperty.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    private List<SpectrumProperty> properties;

    @OneToMany(targetEntity = SpectrumMatch.class, mappedBy = "querySpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpectrumMatch> matches;

    @OneToMany(mappedBy = "matchSpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SpectrumMatch> matches2;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {})
    @JoinColumn(name = "ClusterId", referencedColumnName = "Id")
    private SpectrumCluster cluster;

    private boolean consensus;

    private boolean reference;

    private Double precursor;

    private Double retentionTime;

    private Double significance;

    @NotNull(message = "Spectrum: the field Chromatography Type is required.")
    @Enumerated(EnumType.STRING)
    private ChromatographyType chromatographyType;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        String fullName = name;
        if (fullName == null) {
            fullName = "UNKNOWN";
        }
        if (reference) {
            fullName = "[Ref Spec] " + fullName;
        }
        if (consensus) {
            fullName = "[Con Spec] " + fullName;
        }
        return fullName;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public List<Peak> getPeaks() {
        return peaks;
    }

    public void setPeaks(final List<Peak> peaks) {
        setPeaks(peaks, false);
    }

    public void setPeaks(final List<Peak> peaks, final boolean normalize) {

        this.peaks = peaks;

        if (peaks != null && normalize) {

            final double totalIntensity = peaks.stream()
                    .mapToDouble(Peak::getIntensity)
                    .sum();

            for (final Peak peak : peaks) {
                peak.setIntensity(peak.getIntensity() / totalIntensity);
            }
        }
    }

    public List<SpectrumProperty> getProperties() {
        return properties;
    }

    public void setProperties(final List<SpectrumProperty> properties) {
        this.properties = properties;
    }

    public void addProperty(final String name, final String value) {

        if (properties == null) {
            properties = new ArrayList<>();
        }

        if (name.equalsIgnoreCase(NAME_PROPERTY_NAME)) {
            setName(value);
        } else if (name.equalsIgnoreCase(PRECURSOR_MASS_PROPERTY_NAME)) {
            setPrecursor(Double.valueOf(value));
        } else if (name.equalsIgnoreCase(SIGNIFICANCE_PROPERTY_NAME)) {
            setSignificance(Double.valueOf(value));
        } else if (name.equalsIgnoreCase(RETENTION_TIME_PROPERTY_NAME)) {
            setRetentionTime(Double.valueOf(value));
        }

        final SpectrumProperty property = new SpectrumProperty();
        property.setName(name);
        property.setValue(value);
        property.setSpectrum(this);
        properties.add(property);
    }

    public List<SpectrumMatch> getMatches() {
        return matches;
    }

    public void setMatches(final List<SpectrumMatch> matches) {
        this.matches = matches;
    }

    public List<SpectrumMatch> getMatches2() {
        return matches2;
    }

    public void setMatches2(final List<SpectrumMatch> matches2) {
        this.matches2 = matches2;
    }

    public SpectrumCluster getCluster() {
        return cluster;
    }

    public void setCluster(final SpectrumCluster cluster) {
        this.cluster = cluster;
    }

    public boolean isConsensus() {
        return consensus;
    }

    public void setConsensus(final boolean consensus) {
        this.consensus = consensus;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(final boolean reference) {
        this.reference = reference;
    }

    public Double getPrecursor() {
        return precursor;
    }

    public void setPrecursor(final Double precursor) {
        this.precursor = precursor;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(final Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(final ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    public Double getSignificance() {
        return significance;
    }

    public void setSignificance(final Double significance) {
        this.significance = significance;
    }

    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Spectrum)) {
            return false;
        }
        if(id == 0) {
            return super.equals(other);
        }
        return id == ((Spectrum) other).id;
    }

    @Override
    public int hashCode() {
        if(id == 0) {
            return super.hashCode();
        } else {
            return Long.hashCode(id);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
