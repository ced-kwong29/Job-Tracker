package com.cedrickwong.JobTracker.configuration;

import com.cedrickwong.JobTracker.adapter.LocalDateAdapter;
import com.google.gson.Gson;

import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.time.LocalDate;

@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder().setPrettyPrinting()
                                .serializeNulls()
                                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                                .create();
    }

    @Bean
    public GsonHttpMessageConverter gsonHttpMessageConverter(Gson gson) {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter(gson);
        converter.setGson(gson);
        return converter;
    }
}
