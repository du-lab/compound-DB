package org.dulab.adapcompounddb.site.repositories;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    private static final String PEAK_VALUE_SQL_STRING = "(%f, %f, %d)";
    private static final String PROPERTY_VALUE_SQL_STRING = "(%d, %s, %s)";
    private static final String SPECTRUM_VALUE_SQL_STRING = "(%s, %f, %f, %f, %s, %d)";

    public static final String DOUBLE_QUOTE = "\"";
    public static final String COMMA = ",";
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SpectrumMatch> spectrumSearch(final SearchType searchType, final Spectrum querySpectrum, final QueryParameters params) {

        final SpectrumQueryBuilder queryBuilder = new SpectrumQueryBuilder(
                searchType, querySpectrum.getChromatographyType(), params.getExcludeSpectra());

        if (params.getScoreThreshold() != null && params.getMzTolerance() != null) {
            queryBuilder.setSpectrum(querySpectrum, params.getMzTolerance(), params.getScoreThreshold());
        }

        if (querySpectrum.getPrecursor() != null && params.getPrecursorTolerance() != null) {
            queryBuilder.setPrecursorRange(querySpectrum.getPrecursor(), params.getPrecursorTolerance());
        }

        if (querySpectrum.getRetentionTime() != null && params.getRetTimeTolerance() != null) {
            queryBuilder.setRetentionTimeRange(querySpectrum.getRetentionTime(), params.getRetTimeTolerance());
        }

        queryBuilder.setTags(params.getTags());

        final String sqlQuery = queryBuilder.build();

        @SuppressWarnings("unchecked") final List<Object[]> resultList = entityManager
                .createNativeQuery(sqlQuery, "SpectrumScoreMapping")
                .getResultList();

        final List<SpectrumMatch> matches = new ArrayList<>();
        for (final Object[] objects : resultList) {
            final long matchSpectrumId = (long) objects[0];
            final double score = (double) objects[1];

            final SpectrumMatch match = new SpectrumMatch();
            match.setQuerySpectrum(querySpectrum);
            match.setMatchSpectrum(entityManager.find(Spectrum.class, matchSpectrumId));
            match.setScore(score);
            matches.add(match);
        }

        return matches;
    }

    @Override
    public void savePeaksAndPropertiesQuery(final List<Spectrum> spectrumList, final List<Long> savedSpectrumIdList) {
        final StringBuilder peakSql = new StringBuilder("INSERT INTO `Peak`(" +
                "`Mz`, `Intensity`, `SpectrumId`) VALUES ");
        final StringBuilder propertySql = new StringBuilder("INSERT INTO `SpectrumProperty`(" +
                "`SpectrumId`, `Name`, `Value`) VALUES ");

        final String peakValueString = "(%f, %f, %d)";
        final String propertyValueString = "(%d, %s, %s)";

        for (int i = 0; i < spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();
            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();

            for (int j = 0; j < peaks.size(); j++) {
                if (i != 0 || j != 0) {
                    peakSql.append(COMMA);
                }
                final Peak p = peaks.get(j);

                peakSql.append(String.format(peakValueString, p.getMz(), p.getIntensity(), savedSpectrumIdList.get(i)));
            }

            for (int j = 0; j < properties.size(); j++) {
                if (i != 0 || j != 0) {
                    propertySql.append(COMMA);
                }
                final SpectrumProperty sp = properties.get(j);
                propertySql.append(String.format(propertyValueString, savedSpectrumIdList.get(i), DOUBLE_QUOTE + sp.getName() + DOUBLE_QUOTE, DOUBLE_QUOTE + sp.getValue() + DOUBLE_QUOTE));
            }
        }

        final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
        peakQuery.executeUpdate();
        final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
        propertyQuery.executeUpdate();
    }

    @Override
    public void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList) {
        final List<Spectrum> spectrumList = new ArrayList<>();

        final StringBuilder insertSql = new StringBuilder("INSERT INTO `Spectrum`(" +
                "`Name`, `Precursor`, `RetentionTime`," +
                "`Significance`, `ChromatographyType`, `FileId`" +
                ") VALUES ");

//        final String propertyValueString = "(%s, %f, %f, %f, %s, %d)";

        for (int i = 0; i < fileList.size(); i++) {
            final List<Spectrum> spectra = fileList.get(i).getSpectra();
            spectrumList.addAll(spectra);
            for (int j = 0; j < spectra.size(); j++) {
                if (i != 0 || j != 0) {
                    insertSql.append(COMMA);
                }
                final Spectrum spectrum = spectra.get(j);

                insertSql.append(String.format(SPECTRUM_VALUE_SQL_STRING,
                        DOUBLE_QUOTE + spectrum.getName() + DOUBLE_QUOTE,
                        spectrum.getPrecursor(),
                        spectrum.getRetentionTime(),
                        spectrum.getSignificance(),
                        DOUBLE_QUOTE + spectrum.getChromatographyType().name() + DOUBLE_QUOTE,
                        savedFileIdList.get(i)
                ));
            }
        }
        final Query insertQuery = entityManager.createNativeQuery(insertSql.toString());
        insertQuery.executeUpdate();

        final List<Long> fileIds = new ArrayList<>(fileList.size());
        fileList.forEach(file -> fileIds.add(file.getId()));
        final String selectSql = "select s.id from Spectrum s where s.file.id in (:fileIds)";

        final TypedQuery<Long> selectQuery = entityManager.createQuery(selectSql, Long.class);
        selectQuery.setParameter("fileIds", fileIds);

        final List<Long> spectrumIds = selectQuery.getResultList();

        savePeaksAndPropertiesQuery(spectrumList, spectrumIds);
    }

    @Override
    public void savePeaksAndProperties(final Long spectrumId, final List<Peak> peaks, final List<SpectrumProperty> properties) {
        final StringBuilder peakSql = new StringBuilder("INSERT INTO `Peak` (`Mz`, `Intensity`, `SpectrumId`) VALUES ");
        final StringBuilder propertySql = new StringBuilder("INSERT INTO `SpectrumProperty` (`SpectrumId`, `Name`, `Value`) VALUES ");

//        final String peakValueString = "(%f, %f, %d)";
//        final String propertyValueString = "(%d, %s, %s)";

        for (int j = 0; j < peaks.size(); j++) {
            if (j != 0) {
                peakSql.append(COMMA);
            }
            final Peak p = peaks.get(j);

            peakSql.append(String.format(PEAK_VALUE_SQL_STRING, p.getMz(), p.getIntensity(), spectrumId));
        }

        for (int j = 0; j < properties.size(); j++) {
            if (j != 0) {
                propertySql.append(COMMA);
            }
            final SpectrumProperty sp = properties.get(j);
            propertySql.append(String.format(PROPERTY_VALUE_SQL_STRING, spectrumId, DOUBLE_QUOTE + sp.getName() + DOUBLE_QUOTE, DOUBLE_QUOTE + sp.getValue() + DOUBLE_QUOTE));
        }

        final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
        peakQuery.executeUpdate();
        final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
        propertyQuery.executeUpdate();
    }

    @Override
    public void updateSpectraInCluster(final long clusterId, final Set<Long> spectrumIds) {

        StringBuilder sqlBuilder = new StringBuilder("UPDATE Spectrum ");
        sqlBuilder.append(String.format("SET ClusterId = %d WHERE Id IN (", clusterId));
        sqlBuilder.append(spectrumIds
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")));
        sqlBuilder.append(")");

        String sqlQuery = sqlBuilder.toString();

        Query nativeQuery = entityManager.createNativeQuery(sqlQuery);
        nativeQuery.executeUpdate();
    }
}
