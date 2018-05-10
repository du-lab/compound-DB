package org.dulab.site.repositories;

import org.dulab.models.entities.Submission;
import org.springframework.data.repository.CrudRepository;

public interface SubmissionRepository extends CrudRepository<Submission, Long> {

    Iterable<Submission> findByUserId(long userPrincipalId);

    void deleteById(long id);

    long countByCategoryId(long submissionCategoryId);
}
