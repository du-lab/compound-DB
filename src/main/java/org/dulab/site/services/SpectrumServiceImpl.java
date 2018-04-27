package org.dulab.site.services;

import org.dulab.exceptions.EmptySearchResultException;
import org.dulab.models.search.CriteriaBlock;
import org.dulab.models.Hit;
import org.dulab.models.Spectrum;
import org.dulab.models.UserParameter;
import org.dulab.site.repositories.SpectrumRepository;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpectrumServiceImpl implements SpectrumService {

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
            hits = ServiceUtils.toList(spectrumRepository
                    .searchSpectra(querySpectrum, criteria, mzTolerance, numHits, scoreThreshold));
        } catch (SQLGrammarException e) {
            throw new EmptySearchResultException(e.getMessage());
        }


        if (hits.isEmpty())
            throw new EmptySearchResultException();

        return hits;
    }
}