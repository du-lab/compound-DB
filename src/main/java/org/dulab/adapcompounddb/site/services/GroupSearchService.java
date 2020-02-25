package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.entities.Submission;

import javax.servlet.http.HttpSession;

public interface GroupSearchService {

    void groupSearch(Submission submission, HttpSession session, String species, String source, String disease);

    /**
     * Calculates the fraction of processed query spectra
     *
     * @return an integer between 0 and 100
     */
    float getProgress();

    void setProgress(float progress);
}
