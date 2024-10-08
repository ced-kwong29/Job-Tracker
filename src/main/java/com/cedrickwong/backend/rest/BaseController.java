package com.cedrickwong.backend.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class BaseController {

    protected final Gson gson;

    @Autowired
    public BaseController(Gson gson) {
        this.gson = gson;
    }

    @GetMapping(path = "/ping")
    public ResponseEntity<JsonObject> ping() {
        return getOkResponse(true, "You have pinged the API!");
    }

    private JsonObject getOkJsonObject(boolean success, String message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", success ? "success" : "fail");
        jsonObject.addProperty("message", message);
        return jsonObject;
    }

    private <T> JsonObject getOkJsonObject(String message, List<T> itemList) {
        JsonObject jsonObject = getOkJsonObject(true, message);
        jsonObject.addProperty("size", itemList.size());

        JsonArray jsonArray = new JsonArray();
        for (T item : itemList) {
            jsonArray.add(gson.toJsonTree(item));
        }
        jsonObject.add("items", jsonArray);

        return jsonObject;
    }

    private ResponseEntity<JsonObject> getOkJsonObjectResponse(JsonObject jsonObject) {
        return ResponseEntity.ok(jsonObject);
    }

    protected ResponseEntity<JsonObject> getOkResponse(boolean success, String message) {
        return getOkJsonObjectResponse(getOkJsonObject(success, message));
    }

    protected <T> ResponseEntity<JsonObject> getItemizedOkResponse(String message, List<T> itemList) {
        return getOkJsonObjectResponse(getOkJsonObject(message, itemList));
    }

    protected ResponseEntity<JsonObject> actionOkResponse(String action) {
        return getOkResponse(true, "Successful " + action);
    }

    protected <T> ResponseEntity<JsonObject> actionOkResponse(String action, T item) {
        return getItemizedOkResponse("Successful " + action, List.of(item));
    }

    protected <T> ResponseEntity<JsonObject> actionOkResponse(String action, List<T> itemList) {
        return getItemizedOkResponse("Successful " + action, itemList);
    }

    private JsonObject getErrorJsonObject(String message) {
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
}
