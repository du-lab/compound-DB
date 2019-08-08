package org.dulab.adapcompounddb.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
public class SpectrumCluster implements Serializable {

    private static final long serialVersionUID = 1L;

    // *************************
    // ***** Entity fields *****
    // *************************

    private long id;

    private Spectrum consensusSpectrum;

    @NotNull(message = "Diameter of cluster is required.")
    private Double diameter;

    @NotNull(message = "Size of cluster is required.")
    private Integer size;

    private Double aveSignificance;
    private Double minSignificance;
    private Double maxSignificance;

    private Double aveDiversity;
    private Double minDiversity;
    private Double maxDiversity;

    private Double minPValue;
    private Double diseasePValue;
    private Double speciesPValue;
    private Double sampleSourcePValue;

    private List<Spectrum> spectra;

    private List<TagDistribution> tagDistributions;

    private Set<DiversityIndex> diversityIndices;

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ConsensusSpectrumId", referencedColumnName = "Id")
    @JsonIgnore
    public Spectrum getConsensusSpectrum() {
        return consensusSpectrum;
    }

    public void setConsensusSpectrum(final Spectrum consensusSpectrum) {
        this.consensusSpectrum = consensusSpectrum;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(final Double diameter) {
        this.diameter = diameter;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    @OneToMany(
            mappedBy = "cluster",
            fetch = FetchType.LAZY)
    public List<Spectrum> getSpectra() {
        return spectra;
    }

    public void setSpectra(final List<Spectrum> spectra) {
        this.spectra = spectra;
    }


    @OneToMany(
            mappedBy = "cluster",
            fetch = FetchType.LAZY)
    public List<TagDistribution> getTagDistributions() {
        return tagDistributions;
    }

    public void setTagDistributions(final List<TagDistribution> tagDistributions) {
        this.tagDistributions = tagDistributions;
    }


    @OneToMany(
            mappedBy = "id.cluster",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    public Set<DiversityIndex> getDiversityIndices() {
        return diversityIndices;
    }

    public void setDiversityIndices(final Set<DiversityIndex> diversityIndices) {
        this.diversityIndices = diversityIndices;
    }

    public Double getAveSignificance() {
        return aveSignificance;
    }

    public void setAveSignificance(final Double aveSignificance) {
        this.aveSignificance = aveSignificance;
    }

    public Double getMinSignificance() {
        return minSignificance;
    }

    public void setMinSignificance(final Double minSignificance) {
        this.minSignificance = minSignificance;
    }

    public Double getMaxSignificance() {
        return maxSignificance;
    }

    public void setMaxSignificance(final Double maxSignificance) {
        this.maxSignificance = maxSignificance;
    }

    public Double getMinPValue() { return minPValue; }

    public void setMinPValue(final Double minPValue) { this.minPValue = minPValue; }

    public Double getDiseasePValue() {
        return diseasePValue;
    }

    public void setDiseasePValue(Double diseasePValue) {
        this.diseasePValue = diseasePValue;
    }

    public Double getSpeciesPValue() {
        return speciesPValue;
    }

    public void setSpeciesPValue(Double speciesPValue) {
        this.speciesPValue = speciesPValue;
    }

    public Double getSampleSourcePValue() {
        return sampleSourcePValue;
    }

    public void setSampleSourcePValue(Double sampleSourcePValue) {
        this.sampleSourcePValue = sampleSourcePValue;
    }

    // ****************************
    // ***** Standard methods *****
    // ****************************

    public Double getAveDiversity() {
        return aveDiversity;
    }

    public void setAveDiversity(final Double aveDiversity) {
        this.aveDiversity = aveDiversity;
    }

    public Double getMinDiversity() {
        return minDiversity;
    }

    public void setMinDiversity(final Double minDiversity) {
        this.minDiversity = minDiversity;
    }

    public Double getMaxDiversity() {
        return maxDiversity;
    }

    public void setMaxDiversity(final Double maxDiversity) {
        this.maxDiversity = maxDiversity;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpectrumCluster)) {
            return false;
        }
        return id == ((SpectrumCluster) other).id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Cluster ID = " + getId();
    }
}
