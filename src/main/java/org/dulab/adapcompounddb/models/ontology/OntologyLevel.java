package org.dulab.adapcompounddb.models.ontology;


public class OntologyLevel {

    private final String label;
    private final int priority;
    private final boolean inHouseLibrary;
    private final Double mzTolerance;
    private final Double scoreThreshold;
    private final Double precursorTolerance;
    private final Double massTolerancePPM;
    private final Double retTimeTolerance;


    public OntologyLevel(String label, int priority, boolean inHouseLibrary, Double mzTolerance, Double scoreThreshold,
                         Double precursorTolerance, Double massTolerancePPM, Double retTimeTolerance) {
        this.label = label;
        this.priority = priority;
        this.inHouseLibrary = inHouseLibrary;
        this.mzTolerance = mzTolerance;
        this.scoreThreshold = scoreThreshold;
        this.massTolerancePPM = massTolerancePPM;
        this.precursorTolerance = precursorTolerance;
        this.retTimeTolerance = retTimeTolerance;
    }

    public String getLabel() {
        return label;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isInHouseLibrary() {
        return inHouseLibrary;
    }

    public Double getMzTolerance() {
        return mzTolerance;
    }

    public Double getScoreThreshold() {
        return scoreThreshold;
    }

    public Double getMassTolerancePPM() {
        return massTolerancePPM;
    }

    public Double getPrecursorTolerance() {
        return precursorTolerance;
    }

    public Double getRetTimeTolerance() {
        return retTimeTolerance;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
