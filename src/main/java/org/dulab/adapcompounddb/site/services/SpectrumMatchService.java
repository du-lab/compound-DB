package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.exceptions.EmptySearchResultException;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;

import java.util.List;

public interface SpectrumMatchService {

    void fillSpectrumMatchTable(float mzTolerance, float scoreThreshold);

    void cluster(float mzTolerance, int minNumSpectra, float scoreThreshold) throws EmptySearchResultException;

    long getTotalNumberOfClusters();

    List<SpectrumCluster> getAllClusters();

    SpectrumCluster getCluster(long id);
}