package org.dulab.adapcompounddb.site.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.entities.Peak;
import org.dulab.adapcompounddb.models.entities.Spectrum;
import org.dulab.adapcompounddb.models.entities.SpectrumProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class MspFileReaderService implements FileReaderService {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public List<Spectrum> read(InputStream inputStream, ChromatographyType type)
            throws IOException {

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(inputStream, "Input stream is empty")));

        List<Spectrum> spectra = new ArrayList<>();
        Spectrum spectrum = new Spectrum();
        List<Peak> peaks = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {

                if (!peaks.isEmpty()) {
                    spectrum.setChromatographyType(type);
                    spectrum.setPeaks(peaks, true);
                    spectra.add(spectrum);
                }

                spectrum = new Spectrum();
                peaks = new ArrayList<>();
            }
            else if (line.contains(":")) {
                // Add property
                for (String s : line.split(";")) {
                    String[] nameValuePair = s.split(":", 2);
                    if (nameValuePair.length == 2)
                        spectrum.addProperty(nameValuePair[0].trim(), nameValuePair[1].trim());
                }
            } else
                addPeak(spectrum, peaks, line);
        }

        if (!peaks.isEmpty()) {
            spectrum.setChromatographyType(type);
            spectrum.setPeaks(peaks, true);
            spectra.add(spectrum);
        }

        reader.close();

        return spectra;
    }

    private void addPeak(Spectrum spectrum, List<Peak> peaks, String line) {

        for (String s : line.split(";")) {
            String[] mzIntensityPair = s.split(" ");
            if (mzIntensityPair.length == 2) {
                try {
                    Peak peak = new Peak();
                    peak.setMz(Double.valueOf(mzIntensityPair[0]));
                    peak.setIntensity(Double.valueOf(mzIntensityPair[1]));
                    peak.setSpectrum(spectrum);
                    peaks.add(peak);
                }
                catch (NumberFormatException e) {
                    LOG.warn("Wrong format of mz-intensity pair: " + mzIntensityPair[0] + ", " + mzIntensityPair[1]);
                }
            }
        }
    }
}
