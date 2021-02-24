package org.dulab.adapcompounddb.models.dto;

import org.dulab.adapcompounddb.models.MatchType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.views.MassSearchResult;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.models.enums.OntologyLevel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class SearchResultDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Map<Integer, Function<SearchResultDTO, Comparable>> COLUMN_TO_FIELD_MAP = new HashMap<>();

    static {
        COLUMN_TO_FIELD_MAP.put(1, SearchResultDTO::getQuerySpectrumName);
        COLUMN_TO_FIELD_MAP.put(2, SearchResultDTO::getName);
        COLUMN_TO_FIELD_MAP.put(3, SearchResultDTO::getSize);
        COLUMN_TO_FIELD_MAP.put(4, SearchResultDTO::getScore);
        COLUMN_TO_FIELD_MAP.put(5, SearchResultDTO::getAveSignificance);
        COLUMN_TO_FIELD_MAP.put(6, SearchResultDTO::getMinSignificance);
        COLUMN_TO_FIELD_MAP.put(7, SearchResultDTO::getMaxSignificance);
        COLUMN_TO_FIELD_MAP.put(8, SearchResultDTO::getChromatographyTypeLabel);
    }

    // *************************
    // ***** Entity fields *****
    // *************************

    // Query
    private Long querySpectrumId;
    private Integer querySpectrumIndex;
    private Integer queryFileIndex;
    private String querySpectrumName;

    // Match
    private MatchType matchType;
    private long id;
    private String name;
    private Integer size;
    private Double aveSignificance;
    private Double minSignificance;
    private Double maxSignificance;
    private OntologyLevel ontologyLevel;
    private String chromatographyTypeLabel;
    private String chromatographyTypePath;
    private String json;
    private Double molecularWeight;

    // Other
    private int position;
    private Double score;
    private Double massError;
    private Double retTimeError;


    public SearchResultDTO() {}

    public SearchResultDTO(Spectrum querySpectrum, SpectrumClusterView view) {
        this(querySpectrum);

        matchType = MatchType.CLUSTER;

        if (view != null) {
            boolean isReference = view.getClusterId() == null;
            this.matchType = (isReference) ? MatchType.SPECTRUM : MatchType.CLUSTER;
            this.id = (isReference) ? view.getId() : view.getClusterId();
            this.name = (isReference ? "[Ref Spec] " : "[Con Spec] ") + view.getName();
            this.size = view.getSize();
            this.score = view.getScore();
            this.massError = view.getMassError();
            this.retTimeError = view.getRetTimeError();
            this.aveSignificance = view.getAverageSignificance();
            this.minSignificance = view.getMinimumSignificance();
            this.maxSignificance = view.getMaximumSignificance();
            this.chromatographyTypeLabel = view.getChromatographyType().getLabel();
            this.chromatographyTypePath = view.getChromatographyType().getIconPath();
        }
    }

    public SearchResultDTO(SpectrumClusterView view) {
        this(null, view);
    }

    public SearchResultDTO(Spectrum querySpectrum) {
        if (querySpectrum != null) {
            this.querySpectrumId = querySpectrum.getId();
            this.querySpectrumName = querySpectrum.getName();
        }
    }

    public SearchResultDTO(Spectrum querySpectrum, MassSearchResult massSearchResult) {
        this(querySpectrum);

        matchType = MatchType.SPECTRUM;

        if (massSearchResult != null) {
            this.id = massSearchResult.getId();
            this.name = massSearchResult.getName();
            this.chromatographyTypeLabel = massSearchResult.getChromatographyType().getLabel();
            this.chromatographyTypePath = massSearchResult.getChromatographyType().getIconPath();
            this.molecularWeight = massSearchResult.getMolecularWeight();
            this.massError = massSearchResult.getError();
        }
    }

    // *******************************
    // ***** Getters and setters *****
    // *******************************

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SearchResultDTO)) {
            return false;
        }
        return id == ((SearchResultDTO) other).id;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(final Integer size) {
        this.size = size;
    }

    public Double getScore() {
        return score;
    }

    public Long getNISTScore() {
        return (score != null) ? Math.round(score * 1000) : null;
    }

    public void setScore(final Double score) {
        this.score = score;
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

    public OntologyLevel getOntologyLevel() {
        return ontologyLevel;
    }

    public void setOntologyLevel(OntologyLevel ontologyLevel) {
        this.ontologyLevel = ontologyLevel;
    }

    public String getQuerySpectrumName() {
        return querySpectrumName;
    }

    public void setQuerySpectrumName(String querySpectrumName) {
        this.querySpectrumName = querySpectrumName;
    }

    public Long getQuerySpectrumId() {
        return querySpectrumId;
    }

    public void setQuerySpectrumId(long querySpectrumId) {
        this.querySpectrumId = querySpectrumId;
    }

    public Integer getQuerySpectrumIndex() {
        return querySpectrumIndex;
    }

    public void setQuerySpectrumIndex(Integer querySpectrumIndex) {
        this.querySpectrumIndex = querySpectrumIndex;
    }

    public Integer getQueryFileIndex() {
        return queryFileIndex;
    }

    public void setQueryFileIndex(Integer queryFileIndex) {
        this.queryFileIndex = queryFileIndex;
    }

    public String getChromatographyTypeLabel() {
        return chromatographyTypeLabel;
    }

    public void setChromatographyTypeLabel(String chromatographyTypeLabel) {
        this.chromatographyTypeLabel = chromatographyTypeLabel;
    }

    public String getChromatographyTypePath() {
        return chromatographyTypePath;
    }

    public void setChromatographyTypePath(String chromatographyTypePath) {
        this.chromatographyTypePath = chromatographyTypePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public MatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public Double getMolecularWeight() {
        return molecularWeight;
    }

    public void setMolecularWeight(Double molecularWeight) {
        this.molecularWeight = molecularWeight;
    }

    public Double getMassError() {
        return massError;
    }

    public void setMassError(Double massError) {
        this.massError = massError;
    }

    public Double getRetTimeError() {
        return retTimeError;
    }

    public void setRetTimeError(Double retTimeError) {
        this.retTimeError = retTimeError;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return "Search Result ID = " + getId();
    }
}
