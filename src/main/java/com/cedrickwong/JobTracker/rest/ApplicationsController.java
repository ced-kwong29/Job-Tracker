package com.cedrickwong.JobTracker.rest;

import com.cedrickwong.JobTracker.model.Application;
import com.cedrickwong.JobTracker.model.Application.Status;
import com.cedrickwong.JobTracker.service.ApplicationService;
import com.cedrickwong.JobTracker.model.Company;
import com.cedrickwong.JobTracker.service.CompanyService;
import com.cedrickwong.JobTracker.model.Job;
import com.cedrickwong.JobTracker.model.Job.Type;
import com.cedrickwong.JobTracker.service.JobService;
import com.cedrickwong.JobTracker.model.User;

import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/applications")
public class ApplicationsController extends BaseController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final HttpSession httpSession;

    @Autowired
    public ApplicationsController(ApplicationService applicationService, CompanyService companyService, JobService jobService, HttpSession httpSession, Gson gson) {
        super(gson);
        this.applicationService = applicationService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.httpSession = httpSession;
    }

    private LocalDate parseDateString(String date, LocalDate defaultValue) {
        return date == null ? defaultValue : LocalDate.parse(date);
    }

    @GetMapping("/apps")
    public ResponseEntity<JsonObject> getAllFromUser(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) Status status, @RequestParam(required = false) Type type, @RequestParam(required = false) String companyName, @RequestParam(required = false) String jobTitle) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if ((startDate == null && endDate != null) || (startDate != null && endDate == null) || (startDate != null && startDate.isEmpty()) || (endDate != null && endDate.isEmpty())) {
            return super.getErrorResponse("Provide either both start and end dates or none");
        }

        return super.searchOkResponse(applicationService.getAllByUser(user, parseDateString(startDate, null), parseDateString(endDate, null), companyName, jobTitle, status, type));
    }

    private ResponseEntity<JsonObject> missingOrInvalidApplicationID(Long id) {
        return super.getErrorResponse(id == null ? "Provide application id" : "Invalid application id: " + id);
    }

    @GetMapping("/app")
    public ResponseEntity<JsonObject> get(@RequestParam Long id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if (id == null) {
            return missingOrInvalidApplicationID(null);
        }

        Optional<Application> applicationSearch = applicationService.getById(id);
        if (applicationSearch.isEmpty()) {
            return missingOrInvalidApplicationID(id);
        }

        Application application = applicationSearch.get();
        return user.equals(application.getUser()) ? super.searchOkResponse(application) : missingOrInvalidApplicationID(id);
    }

    private Company createCompany(String name) {
        Company company = new Company(name);
        companyService.save(company);
        return company;
    }

    private Job createJob(Company company, String title, Type type) {
        Job job = new Job(company, title, type);
        jobService.save(job);
        return job;
    }

    @PostMapping("/app/{action}")
    public ResponseEntity<JsonObject> action(@PathVariable String action, @RequestParam(required = false) String companyName, @RequestParam(required = false) String jobTitle, @RequestParam(required = false) Type type, @RequestParam(required = false) String date, @RequestParam(required = false) Status status, @RequestParam(required = false) Long id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        return switch (action) {
                case "create" -> createApplication(user, companyName, jobTitle, type, date, status);
                case "delete" -> deleteApplication(id, user);
                case "update" -> updateApplication(id, user, status);
                default -> ResponseEntity.badRequest().build();
        };
    }

    private ResponseEntity<JsonObject> createApplication(User user, String companyName, String jobTitle, Type type, String date, Status status) {
        if (companyName == null || companyName.isEmpty() || jobTitle == null || jobTitle.isEmpty() || type == null) {
            return super.getErrorResponse("Provide company name and job title");
        }

        Job job = companyService.getByName(companyName).map(company -> jobService.getFromCompanyByTitle(company, jobTitle)
                                                                                .orElseGet(() -> createJob(company, jobTitle, type)))
                                .orElseGet(() -> createJob(createCompany(companyName), jobTitle, type));

        Application application = new Application(user, job, parseDateString(date, LocalDate.now()), status == null ? Status.WAITING : status);
        applicationService.save(application);

        return super.actionOkResponse("creation", application);
    }

    private Optional<Application> checkApplicationIDAndUserCredentials(Long id,  User user) {
        return applicationService.getById(id).filter(application -> user.equals(application.getUser()));
    }

    private ResponseEntity<JsonObject> deleteApplication(Long id, User user) {
        if (id == null) {
            return missingOrInvalidApplicationID(null);
        }

        return checkApplicationIDAndUserCredentials(id, user)
                .map(application -> {
                    applicationService.delete(application);
                    return super.actionOkResponse("deletion", application);
                })
                .orElseGet(() -> missingOrInvalidApplicationID(id));
    }

    private ResponseEntity<JsonObject> updateApplication(Long id, User user, Status status) {
        if (id == null || status == null) {
            return super.getErrorResponse("Provide application id and status");
        }

        return checkApplicationIDAndUserCredentials(id, user)
                .map(application -> {
                    applicationService.update(application, status);
                    return super.actionOkResponse("update", application);
                })
                .orElseGet(() -> missingOrInvalidApplicationID(id));
    }
}
