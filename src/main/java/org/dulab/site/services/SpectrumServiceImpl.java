package org.dulab.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.ChromatographyType;
import org.dulab.models.DatabaseStatistics;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.Hit;
import org.dulab.models.entities.Spectrum;
import org.dulab.site.repositories.SpectrumRepository;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpectrumServiceImpl implements SpectrumService {

    private static final Logger LOGGER = LogManager.getLogger();

    private final SpectrumRepository spectrumRepository;

    @Autowired
    public SpectrumServiceImpl(SpectrumRepository spectrumRepository) {
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    @Transactional
    public Spectrum find(long id) throws EmptySearchResultException {
        return spectrumRepository.findById(id)
                .orElseThrow(EmptySearchResultException::new);
    }

    @Override
    @Transactional
    public List<Hit> match(Spectrum querySpectrum, CriteriaBlock criteria,
                           float mzTolerance, int numHits, float scoreThreshold)
            throws EmptySearchResultException {

        List<Hit> hits;
        try {
            long startTime = System.currentTimeMillis();

            hits = ServiceUtils.toList(spectrumRepository
                    .searchSpectra(querySpectrum, criteria, mzTolerance, numHits, scoreThreshold));

            long estimatedTime = System.currentTimeMillis() - startTime;
            LOGGER.info("Search of similar spectra in " + estimatedTime + " milliseconds.");

        } catch (SQLGrammarException e) {
            throw new EmptySearchResultException(e.getMessage());
        }


        if (hits.isEmpty())
            throw new EmptySearchResultException();

        return hits;
    }

    @Override
    @Transactional
    public long getNumberOfSubmittedSpectra() {
        return spectrumRepository.countByConsensusIsFalse();
    }

    @Override
    @Transactional
    public DatabaseStatistics getStatistics(ChromatographyType type) {
        DatabaseStatistics statistics = new DatabaseStatistics();
        statistics.setNumSubmittedSpectra(
                spectrumRepository.countBySubmissionChromatographyTypeAndConsensusFalse(type));
        statistics.setNumConsensusSpectra(
                spectrumRepository.countByClusterChromatographyTypeAndConsensusTrue(type));
        statistics.setNumUnmatchedSpectra(
                spectrumRepository.countUnmatchedBySubmissionChromatographyType(type));
        return statistics;
    }
}
