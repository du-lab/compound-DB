package org.dulab.adapcompounddb.site.repositories;

import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SpectrumRepository extends CrudRepository<Spectrum, Long>, SpectrumRepositoryCustom {

	@Query("SELECT s FROM Spectrum s WHERE s.matches IS EMPTY")
	Iterable<Spectrum> findAllByMatchesIsEmpty();

    @Query("SELECT s FROM Spectrum s JOIN FETCH s.peaks WHERE s.matches IS EMPTY " +
            "AND s.consensus=FALSE AND s.reference=FALSE AND s.chromatographyType = ?1")
    Iterable<Spectrum> findUnmatchedByChromatographyType(ChromatographyType chromatographyType);

	@Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY AND s.consensus=FALSE AND s.reference=FALSE")
	long countUnmatched();

	Iterable<Spectrum> findAllByConsensusFalseAndReferenceFalseAndChromatographyType(
			ChromatographyType chromatographyType);

	long countByConsensusIsFalse();

	long countByChromatographyTypeAndConsensusFalse(ChromatographyType chromatographyType);

	long countByChromatographyTypeAndConsensusTrue(ChromatographyType chromatographyType);

	@Query(value="select s from Spectrum s "
			+ "where s.file.submission.id = ?1 "
			+ "and (s.name like %?2% or s.file.name like %?2%)")
	Page<Spectrum> findSpectrumBySubmissionId(Long submissionId, String searchStr, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Spectrum s WHERE s.matches IS EMPTY " +
            "AND s.consensus=FALSE AND s.chromatographyType = ?1")
    long countUnmatchedBySubmissionChromatographyType(ChromatographyType chromatographyType);

    long countByConsensusTrue();

    long countByReferenceTrue();
}
