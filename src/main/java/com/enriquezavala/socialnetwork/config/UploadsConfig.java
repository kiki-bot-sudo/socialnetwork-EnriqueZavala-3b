package com.enriquezavala.socialnetwork.config;

import com.enriquezavala.socialnetwork.persistence.storage.LocalImageStorage;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

/** Sirve las imágenes subidas en /uploads/**. */
@Configuration
public class UploadsConfig implements WebMvcConfigurer {

    private final LocalImageStorage storage;

    public UploadsConfig(LocalImageStorage storage) {
        this.storage = storage;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(LocalImageStorage.PUBLIC_PREFIX + "**")
                .addResourceLocations(storage.getDirectory().toUri().toString())
                // Los nombres son UUID únicos, así que la imagen nunca cambia.
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic());
    }
}
