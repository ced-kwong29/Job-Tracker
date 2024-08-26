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

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/applications")
public class ApplicationsController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final HttpSession httpSession;

    @Autowired
    public ApplicationsController(ApplicationService applicationService, CompanyService companyService, JobService jobService, HttpSession httpSession) {
        this.applicationService = applicationService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.httpSession = httpSession;
    }

    @GetMapping("/apps")
    public ResponseEntity<?> getAllFromUser(@RequestParam(required = false) String start, @RequestParam(required = false) String end, @RequestParam(required = false) Status status, @RequestParam(required = false) Type type, @RequestParam(required = false) String companyName, @RequestParam(required = false) String jobTitle) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }

        if ((start == null && end != null) || (start != null && end == null) || (start != null && start.isEmpty()) || (end != null && end.isEmpty())) {
            return ResponseEntity.badRequest().body("Provide both start and end dates");
        }

        return ResponseEntity.ok(applicationService.getAllByUser(user, start == null ? null : LocalDate.parse(start), end == null ? null : LocalDate.parse(end), companyName, jobTitle, status, type));
    }

    @GetMapping("/app")
    public ResponseEntity<?> get(@RequestParam Long id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }
        if (id == null) {
            return ResponseEntity.badRequest().body("Provide application id");
        }
        Optional<Application> applicationSearch = applicationService.getById(id);
        if (applicationSearch.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid application id");
        }
        Application application = applicationSearch.get();
        return ResponseEntity.ok(!user.equals(application.getUser()) ? "Unauthorized" : application);
    }

    @PostMapping("/app/create")
    public ResponseEntity<String> create(@RequestParam String companyName, @RequestParam String jobTitle, @RequestParam Type type, @RequestParam(required = false) String date, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }
        if (companyName == null || companyName.isEmpty() || jobTitle == null || jobTitle.isEmpty()) {
            return ResponseEntity.badRequest().body("Provide company name and job title");
        }
        if (status == null) {
            return ResponseEntity.badRequest().body("Provide valid status");
        }

        Optional<Company> companySearch = companyService.getByName(companyName);
        Job job = companySearch.map(company -> jobService.getFromCompanyByTitle(company, jobTitle).orElseGet(() -> createJob(company, jobTitle, type)))
                            .orElseGet(() -> createJob(createCompany(companyName), jobTitle, type));

        Application application = new Application(user, job, date == null ? LocalDate.now() : LocalDate.parse(date), status == null ? Status.WAITING : status);
        applicationService.save(application);
        return ResponseEntity.ok("Successfully created:\n" + application);
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

    @PostMapping("/app/{interact}")
    public ResponseEntity<String> interact(@PathVariable String interact, @RequestParam(required = false) Long id, @RequestParam(required = false) Status status) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return ResponseEntity.badRequest().body("User is not logged in");
        }

        return switch (interact) {
                case "delete" -> deleteApplication(id, user);
                case "update" -> updateApplication(id, user, status);
                default -> ResponseEntity.badRequest().build();
        };
    }

    private String responseMessage(Optional<Application> applicationSearch, User user) {
        return applicationSearch.map(application -> user.equals(application.getUser()) ? "" : "Unauthorized access to application").orElse("Invalid application id");
    }

    private ResponseEntity<String> deleteApplication(Long id, User user) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Provide application id");
        }
        Optional<Application> applicationSearch = applicationService.getById(id);
        String responseMessage = responseMessage(applicationSearch, user);
        if (!responseMessage.isEmpty()) {
            return ResponseEntity.badRequest().body(responseMessage);
        }

        Application application = applicationSearch.get();
        applicationService.delete(application);
        return ResponseEntity.ok("Successfully deleted:\n" + application);
    }

    private ResponseEntity<String> updateApplication(Long id, User user, Status status) {
        if (id == null || status == null) {
            return ResponseEntity.badRequest().body("Provide application id and valid status");
        }
        Optional<Application> applicationSearch = applicationService.getById(id);
        String responseMessage = responseMessage(applicationSearch, user);
        if (!responseMessage.isEmpty()) {
            return ResponseEntity.badRequest().body(responseMessage);
        }
        Application application = applicationSearch.get();
        applicationService.update(application, status);
        return ResponseEntity.ok("Successfully updated:\n" + application);
    }
}
