package org.dulab.adapcompounddb.site.repositories;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.*;

import org.dulab.adapcompounddb.models.QueryParameters;
import org.dulab.adapcompounddb.models.SearchType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.models.entities.views.SpectrumClusterView;
import org.dulab.adapcompounddb.models.entities.views.MassSearchResult;

public class SpectrumRepositoryImpl implements SpectrumRepositoryCustom {

    private static final String PEAK_INSERT_SQL_STRING = "INSERT INTO `Peak`(`Mz`, `Intensity`, `SpectrumId`) VALUES ";
    private static final String PROPERTY_INSERT_SQL_STRING = "INSERT INTO `SpectrumProperty`(`SpectrumId`, `Name`, `Value`) VALUES ";
    private static final String PEAK_VALUE_SQL_STRING = "(%f,%f,%d)";
    private static final String PROPERTY_VALUE_SQL_STRING = "(%d, %s, %s)";
    private static final String SPECTRUM_VALUE_SQL_STRING = "(%s, %f, %f, %f, %d, %b, %b, %b, %s, %d, %f)";

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


        @SuppressWarnings("unchecked") final List<Object[]> resultList = entityManager  // .getEntityManagerFactory().createEntityManager()
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
    public Iterable<SpectrumClusterView> searchLibrarySpectra(Iterable<BigInteger> submissionIds, Spectrum querySpectrum,
                                                              double scoreThreshold, double mzTolerance,
                                                              Double precursor, Double precursorTolerance) {

//        String query = "SELECT ConsensusSpectrum.Id, SpectrumCluster.Id AS ClusterId, ConsensusSpectrum.Name, COUNT(DISTINCT File.SubmissionId) AS Size, Score, ";
//        query += "AVG(Spectrum.Significance) AS AverageSignificance, MIN(Spectrum.Significance) AS MinimumSignificance, ";
//        query += "MAX(Spectrum.Significance) AS MaximumSignificance, ConsensusSpectrum.ChromatographyType FROM (\n";
//        query += "SELECT ClusterId, POWER(SUM(Product), 2) AS Score FROM (\n";
//        query += querySpectrum.getPeaks().stream()
//                .map(p -> String.format("\tSELECT ClusterId, SQRT(Intensity * %f) AS Product " +
//                                "FROM Peak INNER JOIN Spectrum ON Peak.SpectrumId = Spectrum.Id " +  //
//                                "WHERE Spectrum.Consensus IS TRUE AND Peak.Mz > %f AND Peak.Mz < %f\n",
//                        p.getIntensity(), p.getMz() - mzTolerance, p.getMz() + mzTolerance))
//                .collect(Collectors.joining("\tUNION ALL\n"));
//        query += ") AS SearchTable ";
//        query += "GROUP BY ClusterId HAVING Score > :scoreThreshold\n";
//        query += ") AS ScoreTable JOIN SpectrumCluster ON SpectrumCluster.Id = ClusterId\n";
//        query += "JOIN Spectrum AS ConsensusSpectrum ON ConsensusSpectrum.Id = SpectrumCluster.ConsensusSpectrumId\n";
//        query += "JOIN Spectrum ON Spectrum.ClusterId = SpectrumCluster.Id\n";
//        query += "JOIN File ON File.Id = Spectrum.FileId\n";
//        query += "WHERE File.SubmissionId IN (:submissionIds)\n";
//        query += "GROUP BY Spectrum.ClusterId ORDER BY Score DESC";

        List<BigInteger> submissionIdList = new ArrayList<>();
        submissionIds.forEach(submissionIdList::add);

        String query = new SpectrumQueryBuilderAlt(submissionIdList,
                querySpectrum.getChromatographyType(), true, true)
                .withQuerySpectrum(querySpectrum, mzTolerance, scoreThreshold)
                .withPrecursor(precursor, precursorTolerance)
                .build();

        @SuppressWarnings("unchecked")
        List<SpectrumClusterView> resultList = entityManager
                .createNativeQuery(query, SpectrumClusterView.class)
//                .setParameter("scoreThreshold", scoreThreshold)
//                .setParameter("submissionIds", submissionIds)
                .getResultList();

        return resultList;
    }

