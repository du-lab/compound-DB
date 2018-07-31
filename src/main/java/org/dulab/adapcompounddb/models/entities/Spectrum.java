package org.dulab.adapcompounddb.models.entities;

import java.io.Serializable;
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
@SqlResultSetMapping(name = "SpectrumScoreMapping", columns = {@ColumnResult(name = "SpectrumId", type = Long.class),
        @ColumnResult(name = "Score", type = Double.class)})
public class Spectrum implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String NAME_PROPERTY_NAME = "Name";
    private static final String PRECURSOR_MASS_PROPERTY_NAME = "PrecursorMZ";
    private static final String RETENTION_TIME_PROPERTY_NAME = "RT";
    private static final String REFERENCE_PROPERTY_NAME = "IS_REFERENCE";

    // *************************
    // ***** Entity fields *****
    // *************************

    private String name = null;

    private long id;

    private File file;

    @NotNull(message = "Spectrum: peak list is required.")
    @Valid
    private List<Peak> peaks;

    private List<SpectrumProperty> properties;

    private List<SpectrumMatch> matches;

    private List<SpectrumMatch> matches2;

    private SpectrumCluster cluster;

    private boolean consensus;

    private boolean reference;

    private Double precursor;

    private Double retentionTime;

    @NotNull(message = "Spectrum: the field Chromatography Type is required.")
    private ChromatographyType chromatographyType;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        String fullName = name;
        if (fullName == null)
            fullName = "UNKNOWN";
        if (reference)
            fullName = "[RS] " + fullName;
        if (consensus)
            fullName = "[CS] " + fullName;
        return fullName;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FileId", referencedColumnName = "Id")
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @OneToMany(targetEntity = Peak.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Peak> getPeaks() {
        return peaks;
    }

    public void setPeaks(List<Peak> peaks) {

        if (peaks == null)
            return;

        double totalIntensity = peaks.stream().mapToDouble(Peak::getIntensity).sum();

        if (totalIntensity <= 0.0)
            return;

        for (Peak peak : peaks)
            peak.setIntensity(peak.getIntensity() / totalIntensity);

        this.peaks = peaks;
    }

    @OneToMany(targetEntity = SpectrumProperty.class, mappedBy = "spectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<SpectrumProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SpectrumProperty> properties) {

        if (properties == null)
            return;

        this.properties = properties;
        for (SpectrumProperty property : properties) {

            if (property.getName().equalsIgnoreCase(NAME_PROPERTY_NAME))
                this.setName(property.getValue());

            else if (property.getName().equalsIgnoreCase(PRECURSOR_MASS_PROPERTY_NAME))
                this.setPrecursor(Double.valueOf(property.getValue()));

            else if (property.getName().equalsIgnoreCase(RETENTION_TIME_PROPERTY_NAME))
                this.setRetentionTime(Double.valueOf(property.getValue()));

            else if (property.getName().equalsIgnoreCase(REFERENCE_PROPERTY_NAME))
                this.setReference(true);
        }
    }

    @OneToMany(targetEntity = SpectrumMatch.class, mappedBy = "querySpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<SpectrumMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<SpectrumMatch> matches) {
        this.matches = matches;
    }

    @OneToMany(mappedBy = "matchSpectrum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<SpectrumMatch> getMatches2() {
        return matches2;
    }

    public void setMatches2(List<SpectrumMatch> matches2) {
        this.matches2 = matches2;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClusterId", referencedColumnName = "Id")
    public SpectrumCluster getCluster() {
        return cluster;
    }

    public void setCluster(SpectrumCluster cluster) {
        this.cluster = cluster;
    }

    public boolean isConsensus() {
        return consensus;
    }

    public void setConsensus(boolean consensus) {
        this.consensus = consensus;
    }

    public boolean isReference() {
        return reference;
    }

    public void setReference(boolean reference) {
        this.reference = reference;
    }

    public Double getPrecursor() {
        return precursor;
    }

    public void setPrecursor(Double precursor) {
        this.precursor = precursor;
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    @Enumerated(EnumType.STRING)
    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }

    // ****************************
    // ***** Standard methods *****
    // ****************************

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Spectrum))
            return false;
        return id == ((Spectrum) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return getName();
    }
}
