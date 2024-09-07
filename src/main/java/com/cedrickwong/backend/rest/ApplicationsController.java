package com.cedrickwong.backend.rest;

import com.cedrickwong.backend.model.Application;
import com.cedrickwong.backend.model.Application.Status;
import com.cedrickwong.backend.service.ApplicationService;
import com.cedrickwong.backend.model.Company;
import com.cedrickwong.backend.service.CompanyService;
import com.cedrickwong.backend.model.Job;
import com.cedrickwong.backend.model.Job.Type;
import com.cedrickwong.backend.service.JobService;
import com.cedrickwong.backend.model.User;

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

    @GetMapping("/list")
    public ResponseEntity<JsonObject> getAllFromUser(@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = false) Status status, @RequestParam(required = false) Type type, @RequestParam(required = false) String companyName, @RequestParam(required = false) String jobTitle) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if ((startDate == null && endDate != null) || (startDate != null && endDate == null) || (startDate != null && startDate.isEmpty()) || (endDate != null && endDate.isEmpty())) {
            return super.getErrorResponse("Provide either both start and end dates or none");
        }

        return super.actionOkResponse("search query", applicationService.getAllByUser(user, parseDateString(startDate, null), parseDateString(endDate, null), companyName, jobTitle, status, type));
    }

    private ResponseEntity<JsonObject> invalidApplicationID(Long id) {
        return super.getErrorResponse("Invalid application id: " + id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonObject> get(@PathVariable Long id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        Optional<Application> applicationSearch = applicationService.getById(id);
        if (applicationSearch.isEmpty()) {
            return invalidApplicationID(id);
        }

        Application application = applicationSearch.get();
        return user.equals(application.getUser()) ? super.actionOkResponse("search query", application) : invalidApplicationID(id);
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

    private Job findOrCreateCompanyAndJob(String companyName, String jobTitle, Type type) {
        return companyService.getByName(companyName)
                                .map(company -> jobService.getFromCompanyByTitle(company, jobTitle)
                                                            .orElseGet(() -> createJob(company, jobTitle, type))
                                )
                                .orElseGet(() -> createJob(createCompany(companyName), jobTitle, type));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<JsonObject> create(@RequestParam String companyName, @RequestParam String jobTitle, @RequestParam(required = false) String date, @RequestParam(required = false) Type type, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if (companyName.isEmpty() || jobTitle.isEmpty()) {
            return super.getErrorResponse("Company name and job title can not be empty string");
        }

        Application application = new Application(user, findOrCreateCompanyAndJob(companyName, jobTitle, type), parseDateString(date, LocalDate.now()), status == null ? Status.WAITING : status);
        applicationService.save(application);

        return super.actionOkResponse("creation", application);
    }

    @PostMapping(path = "/{id}/delete")
    public ResponseEntity<JsonObject> delete(@PathVariable Long id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        return checkApplicationIDAndUserCredentials(id, user)
                .map(application -> {
                                        applicationService.delete(application);
                                        return super.actionOkResponse("deletion", application);
                })
                .orElseGet(() -> invalidApplicationID(id));
    }

    private Optional<Application> checkApplicationIDAndUserCredentials(Long id, User user) {
        return applicationService.getById(id).filter(application -> user.equals(application.getUser()));
    }

    @PostMapping(path = "/{id}/update")
    public ResponseEntity<JsonObject> update(@PathVariable Long id, @RequestParam(required = false) String companyName, @RequestParam(required = false) String jobTitle, @RequestParam(required = false) Type type, @RequestParam(required = false) String date, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        return checkApplicationIDAndUserCredentials(id, user)
                .map(application -> {
                                        if (companyName != null && companyName.isEmpty()) {
                                            return super.getErrorResponse("Company name can not be empty string");
                                        }
                                        if (jobTitle != null && jobTitle.isEmpty()) {
                                            return super.getErrorResponse("Job title can not be empty string");
                                        }

                                        Job applicationJob, updatedJobByTitle;
                                        Company updatedCompanyByName;

                                        if (jobTitle == null) {
                                            updatedCompanyByName = companyName == null ? null : companyService.getByName(companyName)
                                                                                                                .orElseGet(() -> createCompany(companyName));
                                            applicationJob = application.getJob();
                                            updatedJobByTitle = null;
                                        } else {
                                            Company queriedCompany = companyName == null ? application.getJob().getCompany() : companyService.getByName(companyName)
                                                                                                                                                .orElseGet(() -> createCompany(companyName));
                                            updatedCompanyByName = queriedCompany.equals(application.getJob().getCompany()) ? null : queriedCompany;
                                            applicationJob = jobService.getFromCompanyByTitle(queriedCompany, jobTitle)
                                                                        .orElseGet(application::getJob);
                                            updatedJobByTitle = applicationJob.equals(application.getJob()) ? null : applicationJob;
                                        }

                                        jobService.update(applicationJob, updatedCompanyByName, type, jobTitle);
                                        applicationService.update(application, updatedJobByTitle, parseDateString(date, null), status);

                                        return super.actionOkResponse("update", application);
                })
                .orElseGet(() -> invalidApplicationID(id));
    }
}