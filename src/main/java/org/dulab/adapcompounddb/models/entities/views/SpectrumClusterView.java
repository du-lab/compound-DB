package org.dulab.adapcompounddb.models.entities.views;

import org.dulab.adapcompounddb.models.ChromatographyType;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class SpectrumClusterView {

    @Id
    private long id;

    private String name;
    private int size;
    private double score;
    private Double averageSignificance;
    private Double minimumSignificance;
    private Double maximumSignificance;

    @Enumerated(EnumType.STRING)
    private ChromatographyType chromatographyType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Double getAverageSignificance() {
        return averageSignificance;
    }

    public void setAverageSignificance(Double averageSignificance) {
        this.averageSignificance = averageSignificance;
    }

    public Double getMinimumSignificance() {
        return minimumSignificance;
    }

    public void setMinimumSignificance(Double minimumSignificance) {
        this.minimumSignificance = minimumSignificance;
    }

    public Double getMaximumSignificance() {
        return maximumSignificance;
    }

    public void setMaximumSignificance(Double maximumSignificance) {
        this.maximumSignificance = maximumSignificance;
    }

    public ChromatographyType getChromatographyType() {
        return chromatographyType;
    }

    public void setChromatographyType(ChromatographyType chromatographyType) {
        this.chromatographyType = chromatographyType;
    }
}

