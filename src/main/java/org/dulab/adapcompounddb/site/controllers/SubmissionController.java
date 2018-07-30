package org.dulab.adapcompounddb.site.controllers;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.dulab.adapcompounddb.models.SampleSourceType;
import org.dulab.adapcompounddb.models.SubmissionCategoryType;
import org.dulab.adapcompounddb.models.entities.*;
import org.dulab.adapcompounddb.site.services.SpectrumService;
import org.dulab.adapcompounddb.site.services.SubmissionService;
import org.dulab.adapcompounddb.validation.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("submissionCategoryTypes")
public class SubmissionController extends BaseController {

    private final SubmissionService submissionService;

    @Autowired
    public SubmissionController(SubmissionService submissionService,
                                SpectrumService spectrumService) {

        this.submissionService = submissionService;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("sampleSourceTypeList", SampleSourceType.values());
        model.addAttribute("submissionCategoryTypes", SubmissionCategoryType.values());
    }

    /********************************
     ***** View File / Submission *****
     ********************************/

    @RequestMapping(value = "/file/", method = RequestMethod.GET)
    public String fileView(HttpSession session, Model model) {

        Submission submission = Submission.from(session);
        if (submission == null)
            return redirectFileUpload();

        return view(Submission.from(session), model, false);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/edit", method = RequestMethod.GET)
    public String editSubmission(@PathVariable("submissionId") long submissionId, Model model) {

        Submission submission = submissionService.findSubmission(submissionId);

        if (submission == null) {
            return submissionNotFound(model, submissionId);
        }

        return view(submission, model, true);
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/", method = RequestMethod.GET)
    public String viewSubmission(@PathVariable("submissionId") long submissionId, Model model) {

        Submission submission = submissionService.findSubmission(submissionId);

        if (submission == null) {
            return submissionNotFound(model, submissionId);
        }

        return view(submission, model, false);
    }

    private String view(Submission submission, Model model, boolean edit) {

        boolean authorized = submission.isAuthorized(getCurrentUserPrincipal());
        if(!authorized && edit) {
        	return "redirect:/error?errorMsg=" + ACCESS_DENIED_MESSAGE;
        }
        
        model.addAttribute("submission", submission);

        SubmissionForm form = new SubmissionForm();
        form.setAvailableTags(submissionService.findAllTags());
        form.setCategoryMap(submissionService.findAllCategories());

        form.setName(submission.getName());
        form.setDescription(submission.getDescription());

        if (submission.getTags() != null)
            form.setTags(submission
                    .getTags()
                    .stream()
                    .map(SubmissionTag::getId)
                    .map(SubmissionTagId::getName)
                    .collect(Collectors.joining(",")));

        if (submission.getCategories() != null)
            form.setSubmissionCategoryIds(submission
                    .getCategories()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(SubmissionCategory::getId)
                    .collect(Collectors.toList()));

        model.addAttribute("submissionForm", form);
        model.addAttribute("edit", edit);
		model.addAttribute("authorized", authorized);
		model.addAttribute("authenticated", isAuthenticated());

		if(submission.getId() == 0) {
			return "file/view";
		} else {
			return "submission/view";
		}
    }

    /**********************
     ***** File Clear *****
     **********************/

    @RequestMapping(value = "/file/clear/", method = RequestMethod.GET)
    public String clear(HttpSession session) {
        Submission.clear(session);
        return "redirect:/file/upload/";
    }

    /************************************
     ***** File / Submission Raw View *****
     ************************************/
    @RequestMapping(value = "/file/{fileIndex:\\d+}/view/", method = RequestMethod.GET)
    public String fileRawView(@PathVariable("fileIndex") int fileIndex,
                              HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.from(session);

        if (submission == null) {
            return redirectFileUpload();
        }

        rawView(response, submission.getFiles().get(fileIndex));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/view/", method = RequestMethod.GET)
    public String rawView(@PathVariable("submissionId") long id,
                          @PathVariable("fileIndex") int fileIndex,
                          HttpServletResponse response, Model model) throws IOException {

        Submission submission = submissionService.findSubmission(id);

        if (submission == null) {
            return submissionNotFound(model, id);
        }

        rawView(response, submission.getFiles().get(fileIndex));
        return null;
    }

    private void rawView(HttpServletResponse response, File file) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        response.getOutputStream().write(file.getContent());
    }

    /****************************************
     ***** File / Submission Raw Download *****
     ****************************************/

    @RequestMapping(value = "/file/{fileIndex:\\d+}/download/", method = RequestMethod.GET)
    public String fileRawDownload(@PathVariable("fileIndex") int fileIndex,
                                  HttpSession session,
                                  HttpServletResponse response) throws IOException {

        Submission submission = Submission.from(session);
        if (submission == null)
            return redirectFileUpload();

        rawDownload(response, submission.getFiles().get(fileIndex));
        return null;
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/{fileIndex:\\d+}/download/", method = RequestMethod.GET)
    public String submissionRawDownload(@PathVariable("submissionId") long id,
                                        @PathVariable("fileIndex") int fileIndex,
                                        HttpServletResponse response, Model model)
            throws IOException {

        Submission submission = submissionService.findSubmission(id);
        if (submission == null)
            return submissionNotFound(model, id);

        rawDownload(response, submission.getFiles().get(fileIndex));
        return null;
    }

    private void rawDownload(HttpServletResponse response, File file) throws IOException {
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.getOutputStream().write(file.getContent());
    }

    /**********************************
     ***** File / Submission Submit *****
     **********************************/

    @RequestMapping(value = "/file/submit/", method = RequestMethod.POST)
    public String fileView(HttpSession session, Model model, @Valid SubmissionForm form, Errors errors) {

        if (errors.hasErrors()) {
            return "file/view";
        }

        Submission submission = Submission.from(session);
        if (submission == null)
            return redirectFileUpload();

        submission.setUser(getCurrentUserPrincipal());

        String response = submit(submission, model, form);
        Submission.clear(session);
        return response;
    }

	@RequestMapping(value = "/submission/{submissionId:\\d+}/edit", method = RequestMethod.POST)
//    @RequestMapping(value = "/submission/{submissionId:\\d+}/", method = RequestMethod.POST)
    public String submissionView(@PathVariable("submissionId") long submissionId, Model model, HttpSession session,
                                 @Valid SubmissionForm form, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "file/view";
        }

        Submission submission = submissionService.findSubmission(submissionId);
        if (submission == null)
            return submissionNotFound(model, submissionId);

        return submit(submission, model, form);
    }

    private String submit(Submission submission, Model model, SubmissionForm form) {

        form.setCategoryMap(submissionService.findAllCategories());

        submission.setName(form.getName());
        submission.setDescription(form.getDescription());
        submission.setDateTime(new Date());

        List<SubmissionTag> tags = new ArrayList<>();
        for (String name : form.getTags().split(",")) {
            SubmissionTag submissionTag = new SubmissionTag();
            submissionTag.setId(new SubmissionTagId(submission, name.toLowerCase()));
            tags.add(submissionTag);
        }
        submission.setTags(tags);

        List<SubmissionCategory> categories = new ArrayList<>();
        for (long id : form.getSubmissionCategoryIds())
            if (id > 0)
                categories.add(submissionService
                        .findSubmissionCategory(id)
                        .orElseThrow(() -> new IllegalStateException(
                                String.format("Submission Category with ID = %d cannot be found.", id))));
        submission.setCategories(categories);

        try {
            submissionService.saveSubmission(submission);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            model.addAttribute("validationErrors", e.getConstraintViolations());
            model.addAttribute("submissionForm", form);
            return "file/view";
        } catch (Exception e) {
			// TODO: handle exception
        	throw e;
		}

        model.addAttribute("message", "Mass spectra are submitted successfully.");
        return "redirect:/submission/" + submission.getId() + "/";
    }

    @RequestMapping(value = "/submission/{submissionId:\\d+}/delete/")
    public String delete(@PathVariable("submissionId") long id) {
        submissionService.delete(id);
        return "redirect:/account/";
    }

    private String redirectFileUpload() {
        return "redirect:/file/upload/";
    }

    private String submissionNotFound(Model model, long submissionId) {
        model.addAttribute("errorMessage", "Cannot find submission ID = " + submissionId);
        return "/notfound/";
    }


    public static class SubmissionForm {

        @NotBlank(message = "The field Name is required.")
        private String name;

        private String description;

        private String tags;

        private List<Long> submissionCategoryIds;

        private Map<SubmissionCategoryType, List<SubmissionCategory>> categoryMap;

        private List<String> availableTags;

        public void setCategoryMap(List<SubmissionCategory> categories) {

            this.categoryMap = Arrays.stream(SubmissionCategoryType.values())
                    .collect(Collectors
                            .toMap(t -> t, t -> new ArrayList<>()));

            categories.forEach(
                    category -> this.categoryMap
                            .get(category.getCategoryType())
                            .add(category));
        }

        public void setAvailableTags(List<String> availableTags) {
            this.availableTags = availableTags;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }

        public List<Long> getSubmissionCategoryIds() {
            return submissionCategoryIds;
        }

        public void setSubmissionCategoryIds(List<Long> submissionCategoryIds) {
            this.submissionCategoryIds = submissionCategoryIds;
        }

        public SubmissionCategoryType[] getSubmissionCategoryTypes() {
            return SubmissionCategoryType.values();
        }

        public List<SubmissionCategory> getSubmissionCategories(SubmissionCategoryType type) {
            return categoryMap.get(type);
        }

        public List<String> getAvailableTags() {
            return availableTags;
        }
    }
}
