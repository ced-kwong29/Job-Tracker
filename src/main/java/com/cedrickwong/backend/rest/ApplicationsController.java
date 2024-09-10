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
import java.util.Objects;
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
        return companyService.save(new Company(name));
    }

    private Job createJob(Company company, String title, Type type) {
        return jobService.save(new Job(company, title, type));
    }

    private Job findJob(Company company, String jobTitle, Type type) {
        return jobService.getByCompanyTitleType(company, jobTitle, type)
                            .orElseGet(() -> createJob(company, jobTitle, type));
    }

    private Job findCompanyAndJob(String companyName, String jobTitle, Type type) {
        return companyService.getByName(companyName)
                                .map(company -> findJob(company, jobTitle, type))
                                .orElseGet(() -> createJob(createCompany(companyName), jobTitle, type));
    }

    @PostMapping(path = "/create")
    public ResponseEntity<JsonObject> create(@RequestParam String companyName, @RequestParam String jobTitle, @RequestParam(required = false) Type type, @RequestParam(required = false) String date, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if (companyName.isEmpty() || jobTitle.isEmpty()) {
            return super.getErrorResponse("Company name and job title can not be empty strings");
        }

        Application application = new Application(user, findCompanyAndJob(companyName, jobTitle, Objects.requireNonNullElse(type, Type.UNKNOWN)), parseDateString(date, LocalDate.now()), Objects.requireNonNullElse(status, Status.WAITING));
        applicationService.save(application);

        return super.actionOkResponse("creation", application);
    }


    private Optional<Application> checkApplicationIDAndUserCredentials(Long id, User user) {
        return applicationService.getById(id).filter(application -> user.equals(application.getUser()));
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

//    private Type defaultType(Type type, Type defaultValue) {
//        return type != null ? type : defaultValue;
//    }


    @PostMapping(path = "/{id}/update")
    public ResponseEntity<JsonObject> update(@PathVariable Long id, @RequestParam(required = false) String companyName, @RequestParam(required = false) String jobTitle, @RequestParam(required = false) Type type, @RequestParam(required = false) String date, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        return checkApplicationIDAndUserCredentials(id, user)
                .map(application -> {
                                        if (companyName != null && companyName.isEmpty() || jobTitle != null && jobTitle.isEmpty()) {
                                            return super.getErrorResponse("Company name and job title can not be empty strings");
                                        }

                                        Job updatedJob, currentJob = application.getJob();

                                        if (jobTitle != null && companyName != null){
                                            updatedJob = findCompanyAndJob(companyName, jobTitle, Objects.requireNonNullElse(type, currentJob.getType()));
                                        } else if (jobTitle != null) {
                                            updatedJob = findCompanyAndJob(currentJob.getCompany().getName(), jobTitle, Objects.requireNonNullElse(type, currentJob.getType()));
                                        } else if (companyName != null) {
                                            updatedJob = findCompanyAndJob(companyName, currentJob.getTitle(), Objects.requireNonNullElse(type, currentJob.getType()));
                                        } else {
                                            updatedJob = type == null ? null : findCompanyAndJob(currentJob.getCompany().getName(), currentJob.getTitle(), type);
                                        }

                                        applicationService.update(application, updatedJob, parseDateString(date, null), status);
                                        return super.actionOkResponse("update", application);
                })
                .orElseGet(() -> invalidApplicationID(id));
    }
}