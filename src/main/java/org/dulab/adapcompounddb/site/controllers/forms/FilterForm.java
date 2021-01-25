package org.dulab.adapcompounddb.site.controllers.forms;

import java.util.Map;

public class FilterForm {

    private String species;
    private String source;
    private String disease;
    private Map<Long, String> submissions;

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

    public Map<Long, String> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Map<Long, String> submissions) {
        this.submissions = submissions;
    }
}
