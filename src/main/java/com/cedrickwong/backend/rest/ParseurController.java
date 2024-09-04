package com.cedrickwong.backend.rest;

import com.cedrickwong.backend.service.ParseurService;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/parseur")
public class ParseurController extends BaseController {

    private final ParseurService parseurService;
    private final HttpSession httpSession;

    @Autowired
    public ParseurController(ParseurService parseurService, HttpSession httpSession, Gson gson) {
        super(gson);
        this.parseurService = parseurService;
        this.httpSession = httpSession;
    }


}
