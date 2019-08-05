package org.dulab.adapcompounddb.site.services;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.dto.GroupSearchDTO;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumMatch;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.controllers.ControllerUtils;
import org.dulab.adapcompounddb.site.repositories.SpectrumRepository;
import org.dulab.adapcompounddb.site.repositories.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

@Service
public class GroupSearchServiceImpl implements GroupSearchService {

    //TODO: I think three global variables (progress, progressStep, and fullSteps) are too many to calculate the progress
    //TODO: move this definition to the top. It's very hard to find it here
    private float progress = -1F;
    private final SubmissionRepository submissionRepository;
    private final SpectrumRepository spectrumRepository;

    @Autowired
    public GroupSearchServiceImpl(SubmissionRepository submissionRepository, SpectrumRepository spectrumRepository) {
        this.submissionRepository = submissionRepository;
        this.spectrumRepository = spectrumRepository;
    }

    @Override
    public float getProgress() {
        return progress;
    }

    @Override
    public void setProgress(final float progress) {
        this.progress = progress;
    }

    @Override
    @Transactional
    public void groupSearch(long submissionId, HttpSession session, QueryParameters parameters) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(EmptyStackException::new);
        setSession(submission, parameters, session);
    }

    @Override
    @Transactional
    public void nonSubmittedGroupSearch(Submission submission, HttpSession session, QueryParameters parameters) {
        setSession(submission, parameters, session);
    }

    private void setSession(Submission submission, QueryParameters parameters, HttpSession session) {

        long fullSteps = 0l;
        float progressStep = 0F;
        progress = 0F;
        // Calculate total number of submissions

        for (File f : submission.getFiles()) {
            List<Spectrum> querySpectra = f.getSpectra();
            for (Spectrum s : querySpectra) {
                fullSteps++;
            }
        }

        final List<GroupSearchDTO> groupSearchDTOList = new ArrayList<>();

        for (int fileIndex = 0; fileIndex < submission.getFiles().size(); fileIndex++) {

            List<Spectrum> querySpectra = submission.getFiles().get(fileIndex).getSpectra();

            for (int i = 0; i < querySpectra.size(); i++) {

                long querySpectrumId = querySpectra.get(i).getId();
                final List<SpectrumMatch> matches = spectrumRepository.spectrumSearch(
                        SearchType.SIMILARITY_SEARCH, querySpectra.get(i), parameters);

                // get the best match if the match is not null
                if (matches.size() > 0) {
                    groupSearchDTOList.add(saveDTO(matches.get(0), fileIndex, i, querySpectrumId));
                } else {
                    SpectrumMatch noneMatch = new SpectrumMatch();
                    noneMatch.setQuerySpectrum(querySpectra.get(i));
                    groupSearchDTOList.add(saveDTO(noneMatch, fileIndex, i, querySpectrumId));
                }
                session.setAttribute(ControllerUtils.GROUP_SEARCH_RESULTS_ATTRIBUTE_NAME, groupSearchDTOList);
                progress = progressStep / fullSteps;
                progressStep = progressStep + 1F;
                //TODO: What's the purpose of count?
            }
        }
        progress = -1F;
    }

    private GroupSearchDTO saveDTO(SpectrumMatch spectrumMatch, int fileIndex, int spectrumIndex, long querySpectrumId) {
        GroupSearchDTO groupSearchDTO = new GroupSearchDTO();
        if (spectrumMatch.getMatchSpectrum() != null) {
            if (spectrumMatch.getMatchSpectrum().getCluster().getMinPValue() != null) {
                double pValue = spectrumMatch.getMatchSpectrum().getCluster().getMinPValue();
                groupSearchDTO.setMinPValue(pValue);
            }
            if (spectrumMatch.getMatchSpectrum().getCluster().getMaxDiversity() != null) {
                double maxDiversity = spectrumMatch.getMatchSpectrum().getCluster().getMaxDiversity();
                groupSearchDTO.setMaxDiversity(maxDiversity);
            }
            long matchSpectrumClusterId = spectrumMatch.getMatchSpectrum().getCluster().getId();
            double score = spectrumMatch.getScore();
            String matchSpectrumName = spectrumMatch.getMatchSpectrum().getName();
            groupSearchDTO.setMatchSpectrumClusterId(matchSpectrumClusterId);
            groupSearchDTO.setMatchSpectrumName(matchSpectrumName);
            groupSearchDTO.setScore(score);
        } else {
            groupSearchDTO.setScore(null);
        }
        long id = spectrumMatch.getId();
        String querySpectrumName = spectrumMatch.getQuerySpectrum().getName();
        groupSearchDTO.setFileIndex(fileIndex);
        groupSearchDTO.setId(id);
        groupSearchDTO.setQuerySpectrumName(querySpectrumName);
        groupSearchDTO.setSpectrumIndex(spectrumIndex);
        groupSearchDTO.setQuerySpectrumId(querySpectrumId);
        return groupSearchDTO;
    }
}
