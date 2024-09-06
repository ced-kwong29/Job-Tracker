package com.cedrickwong.backend.rest;

import com.cedrickwong.backend.model.Application;
import com.cedrickwong.backend.model.Application.Status;
import com.cedrickwong.backend.model.Company;
import com.cedrickwong.backend.model.Job;
import com.cedrickwong.backend.model.User;
import com.cedrickwong.backend.service.ApplicationService;
import com.cedrickwong.backend.service.CompanyService;
import com.cedrickwong.backend.service.JobService;
import com.cedrickwong.backend.service.ParseurService;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/api/parseur")
public class ParseurController extends BaseController {

    private final ApplicationService applicationService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final ParseurService parseurService;
    private final HttpSession httpSession;

    @Autowired
    public ParseurController(ApplicationService applicationService,CompanyService companyService, JobService jobService, ParseurService parseurService, HttpSession httpSession, Gson gson) {
        super(gson);
        this.applicationService = applicationService;
        this.companyService = companyService;
        this.jobService = jobService;
        this.parseurService = parseurService;
        this.httpSession = httpSession;
    }

    private Company createCompany(String name) {
        Company company = new Company(name);
        companyService.save(company);
        return company;
    }

    private Job createJob(Company company, String title) {
        Job job = new Job(company, title, null);
        jobService.save(job);
        return job;
    }

    private Status processParsedStatus(JsonElement status) {
        if (status != null) {
            String parsedStatus = status.getAsString().toLowerCase();
            if (parsedStatus.contains("interview")) {
                return Status.INTERVIEWING;
            }
            if (parsedStatus.contains("offer")) {
                return Status.OFFERED;
            }
            if (parsedStatus.contains("assessment")) {
                return Status.ASSESSMENT;
            }
            if (parsedStatus.contains("accept")) {
                return Status.ACCEPTED;
            }
        }

        return Status.WAITING;
    }

    @GetMapping(path = "/mailbox/{id}/parsed-applications")
    public ResponseEntity<JsonObject> getDocumentsFromMailbox(@PathVariable String id) {
        User user = (User) httpSession.getAttribute("user");
        if (user == null) {
            return super.notLoggedInErrorResponse();
        }

        try {
            List<Application> parsedApplications = new ArrayList<>();

            parseurService.getDocuments(id).forEach(jsonElement -> {
                JsonObject jsonObject = JsonParser.parseString(jsonElement.getAsJsonObject().get("result").getAsString())
                                                    .getAsJsonObject();

                String jobTitle = jsonObject.get("JobRole").getAsString();
                String companyName = jsonObject.get("JobCompany").getAsString();

                Job job = companyService.getByName(companyName)
                                        .map(company -> jobService.getFromCompanyByTitle(company, jobTitle)
                                                                    .orElseGet(() -> createJob(company, jobTitle)))
                                        .orElseGet(() -> createJob(createCompany(companyName), jobTitle));

                LocalDate date = Instant.parse(jsonObject.get("Received").getAsString())
                                                            .atZone(ZoneId.systemDefault())
                                                            .toLocalDate();

                Status status = processParsedStatus(jsonObject.get("Status"));

                Application application = new Application(user, job, date, status);
                applicationService.save(application);

                parsedApplications.add(application);
            });

            return super.actionOkResponse("parsed-application creation", parsedApplications);
        } catch (Exception e) {
            return super.getErrorResponse(e.getMessage());
        }
    }
}
