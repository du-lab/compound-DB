package org.dulab.site.submission;

import org.dulab.site.models.Submission;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Validated
public interface SubmissionService {

    void saveSubmission(
            @NotNull(message = "The submission is required.")
            @Valid Submission submission);
}
