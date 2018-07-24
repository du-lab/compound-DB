package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

@Validated
public interface SubmissionService {

    Submission findSubmission(long submissionId);

    List<Submission> getSubmissionsByUserId(long userId);

    void saveSubmission(
            @NotNull(message = "The submission is required.")
            @Valid Submission submission);

    void deleteSubmission(Submission submission);

    void delete(long submissionId);


    List<String> findAllTags();

    List<SubmissionCategory> findAllCategories();

    List<SubmissionCategory> findAllCategories(SubmissionCategoryType type);

    long countSubmissionsByCategoryId(long submissionCategoryId);

    Optional<SubmissionCategory> findSubmissionCategory(long submissionCategoryId);

    void saveSubmissionCategory(SubmissionCategory category);

    void deleteSubmissionCategory(long submissionCategoryId);
}
