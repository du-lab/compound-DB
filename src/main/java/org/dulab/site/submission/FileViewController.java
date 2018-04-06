package org.dulab.site.submission;

import org.dulab.site.models.Submission;
import org.dulab.site.validation.ContainsSubmission;
import org.dulab.site.validation.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class FileViewController {

    @RequestMapping(value = "/file/view", method = RequestMethod.GET)
    public String view(@ContainsSubmission HttpSession session, Model model) {
        model.addAttribute("form", new Form());
        return "file/view";
    }

    @RequestMapping(value = "/file/raw/view", method = RequestMethod.GET)
    public void rawView(@ContainsSubmission HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.getSubmission(session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "inline; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }

    @RequestMapping(value = "/file/raw/download", method = RequestMethod.GET)
    public void rawDownload(@ContainsSubmission HttpSession session, HttpServletResponse response) throws IOException {

        Submission submission = Submission.getSubmission(session);
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + submission.getFilename() + "\"");
        response.getOutputStream().write(submission.getFile());
    }


    public static class Form {

        @NotBlank(message = "The field Name is required.")
        private String name;

        @NotBlank(message = "The field Description is required.")
        private String description;

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
    }
}