    @Override
    public Iterable<MassSearchResult> searchLibraryMasses(Spectrum querySpectrum, double tolerance, String species, String source, String disease) {

        Double queryWeight = querySpectrum.getMolecularWeight();
        if (queryWeight == null) return new ArrayList<>(0);

        String query = String.format(
                "SELECT Id, Name, MolecularWeight, ABS(MolecularWeight - %f) AS Error, ChromatographyType " +
                        "FROM Spectrum WHERE (Consensus IS TRUE OR Reference IS True) AND " +
                        "ChromatographyType = '%s' AND MolecularWeight > %f AND MolecularWeight < %f " +
                        "ORDER BY Error ASC",
                queryWeight,
                querySpectrum.getChromatographyType(),
                queryWeight - tolerance,
                queryWeight + tolerance);

        @SuppressWarnings("unchecked")
        List<MassSearchResult> resultList = entityManager
                .createNativeQuery(query, MassSearchResult.class)
                .getResultList();

        return resultList;
    }

    @Override
    public void savePeaksAndPropertiesQuery(final List<Spectrum> spectrumList, final List<Long> savedSpectrumIdList) {
        final StringBuilder peakSql = new StringBuilder(PEAK_INSERT_SQL_STRING);
        final StringBuilder propertySql = new StringBuilder(PROPERTY_INSERT_SQL_STRING);

        for (int i = 0; i < spectrumList.size(); i++) {
            final List<Peak> peaks = spectrumList.get(i).getPeaks();
            if (peaks != null) {
                for (int j = 0; j < peaks.size(); j++) {
                    if (i != 0 || j != 0) {
                        peakSql.append(COMMA);
                    }
                    final Peak peak = peaks.get(j);
                    peakSql.append(String.format("(%f, %f, %d)", peak.getMz(), peak.getIntensity(), savedSpectrumIdList.get(i)));
                }
            }

            final List<SpectrumProperty> properties = spectrumList.get(i).getProperties();
            if (properties != null) {
                for (int j = 0; j < properties.size(); j++) {
                    if (i != 0 || j != 0) {
                        propertySql.append(COMMA);
                    }
                    final SpectrumProperty property = properties.get(j);
                    propertySql.append(String.format("(%d, \"%s\", \"%s\")",
                            savedSpectrumIdList.get(i),
                            property.getName().replace("\"", "\"\""),
                            property.getValue().replace("\"", "\"\"")));
                }
            }
        }

        if (!peakSql.toString().equals(PEAK_INSERT_SQL_STRING)) {
            final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
            peakQuery.executeUpdate();
        }
        if (!propertySql.toString().equals(PROPERTY_INSERT_SQL_STRING)) {
            final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
            propertyQuery.executeUpdate();
        }
    }

    @Override
    public void saveSpectrumAndPeaks(final List<File> fileList, final List<Long> savedFileIdList) {
        final List<Spectrum> spectrumList = new ArrayList<>();

        final StringBuilder insertSql = new StringBuilder("INSERT INTO `Spectrum`(" +
                "`Name`, `Precursor`, `RetentionTime`, `Significance`, " +
                "`ClusterId`, `Consensus`, `Reference`, `IntegerMz`, " +
                "`ChromatographyType`, `FileId`, `MolecularWeight`" +
                ") VALUES ");

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
                        spectrum.getCluster() != null ? spectrum.getCluster().getId() : null,
                        spectrum.isConsensus(),
                        spectrum.isReference(),
                        spectrum.isIntegerMz(),
                        DOUBLE_QUOTE + spectrum.getChromatographyType().name() + DOUBLE_QUOTE,
                        savedFileIdList.get(i),
                        spectrum.getMolecularWeight()
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
        final StringBuilder peakSql = new StringBuilder("INSERT INTO `Peak` (`Mz`,`Intensity`,`SpectrumId`) VALUES ");
        final StringBuilder propertySql = new StringBuilder("INSERT INTO `SpectrumProperty` (`SpectrumId`,`Name`,`Value`) VALUES ");

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

        entityManager.flush();
        entityManager.clear();
        final Query peakQuery = entityManager.createNativeQuery(peakSql.toString());
        peakQuery.executeUpdate();
        final Query propertyQuery = entityManager.createNativeQuery(propertySql.toString());
        propertyQuery.executeUpdate();
    }
}
