package org.dulab.adapcompounddb.site.services.search;

public class SearchParameters {

    private Double scoreThreshold;
    private Double mzTolerance;
    private Double precursorTolerance;
    private Double molecularWeightTolerance;
    private String species;
    private String source;
    private String disease;

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public void setScoreThreshold(Double scoreThreshold) {
        this.scoreThreshold = scoreThreshold;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public void setMzTolerance(Double mzTolerance) {
        this.mzTolerance = mzTolerance;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public void setPrecursorTolerance(Double precursorTolerance) {
        this.precursorTolerance = precursorTolerance;
    }

    public Double getMolecularWeightTolerance() {
        return molecularWeightTolerance;
    }

    public void setMolecularWeightTolerance(Double molecularWeightTolerance) {
        this.molecularWeightTolerance = molecularWeightTolerance;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}
