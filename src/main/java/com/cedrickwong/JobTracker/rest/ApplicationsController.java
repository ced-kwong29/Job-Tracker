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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
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

    private LocalDate parseDateString(String date) {
        return date == null ? null : LocalDate.parse(date);
    }

    @GetMapping("/apps")
    public ResponseEntity<JsonObject> getAllFromUser(@RequestParam(required = false) String start, @RequestParam(required = false) String end, @RequestParam(required = false) Status status, @RequestParam(required = false) Type type, @RequestParam(required = false) String companyName, @RequestParam(required = false) String jobTitle) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if ((start == null && end != null) || (start != null && end == null) || (start != null && start.isEmpty()) || (end != null && end.isEmpty())) {
            return super.getErrorResponse("Provide either both start and end dates or none");
        }

        return super.searchOkResponse(applicationService.getAllByUser(user, parseDateString(start), parseDateString(end), companyName, jobTitle, status, type));
    }


    @GetMapping("/app")
    public ResponseEntity<JsonObject> get(@RequestParam Long id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        if (id == null) {
            return super.getErrorResponse("Provide application id");
        }
        Optional<Application> applicationSearch = applicationService.getById(id);
        if (applicationSearch.isEmpty()) {
            return super.getErrorResponse("Invalid application id");
        }

        Application application = applicationSearch.get();
        return user.equals(application.getUser()) ? super.searchOkResponse(application) : super.getErrorResponse("Unauthorized access to application");
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
        System.out.println(job.getCompany());
        Application application = new Application(user, job, date == null ? LocalDate.now() : LocalDate.parse(date), status == null ? Status.WAITING : status);
        applicationService.save(application);

        return super.actionOkResponse("creation", application);
    }

    private ResponseEntity<JsonObject> deleteApplication(Long id, User user) {
        if (id == null) {
            return super.getErrorResponse("Provide application id");
        }

        return applicationService.getById(id).filter(application -> user.equals(application.getUser()))
                                            .map(application -> {
                                                applicationService.delete(application);
                                                return super.actionOkResponse("deletion", application);})
                                            .orElseGet(() -> super.getErrorResponse("Invalid application id"));
    }

    private ResponseEntity<JsonObject> updateApplication(Long id, User user, Status status) {
        if (id == null || status == null) {
            return super.getErrorResponse("Provide application id and status");
        }

        return applicationService.getById(id).filter(application -> user.equals(application.getUser()))
                                            .map(application -> {
                                                applicationService.update(application, status);
                                                return super.actionOkResponse("update",application);})
                                            .orElseGet(() -> super.getErrorResponse("Invalid application id"));
    }
}
