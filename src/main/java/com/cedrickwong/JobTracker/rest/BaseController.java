package com.cedrickwong.JobTracker.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping(path = "/api")
public class BaseController {

    private final Gson gson;

    @Autowired
    public BaseController(Gson gson) {
        this.gson = gson;
    }

    @GetMapping(path = "/ping")
    public ResponseEntity<JsonObject> ping() {
        return getOkResponse(true, "You have pinged the API!");
    }

    protected JsonObject getOkJsonObject(boolean success, String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", success ? "success" : "fail");
        jsonObject.addProperty("message", message);
        return jsonObject;
    }

    protected JsonObject getOkJsonObject(String message, Object entity) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", "success");
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("entity", gson.toJson(entity));
        return jsonObject;
    }

    protected ResponseEntity<JsonObject> getOkResponse(boolean success, String message) {
        return ResponseEntity.ok(getOkJsonObject(success, message));
    }

    protected ResponseEntity<JsonObject> getOkResponse(String message, Object entity) {
        return ResponseEntity.ok(getOkJsonObject(message, entity));
    }

    protected <T> JsonArray getOkJsonArray(List<T> entityList, Function<T, JsonObject> jsonObjectConversion) {
        JsonArray jsonArray = new JsonArray();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("size", entityList.size());
        jsonArray.add(jsonObject);

        for (T entity : entityList) {
            jsonArray.add(jsonObjectConversion.apply(entity));
        }

        return jsonArray;
    }

    protected <T> ResponseEntity<JsonArray> getOkResponse(List<T> entityList, Function<T, JsonObject> jsonObjectConversion) {
        return ResponseEntity.ok(getOkJsonArray(entityList, jsonObjectConversion));
    }

    protected ResponseEntity<JsonObject> getOkResponse(JsonObject jsonObject) {
        return ResponseEntity.ok(jsonObject);
    }

    protected ResponseEntity<JsonObject> missingUserCredentialsOkResponse() {
        return getOkResponse(false, "Provide email and password");
    }

    protected ResponseEntity<JsonObject> invalidUserCredentialsOkResponse() {
        return getOkResponse(false, "Invalid email or password");
    }

    protected ResponseEntity<JsonObject> actionOkResponse(String action) {
        return getOkResponse(true, "Successful " + action);
    }

    protected ResponseEntity<JsonObject> actionOkResponse(String action, Object entity) {
        return getOkResponse("Successful " + action, entity);
    }

    protected JsonObject getErrorJsonObject(String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", "error");
        jsonObject.addProperty("message", message);
        return jsonObject;
    }

    protected ResponseEntity<JsonObject> getErrorResponse(String message) {
        return ResponseEntity.badRequest().body(getErrorJsonObject(message));
    }

    protected ResponseEntity<JsonObject> notLoggedInErrorResponse() {
        return getErrorResponse("User not logged in");
    }

    protected ResponseEntity<JsonObject> invalidUserCredentialsErrorResponse(){
        return getErrorResponse("Invalid email or password");
    }
}
