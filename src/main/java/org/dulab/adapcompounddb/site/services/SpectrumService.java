package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.dto.SpectrumTableResponse;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.validation.annotation.Validated;

@Validated
public interface SpectrumService {

    Spectrum find(long id);

    SpectrumTableResponse findSpectrumBySubmissionId(Long submissionId, Integer start, Integer length, Integer column, String orderDirection);
}
