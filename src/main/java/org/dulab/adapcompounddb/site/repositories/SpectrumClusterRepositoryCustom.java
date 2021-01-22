package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.Collection;

public interface SpectrumClusterRepositoryCustom {

    Page<SpectrumClusterView> findClusters(
            String searchStr, Iterable<BigInteger> submissionIds, Pageable pageable);
}
