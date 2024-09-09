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
                                .map(company -> jobService.getByCompanyTitleType(company, jobTitle, type)
                                                            .orElseGet(() -> createJob(company, jobTitle, type)))
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

        Application application = new Application(user, findOrCreateCompanyAndJob(companyName, jobTitle, type == null ? Type.UNKNOWN : type), parseDateString(date, LocalDate.now()), status == null ? Status.WAITING : status);
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
                                        if (companyName != null && companyName.isEmpty() || jobTitle != null && jobTitle.isEmpty()) {
                                            return super.getErrorResponse("Company name and job title can not be empty strings");
                                        }

                                        Job currentJob = application.getJob();
                                        Company currentCompany = currentJob.getCompany();

                                        Job newJob;

                                        if (jobTitle != null && companyName != null){
                                            Type newType = type != null ? type : currentJob.getType();
                                            // find a company with a matching name
                                            Company company = companyService.getByName(companyName)
                                                                            .orElseGet(() -> {
                                                                                                // edit the company name if it is associated with just one application in the database
                                                                                                // otherwise create a new company
                                                                                                if (applicationService.getCountByCompanyAndJobTitle(currentCompany, null) == 1) {
                                                                                                    companyService.update(currentCompany, companyName);
                                                                                                    return currentCompany;
                                                                                                }
                                                                                                return createCompany(companyName);
                                                                            });

                                            if (company.equals(currentCompany)) {
                                                // edit the job title if the current company was just renamed
                                                jobService.update(currentJob, null, type, jobTitle);
                                                newJob = null;
                                            } else {
                                                newJob = jobService.getByCompanyTitleType(company, jobTitle, newType)
                                                                    .orElseGet(() -> createJob(company, jobTitle, newType));
                                            }



                                        } else if (jobTitle != null) {
                                            Type jobType = type != null ? type : currentJob.getType();

                                            newJob = jobService.getByCompanyTitleType(currentCompany, jobTitle, jobType)
                                                                .map(foundJob -> {
                                                                                    if (applicationService.getCountByCompanyAndJobTitle(currentCompany, currentJob.getTitle()) == 0) {
                                                                                        jobService.update(currentJob, null, type, jobTitle);
                                                                                        return null;
                                                                                    }
                                                                                    return foundJob;
                                                                })
                                                                .orElseGet(() -> createJob(currentCompany, jobTitle, jobType));

                                        } else if (companyName != null) {
                                            String currentTitle = currentJob.getTitle();
                                            Type jobType = type != null ? type : currentJob.getType();
                                            Company newCompany = companyService.getByName(companyName)
                                                                            .map(foundCompany -> {
                                                                                                    if (applicationService.getCountByCompanyAndJobTitle(currentCompany, currentTitle) <= 1) {
                                                                                                        currentCompany.setName(companyName);
                                                                                                        return currentCompany;
                                                                                                    }
                                                                                                    return foundCompany;
                                                                            })
                                                                            .orElseGet(() -> createCompany(companyName));

                                            newJob = jobService.getByCompanyTitleType(newCompany, currentTitle, jobType)
                                                            .orElseGet(() -> createJob(newCompany, currentTitle, jobType));
                                        } else {
                                            if (type != null) {
                                                String title = currentJob.getTitle();
                                                Company company = currentJob.getCompany();

                                                newJob = jobService.getByCompanyTitleType(company, title, type)
                                                                .orElseGet(() -> createJob(company, title, type));
                                            } else {
                                                newJob = null;
                                            }
                                        }
                                        applicationService.update(application, newJob, parseDateString(date, null), status);
                                        return super.actionOkResponse("update", application);
                })
                .orElseGet(() -> invalidApplicationID(id));
    }
}