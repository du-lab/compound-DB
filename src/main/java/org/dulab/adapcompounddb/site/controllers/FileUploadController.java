package org.dulab.adapcompounddb.site.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.ChromatographyType;
import org.dulab.adapcompounddb.models.FileType;
import org.dulab.adapcompounddb.models.entities.File;
import org.dulab.adapcompounddb.models.entities.Submission;
import org.dulab.adapcompounddb.site.services.FileReaderService;
import org.dulab.adapcompounddb.site.services.MspFileReaderService;
import org.dulab.adapcompounddb.validation.ContainsFiles;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileUploadController extends BaseController {

    private static final Logger LOG = LogManager.getLogger();

    private final Map<FileType, FileReaderService> fileReaderServiceMap;

    public FileUploadController() {
        fileReaderServiceMap = new HashMap<>();
        fileReaderServiceMap.put(FileType.MSP, new MspFileReaderService());
    }

    @ModelAttribute
    public void addAttributes(final Model model) {
        model.addAttribute("chromatographyTypeList", ChromatographyType.values());
        model.addAttribute("fileTypeList", FileType.values());
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.GET)
    public String upload(final Model model, final HttpSession session) {
        if (getSubmissionFromSession(session) != null) {
            return "redirect:/file/";
        }

        final FileUploadForm form = new FileUploadForm();
        form.setFileType(FileType.MSP);
        model.addAttribute("fileUploadForm", form);
        return "file/upload";
    }

    @RequestMapping(value = "/file/upload/", method = RequestMethod.POST, consumes = "multipart/form-data")
    public String upload(final Model model, final HttpSession session, @Valid final FileUploadForm form,
            final Errors errors) {

        if (getSubmissionFromSession(session) != null) {
            return "redirect:/file/";
        }

        if (errors.hasErrors()) {
            return "file/upload";
        }

        //        MultipartFile file = form.getFile();

        //        submission.setName(file.getOriginalFilename());
        //        submission.setFilename(file.getOriginalFilename());
        //        submission.setFileType(form.getFileType());
        //        submission.setChromatographyType(form.getChromatographyType());

        final FileReaderService service = fileReaderServiceMap.get(form.fileType);
        if (service == null) {
            LOG.warn("Cannot find an implementation of FileReaderService for a file of type {}", form.getFileType());
            model.addAttribute("message", "Cannot read this file type.");
            return "file/upload";
        }

        final Submission submission = new Submission();

        final List<File> files = new ArrayList<>(form.getFiles().size());
        for (final MultipartFile multipartFile : form.getFiles()) {
            final File file = new File();
            file.setName(multipartFile.getOriginalFilename());
            file.setFileType(form.getFileType());
            file.setSubmission(submission);
            try {
                file.setContent(multipartFile.getBytes());
                file.setSpectra(service.read(multipartFile.getInputStream(), form.getChromatographyType()));
                file.getSpectra().forEach(s -> s.setFile(file));
                files.add(file);

            } catch (final IOException e) {
                LOG.warn(e);
                model.addAttribute("message", "Cannot read this file: " + e.getMessage());
                return "file/upload";
            }
        }

        if (files.isEmpty()) {
            model.addAttribute("message", "Cannot read this file");
            return "file/upload";
        }

        submission.setFiles(files);

        assign(session, submission);
        return "redirect:/file/";
    }

    private static class FileUploadForm {

        @NotNull(message = "Chromatography type must be selected.")
        private ChromatographyType chromatographyType;

        @NotNull(message = "File format must be chosen.")
        private FileType fileType;

        @ContainsFiles
        private List<MultipartFile> files;

        public ChromatographyType getChromatographyType() {
            return chromatographyType;
        }

        public void setChromatographyType(final ChromatographyType chromatographyType) {
            this.chromatographyType = chromatographyType;
        }

        public FileType getFileType() {
            return fileType;
        }

        public void setFileType(final FileType fileType) {
            this.fileType = fileType;
        }

        public List<MultipartFile> getFiles() {
            return files;
        }

        public void setFiles(final List<MultipartFile> files) {
            this.files = files;
        }
    }
}
