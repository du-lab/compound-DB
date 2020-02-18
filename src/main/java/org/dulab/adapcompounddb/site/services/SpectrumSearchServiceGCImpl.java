package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.ClusterDTO;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumCluster;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumClusterRepository;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.services.utils.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpectrumSearchServiceGCImpl implements SpectrumSearchService {

    private final SpectrumRepository spectrumRepository;
    private final SpectrumClusterRepository spectrumClusterRepository;

    @Autowired
    public SpectrumSearchServiceGCImpl(SpectrumRepository spectrumRepository,
                                       SpectrumClusterRepository spectrumClusterRepository) {
        this.spectrumRepository = spectrumRepository;
        this.spectrumClusterRepository = spectrumClusterRepository;
    }

    @Override
    @Transactional
    public List<SpectrumMatch> search(Spectrum querySpectrum, QueryParameters parameters) {
        return spectrumRepository.spectrumSearch(SearchType.SIMILARITY_SEARCH, querySpectrum, parameters);
    }

    @Override
    @Transactional
    public List<ClusterDTO> searchConsensusSpectra(Spectrum querySpectrum, double scoreThreshold, double mzTolerance,
                                                   String species, String source, String disease) {

        List<ClusterDTO> clusters = new ArrayList<>();
        for (SpectrumClusterView view : spectrumRepository.searchConsensusSpectra(
                querySpectrum, scoreThreshold, mzTolerance, species, source, disease)) {

            ClusterDTO cluster = new ClusterDTO();
            cluster.setClusterId(view.getId());
            cluster.setConsensusSpectrumName(view.getName());
            cluster.setSize(view.getSize());
            cluster.setScore(view.getScore());
            cluster.setAveSignificance(view.getAverageSignificance());
            cluster.setMinSignificance(view.getMinimumSignificance());
            cluster.setMaxSignificance(view.getMaximumSignificance());
            cluster.setChromatographyTypeLabel(view.getChromatographyType().getLabel());
            cluster.setChromatographyTypePath(view.getChromatographyType().getIconPath());

            spectrumClusterRepository.findById(view.getId())
                    .ifPresent(c -> cluster.setJson(ControllerUtils
                            .spectrumToJson(c.getConsensusSpectrum())
                            .toString()));

            clusters.add(cluster);
        }
        return clusters;
    }
}
